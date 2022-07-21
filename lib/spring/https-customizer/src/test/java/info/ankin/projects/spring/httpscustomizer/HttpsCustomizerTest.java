package info.ankin.projects.spring.httpscustomizer;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class HttpsCustomizerTest {
    HttpsCustomizer<?> h = new HttpsCustomizer<>(new HttpsCustomizerProperties()){
    };

    @SneakyThrows
    @Test
    void generatesKeyStore() {
        KeyStore keyStore = h.getKeyStore();
        assertNotNull(keyStore);
    }

    @SneakyThrows
    @Test
    void assemblesKeyStore() {
        KeyFactory rsa = KeyFactory.getInstance("RSA");
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        RSAPrivateKey key = (RSAPrivateKey) keyPair.getPrivate();
        X509Certificate cert = mock(X509Certificate.class);
        KeyStore keyStore = h.assemble(key, cert);
        assertNotNull(keyStore);
    }

}
