package info.ankin.projects.cli.messagebus;

import info.ankin.picocli.versionprovider.VersionProvider;
import info.ankin.projects.cli.messagebus.command.Publish;
import info.ankin.projects.cli.messagebus.command.Subscribe;
import info.ankin.projects.cli.messagebus.mixin.ConnectionInfo;
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

    public static void main(String[] args) {
        System.exit(new CommandLine(new MessageBus()).execute(args));
    }

    @Override
    public void run() {
        log.debug("configured: {}", this);
    }
}
