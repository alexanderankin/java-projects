package info.ankin.projects.jsonschema.model.v4;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

class SchemaV4Test {

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.registerModule(new SimpleModule() {
            {
                addDeserializer(Schema.TypeField.class, new Schema.TypeField.SimpleTypeFieldDeserializer(objectMapper));
                addDeserializer(Schema.Items.class, new Schema.Items.ItemsDe(objectMapper));
                addDeserializer(Schema.Dependency.class, new Schema.Dependency.DependencyDe(objectMapper));
            }
        });
    }

    @SneakyThrows
    @Test
    void test() {
        String input = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/info/ankin/projects/jsonschema/model/specs/draft-04.json")), StandardCharsets.UTF_8);
        Schema schema = objectMapper.readValue(input, Schema.class);
        String output = objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(schema);

        System.out.println(input);
        System.out.println(schema);
        System.out.println(output);

        String roundTripThruJsonNode = objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(objectMapper.readTree(input));

        // is not true because of some property ordering?
        boolean equals = output.equals(roundTripThruJsonNode);

        System.out.println(equals);

    }

}
