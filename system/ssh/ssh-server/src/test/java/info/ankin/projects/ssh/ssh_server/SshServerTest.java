package info.ankin.projects.ssh.ssh_server;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class SshServerTest {

    @SneakyThrows
    @Test
    void test() {
        PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder()
                .setCommand(new String[]{"bash", "-il"})
                ;

        PtyProcess ptyProcess = ptyProcessBuilder.start();

        ptyProcess.getOutputStream().write(("ssh-keygen " +
                "-f \"/home/" + System.getenv("user.name") + "/.ssh/known_hosts\" " +
                "-R \"[localhost]:2021\" && " +
                "ssh -o StrictHostKeyChecking=no -p 2021 user@localhost").getBytes(StandardCharsets.UTF_8));

        ptyProcess.getOutputStream().flush();

        Thread.sleep(500);

        ptyProcess.getOutputStream().write("pass\n".getBytes(StandardCharsets.UTF_8));
        ptyProcess.getOutputStream().flush();
        Thread.sleep(500);

        ptyProcess.getOutputStream().write("logout\n".getBytes(StandardCharsets.UTF_8));
        ptyProcess.getOutputStream().flush();

        ptyProcess.destroy();
        System.out.println("exited with: " + ptyProcess.waitFor());
        System.out.println(new String(ptyProcess.getInputStream().readAllBytes()));
    }

}
