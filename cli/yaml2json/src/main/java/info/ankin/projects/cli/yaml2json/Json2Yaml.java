package info.ankin.projects.cli.yaml2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.SneakyThrows;

public class Json2Yaml {
    @SneakyThrows
    public static void main(String[] args) {
        JsonMapper jsonMapper = new JsonMapper();
        YAMLMapper yamlMapper = new YAMLMapper();
        yamlMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        JsonNode jsonNode = jsonMapper.reader().readTree(System.in);
        yamlMapper.writeValue(System.out, jsonNode);
        System.out.println();
    }
}
