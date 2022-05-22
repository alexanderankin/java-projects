package info.ankin.projects.msgs.factory;

import info.ankin.projects.msgs.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

abstract class BaseConnectionFactory implements ConnectionFactory {
    static <T> List<T> toList(Iterable<T> iterable) {
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    protected static abstract class BaseConnection implements Connection {
    }

    protected static abstract class BaseConsumer implements Consumer {
    }

    protected static abstract class BaseProducer implements Producer {
    }
}
