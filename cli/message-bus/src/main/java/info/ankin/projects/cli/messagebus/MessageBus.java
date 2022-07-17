package info.ankin.projects.cli.messagebus;

import info.ankin.picocli.nologging.Verbosity;
import info.ankin.picocli.versionprovider.VersionProvider;
import info.ankin.projects.cli.messagebus.command.Publish;
import info.ankin.projects.cli.messagebus.command.Subscribe;
import info.ankin.projects.cli.messagebus.mixin.ConnectionInfo;
import io.micronaut.configuration.picocli.PicocliRunner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@Data
@CommandLine.Command(mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        subcommands = {
                Publish.class,
                Subscribe.class,
        }
)
public class MessageBus implements Runnable {
    @CommandLine.Mixin
    ConnectionInfo connection;

    // capture extra flags
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "increase verbosity")
    boolean[] verbosity = new boolean[0];

    public static void main(String[] args) {
        switch (Verbosity.verbosity(args)) {
            case 3:
                System.setProperty("logger.levels.ROOT", "DEBUG");
            case 2:
                System.getProperties().putIfAbsent("logger.levels.ROOT", "INFO");
            case 1:
                System.setProperty("logger.levels.info.ankin", "DEBUG");
        }
        System.exit(PicocliRunner.execute(MessageBus.class, args));
    }

    @Override
    public void run() {
        log.debug("configured: {}", this);
    }
}
