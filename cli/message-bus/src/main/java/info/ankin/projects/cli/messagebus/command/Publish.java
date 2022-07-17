package info.ankin.projects.cli.messagebus.command;

import picocli.CommandLine;

@CommandLine.Command(aliases = {"p", "pub"},
        mixinStandardHelpOptions = true,
        name = "publish")
public class Publish {
}
