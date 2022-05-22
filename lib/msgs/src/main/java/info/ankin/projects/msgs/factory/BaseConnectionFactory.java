package info.ankin.projects.msgs.factory;

import info.ankin.projects.msgs.ConnectionFactory;

abstract class BaseConnectionFactory implements ConnectionFactory {
    protected static abstract class BaseConnection implements Connection {
    }

    protected static abstract class BaseConsumer implements Consumer {
    }

    protected static abstract class BaseProducer implements Producer {
    }
}
