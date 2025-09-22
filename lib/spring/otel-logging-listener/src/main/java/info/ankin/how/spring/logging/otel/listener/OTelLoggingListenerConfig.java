package info.ankin.how.spring.logging.otel.listener;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@AutoConfiguration
@ComponentScan(basePackageClasses = OTelLoggingListenerConfig.class)
public class OTelLoggingListenerConfig {
    @Data
    @Accessors(chain = true)
    @ConfigurationProperties(Props.PREFIX)
    @Component
    public static class Props {
        public static final String PREFIX = "otel-logging-listener";

        /**
         * should we run the listener
         */
        boolean enabled = true;

        /**
         * the difference between AutoConfiguredOpenTelemetrySdk.initialize() (global) vs the local: .builder().build()
         */
        boolean registerGlobal = false;

        /**
         * should fail is listener is enabled and logging system is not SLF4J (it is requested, but unable, to register)
         */
        boolean strict = false;
    }
}
