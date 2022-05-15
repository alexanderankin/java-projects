package info.ankin.projects.cli.gpg;

import picocli.CommandLine;

public class GpgVersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() {
        return new String[]{getImplementationVersion()};
    }

    public String getImplementationVersion() {
        return GpgVersionProvider.class.getPackage().getImplementationVersion();
    }
}
