package _experimental.a;

import common.datastore.BlockCounter;
import common.datastore.Pair;
import common.datastore.action.Action;
import common.datastore.order.Order;
import common.datastore.pieces.Pieces;
import common.pattern.PiecesGenerator;
import common.tree.AnalyzeTree;
import concurrent.LockedCandidateThreadLocal;
import concurrent.checker.CheckerUsingHoldThreadLocal;
import concurrent.checker.invoker.using_hold.ConcurrentCheckerUsingHoldInvoker;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.field.FieldView;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import lib.MyFiles;
import searcher.common.validator.PerfectValidator;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PutterMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        MinoFactory minoFactory = new MinoFactory();
        PerfectValidator validator = new PerfectValidator();
        PutterUsingHold<Action> putter = new PutterUsingHold<>(minoFactory, validator);

        PiecesGenerator generator = new PiecesGenerator("*p4");
        Set<BlockCounter> blockCounters = generator.stream()
                .map(pieces -> new BlockCounter(pieces.getBlockStream()))
                .collect(Collectors.toSet());

        int maxClearLine = 4;
        int maxDepth = 10;
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedCandidate candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);

        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(core);
        CheckerUsingHoldThreadLocal<Action> checkerThreadLocal = new CheckerUsingHoldThreadLocal<>();
        LockedCandidateThreadLocal candidateThreadLocal = new LockedCandidateThreadLocal(maxClearLine);
        ConcurrentCheckerUsingHoldInvoker invoker = new ConcurrentCheckerUsingHoldInvoker(executorService, candidateThreadLocal, checkerThreadLocal);

        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        List<List<Block>> searchingPieces = piecesGenerator.stream()
                .map(Pieces::getBlocks)
                .collect(Collectors.toList());

        HashMap<Field, Connect> map = new HashMap<>();
        Comparator<Connect> connectComparator = Comparator.<Connect>comparingDouble(o -> o.percent).reversed();

        for (BlockCounter counter : blockCounters) {
            List<Block> blocks = counter.getBlocks();
            System.out.println(blocks);

            Field initField = FieldFactory.createField("");
            TreeSet<Order> orders = putter.search(initField, blocks, candidate, maxClearLine, maxDepth);
            System.out.println(orders.size());

            ArrayList<Connect> results = new ArrayList<>();

            for (Order order : orders) {
                if (order.getMaxClearLine() < maxClearLine)
                    continue;

                Field field = order.getField();

                Connect connect = map.getOrDefault(field, null);
                if (connect != null) {
                    connect.add();
                    results.add(connect);
                    continue;
                }

                List<Pair<List<Block>, Boolean>> search = invoker.search(field, searchingPieces, maxClearLine, maxDepth);
                AnalyzeTree tree = new AnalyzeTree();
                for (Pair<List<Block>, Boolean> pair : search) {
                    tree.set(pair.getValue(), pair.getKey());
                }

//                System.out.println(FieldView.toString(field));
                double percent = tree.getSuccessPercent();
//                System.out.println(percent);
//                System.out.println("===");

                Connect value = new Connect(field, percent);
                map.put(field, value);
                results.add(value);
            }

            results.sort(connectComparator);

            List<String> lines = results.stream()
                    .filter(connect -> 0.0 < connect.percent)
                    .map(connect -> String.format("%d,%.5f", connect.field.getBoard(0), connect.percent))
                    .collect(Collectors.toList());

            String name = blocks.stream().map(Block::getName).collect(Collectors.joining());
            MyFiles.write("output/cycle2/" + name + ".csv", lines);
        }

        List<Connect> values = new ArrayList<>(map.values());
        values.sort(connectComparator);
        List<String> lines = values.stream()
                .filter(connect -> 0.0 < connect.percent)
                .map(connect -> String.format("%d,%.5f,%d", connect.field.getBoard(0), connect.percent, connect.count))
                .collect(Collectors.toList());

        MyFiles.write("output/cycle2/all.csv", lines);

        executorService.shutdown();
    }

    static class Connect {
        private final Field field;
        private final double percent;
        private int count = 1;

        public Connect(Field field, double percent) {
            this.field = field;
            this.percent = percent;
        }

        public void add() {
            count += 1;
        }
    }
}
