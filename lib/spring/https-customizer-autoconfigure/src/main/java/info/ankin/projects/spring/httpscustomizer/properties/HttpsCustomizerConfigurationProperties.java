package info.ankin.projects.spring.httpscustomizer.properties;

import info.ankin.projects.spring.httpscustomizer.HttpsCustomizerProperties;
import io.netty.pkitesting.CertificateBuilder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

@Accessors(chain = true)
@Data
@ConfigurationProperties(prefix = "https-customizer")
public class HttpsCustomizerConfigurationProperties {
    private static final long TEN_YEARS_IN_MILLISECONDS = 315569260000L;

    /**
     * disabled by default
     */
    boolean enabled;

    /**
     * hostname for the cert
     */
    String fqdn = "CN=localhost, O=local, OU=host";

    /**
     * date before which the cert is not valid (defaults to now)
     */
    Date notBefore = new Date();

    /**
     * date to expire the cert (default 10 years from now)
     */
    Date notAfter = new Date(notBefore.getTime() + TEN_YEARS_IN_MILLISECONDS);

    /**
     * type of cert
     */
    CertificateBuilder.Algorithm algorithm = CertificateBuilder.Algorithm.ed25519;

    public HttpsCustomizerProperties toProps() {
        return PropertyMapper.INSTANCE.toProps(this);
    }
}
