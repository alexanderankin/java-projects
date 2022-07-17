package info.ankin.projects.cli.messagebus.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Serdeable
public class Settings {
    /**
     * default broker type
     */
    BrokerType type;
    String identifier;
    String group;
}
