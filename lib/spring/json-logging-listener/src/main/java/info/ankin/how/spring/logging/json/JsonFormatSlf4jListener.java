package info.ankin.how.spring.logging.json;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.composite.LogstashVersionJsonProvider;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static info.ankin.how.spring.logging.json.JsonFormatListenerConfig.Props;

/**
 * the order must be positive to run after Application Listeners (EventPublishingRunListener)
 *
 * @see org.springframework.boot.context.logging.LoggingApplicationListener
 * @see org.springframework.boot.context.event.ApplicationPreparedEvent
 */
@Slf4j
@Order(10)
public class JsonFormatSlf4jListener implements SpringApplicationRunListener {
    public static final String NO_LOGBACK_MESSAGE =
            "No configuration occurred because logger is not logback: " +
                    "logger factory is not a (logback) logger context";
    public static final String STRICT_MESSAGE =
            "JsonFormatSlf4jListener cannot let you proceed with un-configured appender instances. " +
                    "This would result in logs being encoded without being formatted as json " +
                    "and this is not allowed when the strict option is selected.";
    public static final String STRICT_MESSAGE_NO_LOGBACK = STRICT_MESSAGE + " " + NO_LOGBACK_MESSAGE;
    public static final String STRICT_PROP = Props.PREFIX + ".strict";

    static final Set<Class<? extends Encoder<?>>> KNOWN_JSON_ENCODERS = Set.of(
            LogstashEncoder.class
    );

    // see parent class documentation
    @SuppressWarnings("unused")
    JsonFormatSlf4jListener(SpringApplication springApplication, String[] args) {
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                    ConfigurableEnvironment environment) {
        Props props = Binder.get(environment).bindOrCreate(Props.PREFIX, Props.class);

        if (!props.isEnabled()) {
            System.err.println("JsonFormatSlf4jListener is not enabled");
            return;
        }

        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();

        if (!(iLoggerFactory instanceof LoggerContext loggerContext)) {
            System.err.println(NO_LOGBACK_MESSAGE);
            if (props.isStrict())
                throw new InvalidConfigurationPropertyValueException(STRICT_PROP, true, STRICT_MESSAGE_NO_LOGBACK);
            return;
        }

        Iterable<Appender<ILoggingEvent>> appenderIterator =
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)::iteratorForAppenders;

        List<Appender<ILoggingEvent>> appenderList =
                StreamSupport.stream(appenderIterator.spliterator(), false)
                        .toList();

        var consoleAppenderList = appenderList.stream()
                .filter(ConsoleAppender.class::isInstance)
                .map(e -> (ConsoleAppender<ILoggingEvent>) e)
                .toList();

        if (props.isStrict() && consoleAppenderList.size() > appenderList.size()) {
            System.err.println(STRICT_MESSAGE);
            throw new InvalidConfigurationPropertyValueException(STRICT_PROP, props.isStrict(), STRICT_MESSAGE);
        }

        if (consoleAppenderList.isEmpty()) {
            log.debug("no console appender to modify, returning");
            return;
        }

        var encoderList = consoleAppenderList.stream()
                .map(OutputStreamAppender::getEncoder)
                .filter(e -> KNOWN_JSON_ENCODERS.stream().noneMatch(c -> c.isInstance(e)))
                .toList();

        if (encoderList.isEmpty()) {
            log.debug("all encoders already KNOWN_JSON_ENCODERS ({}), returning", KNOWN_JSON_ENCODERS);
            return;
        }

        LogstashEncoder logstashEncoder = new LogstashEncoder();

        // example of removing a default provider:
        logstashEncoder.getProviders().getProviders().stream()
                .filter(LogstashVersionJsonProvider.class::isInstance).findAny()
                .ifPresent(logstashEncoder.getProviders()::removeProvider);

        // don't forget to start logback components
        logstashEncoder.start();

        int fixed = 0;
        for (ConsoleAppender<ILoggingEvent> consoleAppender : consoleAppenderList) {
            if (encoderList.contains(consoleAppender.getEncoder())) {
                consoleAppender.setEncoder(logstashEncoder);
                fixed += 1;
            }
        }

        log.debug("fixed {} encoders on root's {} console appender instances", fixed, consoleAppenderList.size());
    }
}
