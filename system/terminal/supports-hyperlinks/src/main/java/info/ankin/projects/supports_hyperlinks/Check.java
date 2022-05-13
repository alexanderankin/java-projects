package info.ankin.projects.supports_hyperlinks;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface Check {
    /**
     * @see Enum#name()
     */
    String name();

    /**
     * This method might need to be augmented if the jvm ever lifts the restriction of {@link TtyCheck}s.
     *
     * @return if this check says that we support hyperlinks.
     */
    boolean check();

    default Map.Entry<String, Boolean> toEntry() {
        return Map.entry(name(), check());
    }

    enum Checks implements Check {
        dom {
            @SuppressWarnings("SpellCheckingInspection")
            @Override
            public boolean check() {
                return Objects.nonNull(getEnv.apply("DOMTERM"));
            }
        },
        kitty {
            @Override
            public boolean check() {
                return "xterm-kitty".equals(getEnv.apply("TERM"));
            }
        },
        termProgram {
            @Override
            public boolean check() {
                return Arrays.asList("Hyper", "iTerm.app", "terminology", "WezTerm").contains(getEnv.apply("TERM_PROGRAM"));
            }
        },
        /**
         * @see <a href="https://github.com/zkat/supports-hyperlinks/blob/0c37a84df7df119bc2e32a04bee92abac08259b3/src/lib.rs#L19">zkat/supports-hyperlinks</a>
         */
        vteVersion {
            @Override
            public boolean check() {
                try {
                    // VTE-based terminals above v0.50 (Gnome Terminal, Guake, ROXTerm, etc)
                    String versionString = getEnv.apply("VTE_VERSION");
                    int version = Integer.parseInt(versionString);
                    return version > 5000;
                } catch (Throwable t) {
                    return false;
                }
            }
        },
        @SuppressWarnings("SpellCheckingInspection")
        winKonsole {
            @Override
            public boolean check() {
                return Objects.nonNull(getEnv.apply("KONSOLE_VERSION"));
            }
        },
        winSession {
            @Override
            public boolean check() {
                return Objects.nonNull(getEnv.apply("WT_SESSION"));
            }
        },
        ;

        public static final List<Check> VALUES = Arrays.asList(Checks.values());
        static Function<String, String> getEnv = System::getenv;
    }

    /**
     * This checks if the standard out of the jvm is a TTY.
     * However, when both stdout and stdin are both a TTY,
     * then {@link System#console()} returns not null.
     * <p>
     * There is a limitation in java where we cannot
     * check this property on arbitrary file descriptors.
     */
    class TtyCheck implements Check {
        @Override
        public String name() {
            return "tty";
        }

        @Override
        public boolean check() {
            return Objects.nonNull(System.console());
        }
    }

    /**
     * This is a way to override the other checks in this library
     */
    class ForcedCheck implements Check {
        @Override
        public String name() {
            return "forced";
        }

        @Override
        public boolean check() {
            return BooleanUtils.toBoolean(System.getenv("FORCE_HYPERLINK"));
        }
    }
}
