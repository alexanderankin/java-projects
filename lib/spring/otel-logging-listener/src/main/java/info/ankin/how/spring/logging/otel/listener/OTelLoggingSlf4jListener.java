package info.ankin.how.spring.logging.otel.listener;

import ch.qos.logback.classic.LoggerContext;
import info.ankin.how.spring.logging.otel.appender.OpenTelemetryAppender;
import info.ankin.how.spring.logging.otel.listener.OTelLoggingListenerConfig.Props;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.composite.LogstashVersionJsonProvider;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class OTelLoggingSlf4jListener implements SpringApplicationRunListener {
    public static final String NOT_ENABLED_MESSAGE =
            "OTelLoggingSlf4jListener is not enabled - set " + Props.PREFIX + ".enabled=true to enabled";
    public static final String NO_LOGBACK_MESSAGE =
            "No configuration occurred because logger is not logback: " +
                    "logger factory is not a (logback) logger context";
    public static final String STRICT_MESSAGE =
            "OTelLoggingSlf4jListener cannot let you proceed with un-configured appender instances. " +
                    "This would result in logs being encoded without being formatted as json " +
                    "and this is not allowed when the strict option is selected.";
    public static final String STRICT_MESSAGE_NO_LOGBACK = STRICT_MESSAGE + " " + NO_LOGBACK_MESSAGE;
    public static final String STRICT_PROP = Props.PREFIX + ".strict";
    public static final String REGISTERED_MESSAGE = "registered OTelLoggingSlf4jListener";

    public OTelLoggingSlf4jListener(SpringApplication application, String[] args) {
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                    ConfigurableEnvironment environment) {
        Props props = Binder.get(environment).bindOrCreate(Props.PREFIX, Props.class);

        if (!props.isEnabled()) {
            System.err.println(NOT_ENABLED_MESSAGE);
            return;
        }

        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();

        if (!(iLoggerFactory instanceof LoggerContext loggerContext)) {
            System.err.println(NO_LOGBACK_MESSAGE);
            if (props.isStrict())
                throw new InvalidConfigurationPropertyValueException(STRICT_PROP, true, STRICT_MESSAGE_NO_LOGBACK);
            return;
        }

        LogstashEncoder logstashEncoder = new LogstashEncoder();

        // example of removing a default provider:
        logstashEncoder.getProviders().getProviders().stream()
                .filter(LogstashVersionJsonProvider.class::isInstance).findAny()
                .ifPresent(logstashEncoder.getProviders()::removeProvider);

        // don't forget to start logback components
        logstashEncoder.start();

        OpenTelemetry openTelemetrySdk =
                props.isRegisterGlobal()
                        ? AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk()
                        : AutoConfiguredOpenTelemetrySdk.builder().build().getOpenTelemetrySdk();

        OpenTelemetryAppender newAppender = new OpenTelemetryAppender(logstashEncoder, openTelemetrySdk);
        newAppender.start();
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(newAppender);
        log.info(REGISTERED_MESSAGE);
    }
}
