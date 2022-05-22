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
    int port;
    boolean ssl;
}
