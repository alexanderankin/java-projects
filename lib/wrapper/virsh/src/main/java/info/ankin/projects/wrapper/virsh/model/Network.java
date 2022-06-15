package info.ankin.projects.wrapper.virsh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Accessors(chain = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Network {
    @JacksonXmlProperty(isAttribute = true)
    String ipv6;
    @JacksonXmlProperty(isAttribute = true)
    String trustGuestRxFilters;
    String name;
    UUID uuid;
    JsonNode metadata;

}
