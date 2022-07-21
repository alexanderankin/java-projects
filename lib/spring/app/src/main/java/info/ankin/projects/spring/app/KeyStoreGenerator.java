package info.ankin.projects.spring.app;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;

public class KeyStoreGenerator implements Callable<KeyStore> {
    @SneakyThrows
    @Override
    public KeyStore call() {
        SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
        X509Certificate cert = selfSignedCertificate.cert();
        PrivateKey key = selfSignedCertificate.key();
        return assemble(key, cert);
    }

    @SneakyThrows
    protected KeyStore assemble(PrivateKey key, X509Certificate cert) {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(null);
        keyStore.setCertificateEntry("cert", cert);
        keyStore.setKeyEntry("key", key, new char[0], new Certificate[]{cert});
        return keyStore;
    }
}
