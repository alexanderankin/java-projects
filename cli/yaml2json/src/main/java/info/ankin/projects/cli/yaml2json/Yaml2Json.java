package info.ankin.projects.cli.yaml2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;

import java.util.Arrays;

public class Yaml2Json {
    @SneakyThrows
    public static void main(String[] args) {
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        YAMLMapper yamlMapper = new YAMLMapper();

        // https://yaml.org/spec/1.2.2/#92-streams
        boolean stream = Arrays.asList(args).contains("--stream");

        try (var parser = yamlMapper.createParser(System.in);
             var generator = jsonMapper.createGenerator(System.out)) {
            parser.nextToken();
            if (parser.hasCurrentToken() && !stream)
                generator.copyCurrentStructure(parser);
            else if (parser.hasCurrentToken() && stream) {
                generator.writeStartArray();
                while (parser.hasCurrentToken()) {
                    generator.copyCurrentStructure(parser);
                    parser.nextToken();
                    if (!parser.hasCurrentToken())
                        break;
                    generator.writeRaw(',');
                }
                generator.writeEndArray();
            }
        }
        System.out.println();
    }
}
