package info.ankin.projects.ssh.ssh_server;

import lombok.SneakyThrows;
import org.apache.sshd.certificate.OpenSshCertificateBuilder;
import org.apache.sshd.common.config.keys.OpenSshCertificate;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.security.bouncycastle.BouncyCastleGeneratorHostKeyProvider;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.auth.keyboard.DefaultKeyboardInteractiveAuthenticator;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;

import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.apache.sshd.common.config.keys.KeyUtils.generateKeyPair;

public class SshServer {
    private static List<OpenSshCertificate> genPairList = null;

    @SneakyThrows
    public static void main(String[] args) {
        try (org.apache.sshd.server.SshServer sshd = org.apache.sshd.server.SshServer.setUpDefaultServer()) {
            sshd.setPort(2021);
            sshd.setKeyPairProvider(new BouncyCastleGeneratorHostKeyProvider(null));
            sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
            sshd.setCommandFactory(new ScpCommandFactory());

            sshd.setKeyboardInteractiveAuthenticator(new DefaultKeyboardInteractiveAuthenticator());

            sshd.setHostKeyCertificateProvider(session -> genPair());
            sshd.setPasswordAuthenticator((username, password, session) -> "user".equals(username) && "pass".equals(password));
            System.out.println("port: " + sshd.getPort());
            sshd.start();

            new CountDownLatch(1).await();
        }
    }

    @SneakyThrows
    private static List<OpenSshCertificate> genPair() {
        if (genPairList != null) return genPairList;
        KeyPair keyPair = generateKeyPair(KeyPairProvider.SSH_RSA, 2048);
        OpenSshCertificateBuilder builder = OpenSshCertificateBuilder.userCertificate();
        builder.id("id");
        builder.publicKey(keyPair.getPublic());
        List<OpenSshCertificate> result = List.of(builder.sign(keyPair));
        genPairList = result;
        return result;
    }
}
