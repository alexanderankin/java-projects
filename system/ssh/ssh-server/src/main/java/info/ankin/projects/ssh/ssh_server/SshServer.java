package info.ankin.projects.ssh.ssh_server;

import lombok.SneakyThrows;
import org.apache.sshd.certificate.OpenSshCertificateBuilder;
import org.apache.sshd.common.config.keys.OpenSshCertificate;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.security.bouncycastle.BouncyCastleGeneratorHostKeyProvider;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.auth.keyboard.DefaultKeyboardInteractiveAuthenticator;

import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.apache.sshd.common.config.keys.KeyUtils.generateKeyPair;

public class SshServer implements AutoCloseable {
    final org.apache.sshd.server.SshServer sshServer;
    final List<OpenSshCertificate> genPairList;

    public SshServer() {
        sshServer = getSshServer();
        genPairList = genPair();
    }

    @SneakyThrows
    public static void main(String[] args) {
        try (SshServer ignored = new SshServer()) {
            new CountDownLatch(1).await();
        }
    }

    @SneakyThrows
    private org.apache.sshd.server.SshServer getSshServer() {
        final org.apache.sshd.server.SshServer sshServer;
        sshServer = org.apache.sshd.server.SshServer.setUpDefaultServer();
        sshServer.setPort(2021);
        sshServer.setKeyPairProvider(new BouncyCastleGeneratorHostKeyProvider(null));
        sshServer.setShellFactory(new TtyShellFactory());
        sshServer.setCommandFactory(new ScpCommandFactory());

        sshServer.setKeyboardInteractiveAuthenticator(new DefaultKeyboardInteractiveAuthenticator());

        sshServer.setHostKeyCertificateProvider(session -> genPair());
        sshServer.setPasswordAuthenticator((username, password, session) -> "user".equals(username) && "pass".equals(password));
        System.out.println("port: " + sshServer.getPort());
        sshServer.start();
        return sshServer;
    }

    @SneakyThrows
    private List<OpenSshCertificate> genPair() {
        KeyPair keyPair = generateKeyPair(KeyPairProvider.SSH_RSA, 2048);
        OpenSshCertificateBuilder builder = OpenSshCertificateBuilder.userCertificate();
        builder.id("id");
        builder.publicKey(keyPair.getPublic());
        return List.of(builder.sign(keyPair));
    }

    @SneakyThrows
    @Override
    public void close() {
        sshServer.close();
    }
}
