import core.action.reachable.LockedReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import common.buildup.BuildUp;
import common.datastore.OperationWithKey;
import common.iterable.PermutationIterable;
import common.datastore.Operations;

import java.util.List;
import java.util.stream.Collectors;

import static searcher.common.OperationsFactory.createOperations;


public class Main2 {

    public static void main(String[] args) {
        Field fieldOrigin = FieldFactory.createField("" +
                "XXXX____XX" +
                "XXXX___XXX" +
                "XXXX__XXXX" +
                "XXXX___XXX" +
                ""
        );
//        Operations operations = createOperations("J,0,5,0", "T,2,5,1", "I,0,5,0");
        Operations operations = createOperations("T,R,4,1", "S,0,6,1", "Z,0,5,0");
        MinoFactory minoFactory = new MinoFactory();
        int height = 4;
        List<OperationWithKey> objs = BuildUp.createOperationWithKeys(fieldOrigin, operations, minoFactory, height);

        System.out.println("---");
        for (OperationWithKey obj : objs) {
            System.out.println(obj);
        }

        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        LockedReachable reachable = new LockedReachable(minoFactory, minoShifter, minoRotation, height);
        PermutationIterable<OperationWithKey> iterable = new PermutationIterable<>(objs, objs.size());
        for (List<OperationWithKey> list : iterable) {
            System.out.println(list.stream().map(o -> o.getMino().getBlock().getName()).collect(Collectors.joining("")));
            boolean isBuild = BuildUp.cansBuild(fieldOrigin, list, height, reachable);
            System.out.println(isBuild);
        }
    }
}