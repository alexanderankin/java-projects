package info.ankin.projects.msgs.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Information necessary to connect to a broker
 */
@Accessors(chain = true)
@Data
public class Configuration {
    String host;
    Integer port;
    boolean ssl;

    public String getHost() {
        if (host == null)
            return "localhost";

        return host;
    }
}
