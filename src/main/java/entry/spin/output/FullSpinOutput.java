package entry.spin.output;

import common.datastore.Operation;
import common.datastore.Pair;
import common.datastore.SimpleOperation;
import concurrent.LockedReachableThreadLocal;
import concurrent.RotateReachableThreadLocal;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.KeyOperators;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.neighbor.SimpleOriginalPiece;
import core.srs.MinoRotationDetail;
import core.srs.RotateDirection;
import core.srs.SpinResult;
import entry.path.output.FumenParser;
import entry.path.output.MyFile;
import entry.spin.FilterType;
import exceptions.FinderExecuteException;
import output.HTMLBuilder;
import searcher.spins.SpinCommons;
import searcher.spins.candidates.Candidate;
import searcher.spins.results.Result;
import searcher.spins.spin.Spin;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FullSpinOutput implements SpinOutput {
    private final MinoFactory minoFactory;
    private final MinoRotationDetail minoRotationDetail;
    private final LockedReachableThreadLocal lockedReachableThreadLocal;
    private final Formatter formatter;

    public FullSpinOutput(
            FumenParser fumenParser,
            MinoFactory minoFactory, MinoRotationDetail minoRotationDetail,
            LockedReachableThreadLocal lockedReachableThreadLocal,
            RotateReachableThreadLocal rotateReachableThreadLocal,
            FilterType filterType
    ) {
        this.minoFactory = minoFactory;
        this.minoRotationDetail = minoRotationDetail;
        this.lockedReachableThreadLocal = lockedReachableThreadLocal;
        this.formatter = new Formatter(fumenParser, lockedReachableThreadLocal, rotateReachableThreadLocal, filterType);
    }

    @Override
    public int output(MyFile myFile, List<Candidate> results, Field initField, int fieldHeight) throws FinderExecuteException {
        HTMLBuilder<FullSpinColumn> htmlBuilder = new HTMLBuilder<>("Spin Result");

        // HTML???????????????
        for (Candidate candidate : results) {
            add(htmlBuilder, candidate, initField, fieldHeight);
        }

        int size = htmlBuilder.getSize();
        htmlBuilder.addHeader(String.format("%d solutions", size));

        // ????????????
        try (BufferedWriter writer = myFile.newBufferedWriter()) {
            ArrayList<FullSpinColumn> sorted = new ArrayList<>(htmlBuilder.getRegisteredColumns());
            sorted.sort(Comparator.reverseOrder());

            List<String> lines = htmlBuilder.toList(sorted, true);
            for (String line : lines) {
                writer.write(line);
            }

            writer.flush();
        } catch (Exception e) {
            throw new FinderExecuteException("Failed to output file", e);
        }

        return size;
    }

    private void add(HTMLBuilder<FullSpinColumn> htmlBuilder, Candidate candidate, Field initField, int fieldHeight) {
        LockedReachable lockedReachable = lockedReachableThreadLocal.get();

        // T???????????????????????????????????????
        Result result = candidate.getResult();
        SimpleOriginalPiece operationT = candidate.getOperationT();
        int clearedLineOnlyT = Long.bitCount(result.getAllMergedFilledLine() & operationT.getUsingKey());

        // T??????????????????????????????????????????????????????????????????
        Field freeze = candidate.getAllMergedFieldWithoutT().freeze();
        long filledLineWithoutT = candidate.getAllMergedFilledLineWithoutT();

        assert operationT.getNeedDeletedKey() == 0L || (filledLineWithoutT & operationT.getNeedDeletedKey()) != 0L;
        freeze.clearLine();

        // ???????????????????????????????????????y?????????
        Mino mino = operationT.getMino();
        int y = operationT.getY();
        int slideY = Long.bitCount(filledLineWithoutT & KeyOperators.getMaskForKeyBelowY(y + mino.getMinY()));
        SimpleOperation slideOperation = new SimpleOperation(operationT.getPiece(), operationT.getRotate(), operationT.getX(), y - slideY);

        // ????????????????????????????????????
        Spin maxSpin = null;
        int maxPriority = -1;

        // ?????????, ?????????
        for (RotateDirection direction : RotateDirection.values()) {
            RotateDirection beforeDirection = RotateDirection.reverse(direction);

            Mino before = minoFactory.create(slideOperation.getPiece(), slideOperation.getRotate().get(beforeDirection));
            int[][] patterns = minoRotationDetail.getPatternsFrom(before, direction);

            List<Spin> spins = getSpins(lockedReachable, freeze, slideOperation, before, patterns, direction, fieldHeight, clearedLineOnlyT);
            for (Spin spin : spins) {
                int priority = getSpinPriority(spin);
                if (maxSpin == null || maxPriority < priority) {
                    maxSpin = spin;
                    maxPriority = priority;
                }
            }
        }

        assert maxSpin != null;

        final int finalPriority = maxPriority;

        Optional<Pair<String, Integer>> optional = formatter.get(candidate, initField, fieldHeight);
        if (optional.isPresent()) {
            Pair<String, Integer> aLinkSolutionPriority = optional.get();
            FullSpinColumn column = new FullSpinColumn(maxSpin, finalPriority, getSpinString(maxSpin));
            htmlBuilder.addColumn(column, aLinkSolutionPriority.getKey(), aLinkSolutionPriority.getValue());
        }
    }

    private List<Spin> getSpins(LockedReachable lockedReachable, Field fieldWithoutT, Operation operation, Mino before, int[][] patterns, RotateDirection direction, int maxHeight, int clearedLine) {
        List<Spin> spins = new ArrayList<>();

        for (int[] pattern : patterns) {
            // ???????????????????????????
            int beforeX = operation.getX() - pattern[0];
            int beforeY = operation.getY() - pattern[1];

            if (beforeX + before.getMinX() < 0 || 10 <= beforeX + before.getMaxX()) {
                continue;
            }

            if (beforeY + before.getMinY() < 0) {
                continue;
            }

            if (!fieldWithoutT.canPut(before, beforeX, beforeY)) {
                continue;
            }

            SpinResult spinResult = minoRotationDetail.getKicks(fieldWithoutT, direction, before, beforeX, beforeY);

            if (spinResult == SpinResult.NONE) {
                continue;
            }

            // ?????????????????????????????????
            if (spinResult.getToX() != operation.getX() || spinResult.getToY() != operation.getY()) {
                continue;
            }

            // ????????????????????????????????????
            if (!lockedReachable.checks(fieldWithoutT, before, beforeX, beforeY, maxHeight)) {
                continue;
            }

            Spin spin = SpinCommons.getSpins(fieldWithoutT, spinResult, clearedLine);
            spins.add(spin);
        }

        return spins;
    }

    private int getSpinPriority(Spin spin) {
        int clearedLine = spin.getClearedLine();

        switch (spin.getSpin()) {
            case Mini: {
                return clearedLine * 10 + 1;
            }
            case Regular: {
                switch (spin.getName()) {
                    case Iso: {
                        return clearedLine * 10 + 2;
                    }
                    case Fin: {
                        return clearedLine * 10 + 3;
                    }
                    case Neo: {
                        return clearedLine * 10 + 4;
                    }
                    case NoName: {
                        return clearedLine * 10 + 5;
                    }
                }
            }
        }

        throw new IllegalStateException();
    }

    private String getSpinString(Spin spin) {
        int clearedLine = spin.getClearedLine();
        String lineString = getSendLineString(clearedLine);
        switch (spin.getSpin()) {
            case Mini: {
                return lineString + " [Mini]";
            }
            case Regular: {
                return lineString + " [" + spin.getName().getName() + "]";
            }
        }

        throw new IllegalStateException();
    }

    private String getSendLineString(int clearedLine) {
        assert 1 <= clearedLine && clearedLine <= 3;
        switch (clearedLine) {
            case 1:
                return "Single";
            case 2:
                return "Double";
            case 3:
                return "Triple";
        }
        throw new IllegalStateException();
    }
}
