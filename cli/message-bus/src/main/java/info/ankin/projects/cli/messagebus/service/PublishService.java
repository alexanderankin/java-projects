package info.ankin.projects.cli.messagebus.service;

import info.ankin.projects.cli.messagebus.mixin.ConnectionInfo;
import info.ankin.projects.cli.messagebus.model.BrokerInformation;
import info.ankin.projects.cli.messagebus.model.PublishRequest;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class PublishService extends MessageBusService {
    public Mono<Void> publish(PublishRequest publishRequest, ConnectionInfo connectionInfo) {
        BrokerInformation brokerInformation = settingsService.overlay(connectionInfo);
        return Mono.empty();
    }
}
