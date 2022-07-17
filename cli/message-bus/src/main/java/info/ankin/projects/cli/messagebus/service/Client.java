package info.ankin.projects.cli.messagebus.service;

import info.ankin.projects.cli.messagebus.model.BrokerType;

public interface Client {
    /**
     * What broker type does it support
     */
    BrokerType type();
}
