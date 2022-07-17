package info.ankin.projects.cli.messagebus.mixin;

import info.ankin.projects.cli.messagebus.model.BrokerType;
import lombok.Data;
import picocli.CommandLine;

@Data
@CommandLine.Command(resourceBundle = "info.ankin.projects.cli.messagebus.mixin.Mixin")
public class ConnectionInfo {
    @CommandLine.Option(names = {"-T", "--broker-type"},
            descriptionKey = "brokerType.descriptionKey")
    BrokerType brokerType;

    @CommandLine.Option(names = {"-r", "--retries"},
            descriptionKey = "retries.descriptionKey")
    int retries;

    @CommandLine.Option(names = {"-i", "--identifier"},
            descriptionKey = "identifier.descriptionKey")
    String identifier;

    @CommandLine.Option(names = {"-t", "--topic"})
    String topic;

    @CommandLine.Option(names = {"-g", "--group"},
            descriptionKey = "group.descriptionKey")
    String group;

    @CommandLine.Mixin
    Credentials credentials;
}
