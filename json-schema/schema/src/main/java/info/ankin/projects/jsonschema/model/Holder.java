package info.ankin.projects.jsonschema.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class Holder<T> {
    private T t;

    @JsonCreator
    public Holder(T t) {
        this.t = t;
    }

    @JsonValue
    public T getT() {
        return t;
    }
}
