package info.ankin.projects.cli.messagebus.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@ConfigurationProperties(value = "mb")
public class SettingsProperties {
    /**
     * User's config file location (resolved relative to home)
     */
    String config = ".config/message-bus/config.json";
}
