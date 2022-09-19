package info.ankin.projects.ssh.ssh_server;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TtyTest {
    @SneakyThrows
    public static void main(String[] args) {
        PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder()
                .setCommand(new String[]{"bash", "-il"})
                // .setConsole(true)
                ;

        PtyProcess ptyProcess = ptyProcessBuilder.start();

        OutputStream outputStream = ptyProcess.getOutputStream();
        InputStream inputStream = ptyProcess.getInputStream();

        outputStream.write("ls\nexit 0\n".getBytes(StandardCharsets.US_ASCII));
        outputStream.flush();

        System.out.println(new String(inputStream.readAllBytes()));
        System.exit(0);
    }
}
