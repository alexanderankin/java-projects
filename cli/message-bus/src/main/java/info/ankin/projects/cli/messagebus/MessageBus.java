package info.ankin.projects.cli.messagebus;

import info.ankin.picocli.versionprovider.VersionProvider;
import picocli.CommandLine;

@CommandLine.Command(mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class MessageBus {
    public static void main(String[] args) {
        System.exit(new CommandLine(new MessageBus()).execute(args));
    }
}
