package info.ankin.how.spring.logging.json;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@AutoConfiguration
@ComponentScan(basePackageClasses = JsonFormatListenerConfig.class)
public class JsonFormatListenerConfig {

    @Data
    @Accessors(chain = true)
    @ConfigurationProperties(Props.PREFIX)
    @Component
    public static class Props {
        public static final String PREFIX = "json-logging-listener";

        /**
         * should the listener attempt to set the json encoder on all registered appender instances
         */
        boolean enabled = true;

        /**
         * fail if any appender instances are not configurable (supported appender types: console)
         */
        boolean strict = false;
    }

}
