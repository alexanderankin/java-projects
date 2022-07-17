package info.ankin.picocli.nologging;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class Verbosity {
    public static int verbosity(String... args) {
        return new CommandLine(new VerbosityCalculatorApp()).execute(args);
    }

    public static int verbosity(List<String> args) {
        return verbosity(args.toArray(String[]::new));
    }

    @CommandLine.Command
    public static class VerbosityCalculatorApp implements Callable<Integer> {
        @CommandLine.Option(names = {"-v", "--verbose"},
                description = {
                        "Specify multiple -v options to increase verbosity.",
                        "For example, `-v -v -v` or `-vvv`"})
        Boolean[] verbosity;

        @Override
        public Integer call() {
            return Math.toIntExact(verbosity());
        }

        private long verbosity() {
            return Arrays.stream(Objects.requireNonNullElse(verbosity, new Boolean[0]))
                    .filter(Predicate.isEqual(Boolean.TRUE))
                    .count();
        }
    }
}
