package info.ankin.projects.spring.httpscustomizer.properties;

import info.ankin.projects.spring.httpscustomizer.HttpsCustomizerProperties;
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
    String fqdn = "localhost";

    /**
     * how big of a cert should it be (default 2048)
     */
    Integer bits = 2048;

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
    Algorithm algorithm = Algorithm.RSA;

    public enum Algorithm { RSA, EC };

    public HttpsCustomizerProperties toProps() {
        return PropertyMapper.INSTANCE.toProps(this);
    }
}
