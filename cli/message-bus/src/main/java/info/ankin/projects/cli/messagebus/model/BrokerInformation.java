package info.ankin.projects.cli.messagebus.model;

import info.ankin.projects.cli.messagebus.mixin.ConnectionInfo;
import lombok.Value;

/**
 * {@link ConnectionInfo}, after overlay with {@link Settings}.
 */
@Value
public class BrokerInformation {
    BrokerType brokerType;
    String host;
    String virtualHost;
    String username;
    char[] password;
}
