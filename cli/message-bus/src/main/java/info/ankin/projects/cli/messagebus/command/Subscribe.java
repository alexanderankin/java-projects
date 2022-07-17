package info.ankin.projects.cli.messagebus.command;

import picocli.CommandLine;

@CommandLine.Command(aliases = {"s", "sub"},
        mixinStandardHelpOptions = true,
        name = "subscribe")
public class Subscribe {
}
