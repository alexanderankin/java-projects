package info.ankin.projects.supports_hyperlinks;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Main entrypoint to this artifact
 */
public class SupportsHyperlinks {
    private static final AtomicReference<Checker> cached = new AtomicReference<>();

    public static Checker checkerCached() {
        if (cached.get() == null) {
            cached.set(checker());
        }

        return cached.get();
    }

    public static Checker checker() {
        return new Checker(new Check.ForcedCheck(), new Check.TtyCheck(), Check.Checks.VALUES);
    }

    @Data
    public static class Checker {
        private final boolean forced;
        private final boolean tty;
        private final Map<String, Boolean> checks;
        private final boolean anyPassed;

        public Checker(Check forced, Check tty, List<Check> checkList) {
            this.forced = forced.check();
            this.tty = tty.check();
            checks = checkList.stream().map(Check::toEntry).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            anyPassed = checks.values().stream().anyMatch(Boolean::booleanValue);
        }

        /**
         * Checks if we support hyperlinks based on the current environment variables and stdout stream
         *
         * @return whether the current environment supports hyperlinks
         */
        public boolean supportsHyperlinks() {
            return forced || (tty && anyPassed);
        }
    }

}
