package info.ankin.projects.picocli.logback.verbosity;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.*;
import lombok.experimental.Accessors;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

@Data
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LogbackVerbosityMixin {
    public static final List<Level> LEVELS = List.of(Level.OFF, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE);
    public static final int DEFAULT_LEVEL = 2;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    final Logger rootLogger;
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter
    @Setter(AccessLevel.NONE)
    int verbosity;

    public LogbackVerbosityMixin() {
        this(LEVELS.get(DEFAULT_LEVEL));
    }

    public LogbackVerbosityMixin(Level defaultLevel) {
        rootLogger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(defaultLevel);
    }

    @CommandLine.Option(
            names = {"-v", "--verbose"},
            description = "Increase verbosity. Specify multiple times to increase (-vvv)."
    )
    public void verbosity(boolean[] verbosity) {
        // default is warn, for every other one, set it
        int delta = 0;
        for (boolean b : verbosity) {
            delta += b ? 1 : -1;
        }

        setVerbosity(delta);
    }

    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        int newLevel = DEFAULT_LEVEL + verbosity;
        int legalNewLevel = newLevel < 0
                ? 0
                :
                (
                        newLevel >= LEVELS.size()
                                ? LEVELS.size() - 1
                                : newLevel
                );

        rootLogger.setLevel(LEVELS.get(legalNewLevel));
    }

}
