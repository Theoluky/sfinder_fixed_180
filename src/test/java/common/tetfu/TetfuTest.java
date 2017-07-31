package common.tetfu;

import common.buildup.BuildUpStream;
import common.datastore.OperationWithKey;
import common.datastore.Operations;
import common.parser.OperationTransform;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ArrayColoredField;
import common.tetfu.field.ColoredField;
import common.tetfu.field.ColoredFieldFactory;
import concurrent.LockedReachableThreadLocal;
import core.column_field.ColumnField;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import core.srs.Rotate;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import searcher.pack.InOutPairField;
import searcher.pack.SeparableMinos;
import searcher.pack.SizedBit;
import searcher.pack.memento.SRSValidSolutionFilter;
import searcher.pack.memento.SolutionFilter;
import searcher.pack.solutions.OnDemandBasicSolutions;
import searcher.pack.task.Field4x10MinoPackingHelper;
import searcher.pack.task.PackSearcher;
import searcher.pack.task.Result;
import searcher.pack.task.TaskResultHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static core.mino.Block.J;
import static core.mino.Block.L;
import static org.assertj.core.api.Assertions.assertThat;

class TetfuTest {
    private static void assertField(ColoredField actual, ColoredField expected) {
        for (int y = 0; y < 24; y++)
            for (int x = 0; x < 10; x++)
                assertThat(actual.getBlockNumber(x, y)).isEqualTo(expected.getBlockNumber(x, y));
    }

    @Test
    void encode1() throws Exception {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.T, Rotate.Spawn, 5, 0)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhA1QJ");
    }

    @Test
    void encode2() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.L, Rotate.Spawn, 4, 0),
                new TetfuElement(ColorType.J, Rotate.Spawn, 8, 0),
                new TetfuElement(ColorType.I, Rotate.Left, 6, 1),
                new TetfuElement(ColorType.S, Rotate.Spawn, 4, 1),
                new TetfuElement(ColorType.Z, Rotate.Spawn, 8, 1),
                new TetfuElement(ColorType.T, Rotate.Spawn, 4, 3),
                new TetfuElement(ColorType.O, Rotate.Spawn, 0, 0),
                new TetfuElement(ColorType.J, Rotate.Right, 0, 3)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhHSQJWyBJnBXmBUoBVhBTpBOfB");
    }

    @Test
    void encode3() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 5, 0, "a"),
                new TetfuElement(ColorType.S, Rotate.Reverse, 5, 2, "b"),
                new TetfuElement(ColorType.J, Rotate.Left, 9, 1, "c"),
                new TetfuElement(ColorType.O, Rotate.Right, 0, 1, "d"),
                new TetfuElement(ColorType.Z, Rotate.Left, 3, 1, "e"),
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "日本語"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhGBQYBABBAAAnmQBACBAAA+tQBADBAAALpQBAEBAA?AcqQBAFBAAAKfQSAlfrHBFwDfE2Cx2Bl/PwB53AAAlsQAA");
    }

    @Test
    void encode4() throws Exception {
        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
        field.putMino(factory.create(Block.I, Rotate.Spawn), 1, 0);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(field, ColorType.I, Rotate.Spawn, 5, 0, "")
        );

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("bhzhPexQJ");
    }

    @Test
    void encode5() throws Exception {
        MinoFactory factory = new MinoFactory();
        ArrayColoredField field = new ArrayColoredField(Tetfu.TETFU_MAX_HEIGHT);
        field.putMino(factory.create(Block.I, Rotate.Spawn), 1, 0);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(field, ColorType.I, Rotate.Reverse, 6, 0)
        );

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("bhzhPehQJ");
    }

    @Test
    void encode6() throws Exception {
        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.Empty, Rotate.Spawn, 6, 0)
        );

        MinoFactory factory = new MinoFactory();

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhAAgH");
    }

    @Test
    void encode7() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                TetfuElement.createFieldOnly(ColoredFieldFactory.createColoredField("XXXXLLXXXX")),
                TetfuElement.createFieldOnly(ColoredFieldFactory.createColoredField("XXXXJJXXXX"))
        );

        MinoFactory factory = new MinoFactory();

        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("bhD8hlD8JeAgHbhD8h0D8JeAAA");
    }


    @Test
    void encodeQuiz1() throws Exception {
        List<Block> orders = Collections.singletonList(L);
        String quiz = Tetfu.encodeForQuiz(orders);

        List<TetfuElement> elements = Collections.singletonList(
                new TetfuElement(ColorType.L, Rotate.Right, 0, 1, quiz)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhAKJYUAFLDmClcJSAVDEHBEooRBMoAVB");
    }

    @Test
    void encodeQuiz2() throws Exception {
        List<Block> orders = Arrays.asList(J, L);
        String quiz = Tetfu.encodeForQuiz(orders, L);

        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.L, Rotate.Right, 0, 1, quiz),
                new TetfuElement(ColorType.J, Rotate.Left, 3, 1, quiz)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);
        assertThat(encode).isEqualTo("vhBKJYVAFLDmClcJSAVTXSAVG88AYS88AZAAAA+qB");
    }

    @Test
    void decode1() throws Exception {
        String value = "bhzhPexAN";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(1);
        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Spawn, TetfuPage::getRotate)
                .returns(5, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField("IIII______"), pages.get(0).getField());
    }

    @Test
    void decode2() throws Exception {
        String value = "bhzhPexAcFAooMDEPBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(1);
        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Spawn, TetfuPage::getRotate)
                .returns(5, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("hello", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField("IIII______"), pages.get(0).getField());
    }

    @Test
    void decode3() throws Exception {
        // empty
        String value = "vhAAgH";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(1);
        assertThat(pages.get(0))
                .returns(ColorType.Empty, TetfuPage::getColorType)
                .returns(Rotate.Reverse, TetfuPage::getRotate)
                .returns(0, TetfuPage::getX)
                .returns(22, TetfuPage::getY)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField(""), pages.get(0).getField());
    }

    @Test
    void decode4() throws Exception {
        List<TetfuElement> elements = Arrays.asList(
                new TetfuElement(ColorType.I, Rotate.Reverse, 5, 0, "a"),
                new TetfuElement(ColorType.S, Rotate.Reverse, 5, 2, "b"),
                new TetfuElement(ColorType.J, Rotate.Left, 9, 1, "c"),
                new TetfuElement(ColorType.O, Rotate.Right, 0, 1, "hello world!"),
                new TetfuElement(ColorType.Z, Rotate.Left, 3, 1, "こんにちは"),
                new TetfuElement(ColorType.L, Rotate.Right, 0, 3, "x ~= 1;"),
                new TetfuElement(ColorType.T, Rotate.Reverse, 7, 1)
        );

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        String encode = tetfu.encode(elements);

        List<TetfuPage> pages = tetfu.decode(encode);

        assertThat(pages).hasSize(elements.size());
        for (int index = 0; index < pages.size(); index++) {
            TetfuElement element = elements.get(index);
            assertThat(pages.get(index))
                    .returns(element.getColorType(), TetfuPage::getColorType)
                    .returns(element.getRotate(), TetfuPage::getRotate)
                    .returns(element.getX(), TetfuPage::getX)
                    .returns(element.getY(), TetfuPage::getY)
                    .returns(element.getComment(), TetfuPage::getComment);
        }
    }

    @Test
    void decode5() throws Exception {
        String value = "bhzhFeH8Bex4OvhAAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(2);

        assertThat(pages.get(0))
                .returns(ColorType.I, TetfuPage::getColorType)
                .returns(Rotate.Spawn, TetfuPage::getRotate)
                .returns(5, TetfuPage::getX)
                .returns(0, TetfuPage::getY)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField(
                "IIII______"
        ), pages.get(0).getField());

        assertThat(pages.get(1))
                .returns(ColorType.Empty, TetfuPage::getColorType)
                .returns("", TetfuPage::getComment);
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "__IIIIIIII" +
                        "__XXXXXXXX"
        ), pages.get(1).getField());
    }

    @Test
    void decode6() throws Exception {
        String value = "VhRpHeRpNeAgHvhIAAAAAAAAAAAAAAAAAAAAAAAAAA?A";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(10);
        for (int index = 0; index < 10; index++) {
            assertThat(pages.get(index))
                    .returns(ColorType.Empty, TetfuPage::getColorType)
                    .returns("", TetfuPage::getComment);
        }
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "____OO____" +
                        "____OO____"
        ), pages.get(9).getField());
    }

    @Test
    void decode7() throws Exception {
        String value = "+gH8AeI8BeH8AeI8KeAgHvhBpoBAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(3);
        assertThat(pages.get(0)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertThat(pages.get(1)).returns(ColorType.I, TetfuPage::getColorType);
        assertThat(pages.get(2)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertField(ColoredFieldFactory.createColoredField(
                "" +
                        "_XXXXXXXXI" +
                        "_XXXXXXXXI"
        ), pages.get(2).getField());
    }

    @Test
    void decode8() throws Exception {
        String value = "bhD8hlD8JeAgHbhD8h0D8JeAAA";

        MinoFactory factory = new MinoFactory();
        ColorConverter converter = new ColorConverter();
        Tetfu tetfu = new Tetfu(factory, converter);
        List<TetfuPage> pages = tetfu.decode(value);

        assertThat(pages).hasSize(2);
        assertThat(pages.get(0)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertThat(pages.get(1)).returns(ColorType.Empty, TetfuPage::getColorType);
        assertField(ColoredFieldFactory.createColoredField(
                "XXXXJJXXXX"
        ), pages.get(1).getField());
    }

    @Test
    @Tag("long")
    void random() throws Exception {
        // Initialize
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        ColorConverter colorConverter = new ColorConverter();

        // Define size
        int height = 4;
        int basicWidth = 3;
        SizedBit sizedBit = new SizedBit(basicWidth, height);
        SeparableMinos separableMinos = SeparableMinos.createSeparableMinos(minoFactory, minoShifter, sizedBit);

        // Create basic solutions
        TaskResultHelper taskResultHelper = new Field4x10MinoPackingHelper();
        LockedReachableThreadLocal lockedReachableThreadLocal = new LockedReachableThreadLocal(minoFactory, minoShifter, minoRotation, height);
        Predicate<ColumnField> memorizedPredicate = (columnField) -> true;
        OnDemandBasicSolutions basicSolutions = new OnDemandBasicSolutions(separableMinos, sizedBit, memorizedPredicate);

        for (int count = 0; count < 20; count++) {
            System.out.println(count);
            // Create field
            int numOfMinos = randoms.nextIntClosed(6, 10);
            Field field = randoms.field(height, numOfMinos);

            // Search
            List<InOutPairField> inOutPairFields = InOutPairField.createInOutPairFields(basicWidth, height, field);
            SolutionFilter solutionFilter = new SRSValidSolutionFilter(field, lockedReachableThreadLocal, sizedBit);
            PackSearcher searcher = new PackSearcher(inOutPairFields, basicSolutions, sizedBit, solutionFilter, taskResultHelper);
            Optional<Result> resultOptional = searcher.findAny();

            BuildUpStream buildUpStream = new BuildUpStream(lockedReachableThreadLocal.get(), height);
            // If found solution
            resultOptional.ifPresent(result -> {
                List<OperationWithKey> list = result.getMemento()
                        .getOperationsStream(basicWidth)
                        .collect(Collectors.toList());
                Optional<List<OperationWithKey>> validOption = buildUpStream.existsValidBuildPattern(field, list).findAny();
                validOption.ifPresent(operationWithKeys -> {
                    Operations operations = OperationTransform.parseToOperations(field, operationWithKeys, height);
                    List<TetfuElement> elements = operations.getOperations().stream()
                            .map(operation -> {
                                ColorType colorType = colorConverter.parseToColorType(operation.getBlock());
                                Rotate rotate = operation.getRotate();
                                int x = operation.getX();
                                int y = operation.getY();
                                String comment = randoms.string() + randoms.string() + randoms.string();
                                return new TetfuElement(colorType, rotate, x, y, comment);
                            })
                            .collect(Collectors.toList());

                    String encode = new Tetfu(minoFactory, colorConverter).encode(elements);
                    List<TetfuPage> decode = new Tetfu(minoFactory, colorConverter).decode(encode);

                    assertThat(decode).hasSize(elements.size());
                    for (int index = 0; index < decode.size(); index++) {
                        TetfuElement element = elements.get(index);
                        assertThat(decode.get(index))
                                .returns(element.getColorType(), TetfuPage::getColorType)
                                .returns(element.getRotate(), TetfuPage::getRotate)
                                .returns(element.getX(), TetfuPage::getX)
                                .returns(element.getY(), TetfuPage::getY)
                                .returns(element.getComment(), TetfuPage::getComment);
                    }
                });
            });
        }
    }
}