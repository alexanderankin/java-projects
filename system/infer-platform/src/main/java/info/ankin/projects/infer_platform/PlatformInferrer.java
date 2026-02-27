package info.ankin.projects.infer_platform;

public class PlatformInferrer {
    public Os os() {
        // echo 'System.out.println(System.getProperty("os.name"))' | jshell -
        String osName = System.getProperty("os.name");
        String lowerCase = osName.toLowerCase();
        if (lowerCase.startsWith("windows")) return Os.WINDOWS;
        if (lowerCase.startsWith("mac")) return Os.DARWIN;
        if (lowerCase.startsWith("linux")) return Os.LINUX;
        return Os.UNKNOWN;
    }

    public Arch arch() {
        // echo 'System.out.println(System.getProperty("os.arch"))' | jshell -
        String archName = System.getProperty("os.arch");
        String lowerCase = archName.toLowerCase();
        return switch (lowerCase) {
            case "amd64", "x86_64" -> Arch.AMD_64;
            case "arm64", "aarch64" -> Arch.ARM_64;
            case "arm" -> Arch.ARM;
            default -> Arch.UNKNOWN;
        };
    }
}
