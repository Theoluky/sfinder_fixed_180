package entry.util.seq;

import entry.common.option.ListArgOption;
import entry.common.option.NoArgOption;
import entry.common.option.OptionBuilder;
import entry.common.option.SingleArgOption;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public enum SeqUtilOptions {
    Help(NoArgOption.full("h", "help", "Usage")),
    Patterns(ListArgOption.fullSpace("p", "patterns", "definition", "Specify pattern definition, directly")),
    Mode(SingleArgOption.full("M", "mode", "pass or forward", "Specify sequence mode")),
    CuttingSize(SingleArgOption.full("c", "cut", "cutting size", "Specify max piece size in a sequence")),
    Distinct(SingleArgOption.full("d", "distinct", "yes or no", "Specify whether or not to remove duplicates")),
    LogPath(SingleArgOption.full("lp", "log-path", "path", "File path of output log")),
    ;

    private final OptionBuilder optionBuilder;

    SeqUtilOptions(OptionBuilder optionBuilder) {
        this.optionBuilder = optionBuilder;
    }

    public String optName() {
        return optionBuilder.getLongName();
    }

    public static Options create() {
        Options allOptions = new Options();

        for (SeqUtilOptions options : SeqUtilOptions.values()) {
            Option option = options.optionBuilder.toOption();
            allOptions.addOption(option);
        }

        return allOptions;
    }
}