package info.ankin.projects.cli.messagebus.service;

import info.ankin.projects.cli.messagebus.model.BrokerInformation;
import info.ankin.projects.cli.messagebus.model.BrokerType;
import reactor.core.publisher.Mono;

public interface Client {
    /**
     * What broker type does it support
     */
    BrokerType type();

    Mono<Void> init(BrokerInformation brokerInformation);
}
