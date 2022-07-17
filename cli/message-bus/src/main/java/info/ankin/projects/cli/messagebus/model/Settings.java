package info.ankin.projects.cli.messagebus.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Settings {
    /**
     * default broker type
     */
    BrokerType type;
}
