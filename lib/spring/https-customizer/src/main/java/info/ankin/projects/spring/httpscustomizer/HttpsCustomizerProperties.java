package info.ankin.projects.spring.httpscustomizer;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Accessors(chain = true)
@Data
public class HttpsCustomizerProperties {
    private static final long TEN_YEARS_IN_MILLISECONDS = 315569260000L;

    String fqdn = "localhost";
    Integer bits = 2048;
    Date notBefore = new Date();
    Date notAfter = new Date(notBefore.getTime() + TEN_YEARS_IN_MILLISECONDS);
    String algorithm = "RSA";
}
