package info.ankin.projects.cli.messagebus.mixin;

import info.ankin.projects.cli.messagebus.model.BrokerType;
import lombok.Data;
import picocli.CommandLine;

@Data
@CommandLine.Command(resourceBundle = "info.ankin.projects.cli.messagebus.mixin.Mixin")
public class ConnectionInfo {
    @CommandLine.Option(names = {"-t", "--broker-type"},
            descriptionKey = "brokerType.descriptionKey")
    BrokerType brokerType;

    @CommandLine.Mixin
    Credentials credentials;
}
