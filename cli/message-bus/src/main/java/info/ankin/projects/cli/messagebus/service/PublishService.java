package info.ankin.projects.cli.messagebus.service;

import info.ankin.projects.cli.messagebus.exception.NotYetImplementedException;
import info.ankin.projects.cli.messagebus.mixin.ConnectionInfo;
import info.ankin.projects.cli.messagebus.model.BrokerInformation;
import info.ankin.projects.cli.messagebus.model.BrokerType;
import info.ankin.projects.cli.messagebus.model.PublishRequest;
import info.ankin.projects.cli.messagebus.service.publish.Publisher;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class PublishService extends MessageBusService {
    private final Map<BrokerType, Publisher> publishers;

    public PublishService(List<Publisher> publishers) {
        this.publishers = publishers.stream().collect(Collectors.toMap(Publisher::type, Function.identity()));
    }

    public Mono<Void> publish(PublishRequest publishRequest, ConnectionInfo connectionInfo) {
        BrokerInformation brokerInformation = settingsService.overlay(connectionInfo);
        BrokerType type = brokerInformation.getBrokerType();
        Publisher publisher = publishers.get(type);

        if (publisher == null)
            throw new NotYetImplementedException("No publisher found for " + type);

        return publishRequest.getMessages()
                .flatMap(publisher::publish)
                .then();
    }
}
