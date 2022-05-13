package info.ankin.projects.cli.killport;

import info.ankin.projects.infer_platform.Os;
import info.ankin.projects.infer_platform.PlatformInferrer;

public class KillPort {
    public static void main(String[] args) {
        System.out.println("hello, world!");
        Os os = new PlatformInferrer().os();
        System.out.println("we are running on " + os + "!");
    }
}
