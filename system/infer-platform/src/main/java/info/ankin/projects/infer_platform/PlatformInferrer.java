package info.ankin.projects.infer_platform;

public class PlatformInferrer {
    public Os os() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("windows")) return Os.WINDOWS;
        if (osName.toLowerCase().startsWith("mac")) return Os.DARWIN;
        if (osName.toLowerCase().startsWith("linux")) return Os.LINUX;
        return Os.UNKNOWN;
    }

    public Arch arch() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
