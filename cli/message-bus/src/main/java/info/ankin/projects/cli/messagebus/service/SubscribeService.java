package info.ankin.projects.cli.messagebus.service;

import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class SubscribeService extends MessageBusService {
    public Mono<Void> subscribe() {
        return Mono.empty();
    }
}
