package info.ankin.projects.cli.yaml2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;

public class Yaml2Json {
    @SneakyThrows
    public static void main(String[] args) {
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        YAMLMapper yamlMapper = new YAMLMapper();


        try (var parser = yamlMapper.createParser(System.in);
             var generator = jsonMapper.createGenerator(System.out)) {
            generator.copyCurrentStructure(parser);
        }
    }
}
