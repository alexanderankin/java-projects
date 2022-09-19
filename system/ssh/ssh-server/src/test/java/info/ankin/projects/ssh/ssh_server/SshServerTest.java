package info.ankin.projects.ssh.ssh_server;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SshServerTest {

    @Test
    void test() {
        try (SshServer ignored = new SshServer()) {
            performTest();
        }
    }

    @SneakyThrows
    void performTest() {
        PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder()
                .setCommand(new String[]{"bash", "-il"})
                ;

        PtyProcess ptyProcess = ptyProcessBuilder.start();

        ptyProcess.getOutputStream().write(("ssh-keygen " +
                "-f \"/home/" + System.getProperty("user.name") + "/.ssh/known_hosts\" " +
                "-R \"[localhost]:2021\" && " +
                "ssh -o StrictHostKeyChecking=no -p 2021 user@localhost\n").getBytes(StandardCharsets.UTF_8));

        ptyProcess.getOutputStream().flush();

        Thread.sleep(1000);

        ptyProcess.getOutputStream().write("pass\n".getBytes(StandardCharsets.UTF_8));
        ptyProcess.getOutputStream().flush();
        Thread.sleep(1000);

        ptyProcess.getOutputStream().write("ls -1\n".getBytes(StandardCharsets.UTF_8));
        ptyProcess.getOutputStream().flush();
        Thread.sleep(1000);

        ptyProcess.getOutputStream().write("logout\n".getBytes(StandardCharsets.UTF_8));
        ptyProcess.getOutputStream().flush();
        Thread.sleep(1000);

        ptyProcess.getOutputStream().write("logout\n".getBytes(StandardCharsets.UTF_8));
        ptyProcess.getOutputStream().flush();
        Thread.sleep(1000);

        System.out.println("exited with: " + ptyProcess.waitFor());
        System.out.println("printed:");
        System.out.println(new String(ptyProcess.getInputStream().readAllBytes()));
        System.out.println("error:");
        System.out.println(new String(ptyProcess.getErrorStream().readAllBytes()));

        assertThat(ptyProcess.exitValue(), is(0));
    }

}
