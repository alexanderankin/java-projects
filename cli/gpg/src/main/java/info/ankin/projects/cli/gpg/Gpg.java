package info.ankin.projects.cli.gpg;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true,
        versionProvider = GpgVersionProvider.class)
public class Gpg implements Callable<Integer> {
    public static void main(String[] args) {
        System.exit(new CommandLine(Gpg.class).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Hello, world!");
        return 0;
    }
}
