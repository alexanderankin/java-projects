package info.ankin.projects.cli.messagebus.service;

import jakarta.inject.Inject;

/**
 * Base class for services dealing with talking to brokers
 */
public abstract class MessageBusService {
    @Inject
    SettingsService settingsService;
}
