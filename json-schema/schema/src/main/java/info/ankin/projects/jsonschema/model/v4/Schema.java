package info.ankin.projects.jsonschema.model.v4;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.ankin.projects.jsonschema.model.Holder;
import info.ankin.projects.jsonschema.model.annotation.Format;
import info.ankin.projects.jsonschema.model.util.ListOrSingleDe;
import info.ankin.projects.jsonschema.model.util.ListXOrSingleYDe;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/json-schema-org/json-schema-spec/blob/dba92b702c94858162f653590230e7573c8b7dd0/schema.json">V4 schema Schema</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "id",
        "$schema",
        "title",
        "description",
        "default",
        "multipleOf",
        "maximum",
        "exclusiveMaximum",
        "minimum",
        "exclusiveMinimum",
        "maxLength",
        "minLength",
        "pattern",
        "additionalItems",
        "items",
        "maxItems",
        "minItems",
        "uniqueItems",
        "maxProperties",
        "minProperties",
        "required",
        "additionalProperties",
        "definitions",
        "properties",
        "patternProperties",
        "dependencies",
        "enum",
        "type",
        "format",
        "allOf",
        "anyOf",
        "oneOf",
        "not",
})
public class Schema {
    private String id;
    @JsonProperty("$schema")
    private String schema;
    private String title;
    private String description;
    @JsonProperty("default")
    private JsonNode default_;
    @Positive
    private Integer multipleOf;
    private Integer maximum;
    private Boolean exclusiveMaximum;
    private Integer minimum;
    private Boolean exclusiveMinimum;
    private Integer maxLength;
    private Integer minLength;
    @Format("regex")
    private String pattern;
    private List<String> required;
    private AdditionalItems additionalItems;
    private Object items; // todo
    private Integer minItems;
    private Integer maxItems;
    private LinkedHashMap<String, Schema> definitions; // todo
    private LinkedHashMap<String, Schema> properties;
    private LinkedHashMap<String, Schema> patternProperties;
    private LinkedHashMap<String, Dependency> dependencies;
    @JsonProperty("enum")
    private List<String> enumValues;
    private TypeField type;
    private String format;

    // allOf anyOf oneOf not

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonSubTypes({
            @JsonSubTypes.Type(Items.ItemsSchema.class),
            @JsonSubTypes.Type(Items.ItemsSchemaArray.class),
    })
    public interface Items {
        class ItemsSchema extends Holder<Schema> implements Items {
            public ItemsSchema(Schema schema) {
                super(schema);
            }
        }

        class ItemsSchemaArray extends Holder<List<Schema>> implements Items {
            public ItemsSchemaArray(List<Schema> schemas) {
                super(schemas);
            }
        }

        class ItemsDe extends ListOrSingleDe<Schema, Items> {
            public ItemsDe(ObjectMapper objectMapper) {
                super(objectMapper, Schema.class, Items.class);
            }
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonSubTypes({
            @JsonSubTypes.Type(Dependency.DependencySchema.class),
            @JsonSubTypes.Type(Dependency.DependencyStringArray.class),
    })
    public interface Dependency {
        class DependencySchema extends Holder<Schema> implements Dependency {
            public DependencySchema(Schema schema) {
                super(schema);
            }
        }

        class DependencyStringArray extends Holder<List<String>> implements Dependency {
            public DependencyStringArray(List<String> strings) {
                super(strings);
            }
        }

        class DependencyDe extends ListXOrSingleYDe<String, Schema, Dependency> {
            public DependencyDe(ObjectMapper objectMapper) {
                super(objectMapper, Schema.class, Dependency.class);
            }
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonSubTypes({
            @JsonSubTypes.Type(AdditionalItems.AdditionalItemsBoolean.class),
            @JsonSubTypes.Type(AdditionalItems.AdditionalItemsSchema.class),
    })
    public static abstract class AdditionalItems {
        @EqualsAndHashCode(callSuper = false)
        @Data
        public static class AdditionalItemsBoolean extends AdditionalItems {
            @JsonIgnore
            private final java.lang.Boolean value;

            @JsonCreator
            public AdditionalItemsBoolean(java.lang.Boolean value) {
                this.value = value;
            }

            @JsonValue
            public java.lang.Boolean getValue() {
                return value;
            }
        }

        @EqualsAndHashCode(callSuper = false)
        @Data
        public static class AdditionalItemsSchema extends AdditionalItems {
            @JsonIgnore
            private final Schema value;

            @JsonCreator
            public AdditionalItemsSchema(Schema value) {
                this.value = value;
            }

            @JsonValue
            public Schema getValue() {
                return value;
            }
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonSubTypes({
            @JsonSubTypes.Type(TypeField.SimpleTypeTypeField.class),
            @JsonSubTypes.Type(TypeField.ArrayOfSimpleTypeTypeField.class),
    })
    public static abstract class TypeField {
        public enum SimpleType {
            @JsonProperty("array") ARRAY,
            @JsonProperty("boolean") BOOLEAN,
            @JsonProperty("integer") INTEGER,
            @JsonProperty("null") NULL,
            @JsonProperty("number") NUMBER,
            @JsonProperty("object") OBJECT,
            @JsonProperty("string") STRING,
        }

        @EqualsAndHashCode(callSuper = false)
        @Data
        public static class SimpleTypeTypeField extends TypeField {
            @JsonIgnore
            private final SimpleType simpleType;

            @JsonCreator
            public SimpleTypeTypeField(SimpleType simpleType) {
                this.simpleType = simpleType;
            }

            @JsonValue
            public SimpleType getSimpleType() {
                return simpleType;
            }
        }

        @EqualsAndHashCode(callSuper = false)
        @Data
        public static class ArrayOfSimpleTypeTypeField extends TypeField {
            @JsonIgnore
            private final List<SimpleType> data;

            @JsonCreator
            public ArrayOfSimpleTypeTypeField(List<SimpleType> data) {
                this.data = data;
            }

            @JsonValue
            public List<SimpleType> getData() {
                return data;
            }
        }

        public static class SimpleTypeFieldDeserializer extends ListOrSingleDe<SimpleType, TypeField> {
            public SimpleTypeFieldDeserializer(ObjectMapper objectMapper) {
                super(objectMapper, SimpleType.class, TypeField.class);
            }
        }
    }
}
