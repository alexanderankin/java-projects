package info.ankin.projects.cli.msgs;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command
public class Msgs implements Callable<Integer> {
    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Msgs());
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        System.out.println("hi");
        return 0;
    }
}
