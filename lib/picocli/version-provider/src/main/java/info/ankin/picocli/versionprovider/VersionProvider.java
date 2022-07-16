package info.ankin.picocli.versionprovider;

import picocli.CommandLine;

/**
 * This class requires setting the <code>Implementation-Version</code>
 * attribute in jar manifest, and only reported when from launched from jar.
 * <p>
 * This can be accomplished by adding this to your <code>build.gradle</code>
 * or <a href="https://stackoverflow.com/a/16646741/4971476">this guide</a>
 * for maven:
 * <pre>
 * tasks.withType(Jar) { manifest { attributes('Implementation-Version', '1.0') } }
 * </pre>
 */
public class VersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() {
        Package p = pkg();

        String i = p.getImplementationVersion();
        if (i != null) {
            return new String[]{i};
        }

        String s = p.getSpecificationVersion();
        if (s != null) {
            return new String[]{s};
        }

        return new String[0];
    }

    protected Package pkg() {
        return getClass().getPackage();
    }

}
