package _usecase.util.seq;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.util.seq.files.OutputFileHelper;
import entry.EntryPointMain;
import helper.CSVStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class UtilSeqTetfuCaseTest {
    @Nested
    class FumenTest extends UtilSeqUseCaseBaseTest {
        private String buildCommand(String fumen, String options) {
            return String.format("spin -t %s %s", fumen, options);
        }

        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void case1() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            String command = String.format("util seq -t %s %s -p *!", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1680, all, fumen1));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2240, all, fumen2));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(3108, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(812, all));

            // CSV
            CSVStore csv = OutputFileHelper.loadSeqCSV(Arrays.asList("name", fumen1, fumen2));

            assertThat(csv.size()).isEqualTo(5040);
            assertThat(csv.row("name", "TIOLJSZ"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "LISZTOJ"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "X");

            assertThat(csv.row("name", "TILSZJO"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "JSTILZO"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "X");
        }

        @Test
        void case1WithoutHold() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            String command = String.format("util seq -t %s %s -p *! --hold no", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
            System.out.println(log.getOutput());

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(120, all, fumen1));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(186, all, fumen2));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(300, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(6, all));

            // CSV
            CSVStore csv = OutputFileHelper.loadSeqCSV(Arrays.asList("name", fumen1, fumen2));

            assertThat(csv.size()).isEqualTo(5040);
            assertThat(csv.row("name", "JIZSOLT"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "SLIOZJT"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "X");

            assertThat(csv.row("name", "ILSZJOT"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "ZOTIJLS"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "X");
        }

        @Test
        void case1Harddrop() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            String command = String.format("util seq -t %s %s -p *! --drop harddrop", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
            System.out.println(log.getOutput());

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1344, all, fumen1));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1008, all, fumen2));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(1848, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(504, all));

            // CSV
            CSVStore csv = OutputFileHelper.loadSeqCSV(Arrays.asList("name", fumen1, fumen2));

            assertThat(csv.size()).isEqualTo(5040);
            assertThat(csv.row("name", "JIOTLSZ"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "OTLIZJS"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "X");

            assertThat(csv.row("name", "ILSJZOT"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "ZSLIOTJ"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "X");
        }

        @Test
        void case1WithoutHoldHarddrop() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            String command = String.format("util seq -t %s %s -p *! --hold no --drop harddrop", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));
            System.out.println(log.getOutput());

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(90, all, fumen1));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(60, all, fumen2));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(150, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(0, all));

            // CSV
            CSVStore csv = OutputFileHelper.loadSeqCSV(Arrays.asList("name", fumen1, fumen2));

            assertThat(csv.size()).isEqualTo(5040);
            assertThat(csv.row("name", "ISLJZOT"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "O");

            assertThat(csv.row("name", "OILSZJT"))
                    .containsEntry(fumen1, "O")
                    .containsEntry(fumen2, "X");

            assertThat(csv.row("name", "ZTJLSIO"))
                    .containsEntry(fumen1, "X")
                    .containsEntry(fumen2, "X");
        }

        @Test
        void case1Page1() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ#1:6";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ#1:-1";

            String command = String.format("util seq -t %s %s -p *!", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            String fumen1d = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2d = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1680, all, fumen1d));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2240, all, fumen2d));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(3108, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(812, all));
        }

        @Test
        void case1Page2() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ#:";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ#1:";

            String command = String.format("util seq -t %s %s -p *!", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            String fumen1d = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2d = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1680, all, fumen1d));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2240, all, fumen2d));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(3108, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(812, all));
        }

        @Test
        void case1Page3() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ#";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ#:-1";

            String command = String.format("util seq -t %s %s -p *!", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            String fumen1d = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2d = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(1680, all, fumen1d));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2240, all, fumen2d));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(3108, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(812, all));
        }

        @Test
        void case1Short() throws Exception {
            String fumen1 = "v115@vhFRQJUGJKJJvMJTNJGBJ#1:5";
            String fumen2 = "v115@vhFRQJPGJKJJGMJTNJ0BJ#1:5";

            String command = String.format("util seq -t %s %s -p *!", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            String fumen1d = "v115@vhFRQJUGJKJJvMJTNJGBJ";
            String fumen2d = "v115@vhFRQJPGJKJJGMJTNJ0BJ";

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(960, all, fumen1d));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(960, all, fumen2d));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(1500, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(420, all));
        }

        @Test
        void case2() throws Exception {
            String fumen = "v115@vhGRQJWLJSBJTyIXoIVjIUUI";

            String command = String.format("util seq -t %s -p *!", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(64, all, fumen));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(64, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(64, all));
        }

        @Test
        void case3() throws Exception {
            String fumen1 = "http://fumen.zui.jp/?v115@vhGTJJSQJJHJWSJUIJXGJVBJ";
            String fumen2 = "v115@vhGJHJqMJvNJ+LJsKJVBJTJJ";

            String command = String.format("util seq -t %s %s -p *!", fumen1, fumen2);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            String fumen1d = "v115@vhGTJJSQJJHJWSJUIJXGJVBJ";

            // Log
            int all = 5040;
            assertThat(log.getOutput()).contains(Messages.foundSolutions(2520, all, fumen1d));
            assertThat(log.getOutput()).contains(Messages.foundSolutions(3822, all, fumen2));
            assertThat(log.getOutput()).contains(Messages.foundOrSolutions(4102, all));
            assertThat(log.getOutput()).contains(Messages.foundAndSolutions(2240, all));
        }
    }
}