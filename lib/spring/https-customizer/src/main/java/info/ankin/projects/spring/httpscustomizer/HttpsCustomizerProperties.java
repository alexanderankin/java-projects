package info.ankin.projects.spring.httpscustomizer;

import io.netty.pkitesting.CertificateBuilder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class HttpsCustomizerProperties {
    private static final long TEN_YEARS_IN_MILLISECONDS = 315569260000L;

    String fqdn = "CN=localhost, O=local, OU=host";
    Date notBefore = new Date();
    Date notAfter = new Date(notBefore.getTime() + TEN_YEARS_IN_MILLISECONDS);
    CertificateBuilder.Algorithm algorithm = CertificateBuilder.Algorithm.ed25519;
}
