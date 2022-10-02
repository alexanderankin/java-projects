package info.ankin.projects.jsonschema.model.util;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @param <X> list item type
 * @param <Y> single item type
 * @param <W> the wrapping type (must have constructors for {@code C}, {@code List<C>})
 */
public class ListXOrSingleYDe<X, Y, W> extends JsonDeserializer<W> {
    private final ObjectMapper objectMapper;
    private final Class<Y> yClass;
    private final TypeReference<List<X>> typeReference;
    private final Function<Y, W> wCtorTakingY;
    private final Function<List<X>, W> wCtorTakingXList;

    public ListXOrSingleYDe(ObjectMapper objectMapper,
                            Class<Y> yClass,
                            Class<W> wClass) {
        this.objectMapper = objectMapper;
        this.yClass = yClass;
        typeReference = new TypeReference<>() {
        };

        Constructor<? extends W> wConstructor = getOrNull(() -> this.getConstructor(yClass, wClass));
        Objects.requireNonNull(wConstructor, () -> "no t constructor for argument 'y' where t = " + wClass.getName() + " and y is " + yClass.getName());
        wCtorTakingY = ThrowableFunctionUtils.wrap(wConstructor::newInstance);

        Constructor<? extends W> wListConstructor = getOrNull(() -> getConstructor(List.class, wClass));
        Objects.requireNonNull(wListConstructor, () -> "no t constructor for argument j.u.List where t = " + wClass.getName());
        wCtorTakingXList = ThrowableFunctionUtils.wrap(wListConstructor::newInstance);
    }

    @Override
    public W deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode treeNode = p.getCodec().readTree(p);

        // if we are a string, then it's the only one, otherwise it is an array
        if (!(treeNode instanceof ArrayNode)) {
            return wCtorTakingY.apply(objectMapper.treeToValue(treeNode, yClass));
        } else {
            return wCtorTakingXList.apply(objectMapper.convertValue(treeNode, typeReference));
        }
    }

    private <T> T getOrNull(Callable<T> supplier) {
        try {
            return supplier.call();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Gets a constructor from while
     *
     * @param argumentClass the type of constructor we are looking for
     * @param wrapper wrapper class with json subtype annotations
     * @return constructor taking {@link P} and returning wrapper subtype {@link W}
     * @param <P> generic specifying the parameter type
     */
    private <P> Constructor<? extends W> getConstructor(Class<P> argumentClass,
                                                        Class<W> wrapper) {
        List<JsonSubTypes.Type> subTypes = Optional.ofNullable(wrapper.getAnnotation(JsonSubTypes.class))
                .map(JsonSubTypes::value)
                .map(List::of)
                .orElseGet(List::of);

        for (JsonSubTypes.Type subType : subTypes) {
            @SuppressWarnings("unchecked")
            Class<? extends W> subTypeValue = (Class<? extends W>) subType.value();

            Constructor<? extends W> orNull = getOrNull(() -> subTypeValue.getConstructor(argumentClass));
            if (orNull != null) return orNull;
        }


        throw new MissingCompatibleSubTypeException();
    }

    private static class MissingCompatibleSubTypeException extends RuntimeException {
    }
}
