package info.ankin.projects.spring.httpscustomizer;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public abstract class HttpsCustomizer<T extends ConfigurableWebServerFactory> implements WebServerFactoryCustomizer<T> {
    private final HttpsCustomizerProperties properties;

    protected HttpsCustomizer(HttpsCustomizerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(T factory) {
        Ssl ssl = new Ssl();
        ssl.setBundle("default");
        factory.setSsl(ssl);
        factory.setSslBundles(new DefaultSslBundleRegistry("default", SslBundle.of(SslStoreBundle.of(getKeyStore(), "", null))));
    }

    /**
     * If you generate this yourself (e.g. with OpenSSL cli)
     * you will need to parse it, like so:
     * <a href="https://stackoverflow.com/a/12514888">stackoverflow link</a>.
     */
    @SneakyThrows
    protected KeyStore getKeyStore() {
        SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate(
                properties.getFqdn(),
                properties.getNotBefore(),
                properties.getNotAfter(),
                properties.getAlgorithm(),
                properties.getBits()
        );
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
