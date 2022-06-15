package info.ankin.projects.wrapper.virsh.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

    @SneakyThrows
    @Test
    void test() {
        String s = "<network ipv6='yes' trustGuestRxFilters='no'>\n" +
                "  <name>default</name>\n" +
                "  <uuid>3e3fce45-4f53-4fa7-bb32-11f34168b82b</uuid>\n" +
                "  <metadata>\n" +
                "    <app1:foo xmlns:app1=\"http://app1.org/app1/\">..</app1:foo>\n" +
                "    <app2:bar xmlns:app2=\"http://app1.org/app2/\">..</app2:bar>\n" +
                "  </metadata>\n" +
                "</network>";

        ObjectMapper objectMapper = new XmlMapper();
        Network network = objectMapper.readValue(s, Network.class);
        System.out.println("there: " + network);

        String back = objectMapper.writeValueAsString(network);
        System.out.println("back: " + back);
    }

}
