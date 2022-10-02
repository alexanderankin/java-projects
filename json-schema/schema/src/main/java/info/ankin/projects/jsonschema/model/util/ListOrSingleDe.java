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
 * @param <I> single item type
 * @param <W> the wrapping type (must have constructors for {@code C}, {@code List<C>})
 */
public class ListOrSingleDe<I, W> extends JsonDeserializer<W> {
    private final ObjectMapper objectMapper;
    private final Class<I> iClass;
    private final TypeReference<List<I>> typeReference;
    private final Function<I, W> wCtorTakingI;
    private final Function<List<I>, W> wCtorTakingIList;

    public ListOrSingleDe(ObjectMapper objectMapper,
                          Class<I> iClass,
                          Class<W> wClass) {
        this.objectMapper = objectMapper;
        this.iClass = iClass;
        typeReference = new TypeReference<>() {
        };

        Constructor<? extends W> wConstructor = getOrNull(() -> getConstructor(iClass, wClass));
        Objects.requireNonNull(wConstructor, () -> "no t constructor for argument 'c' where t = " + wClass.getName() + " and c is " + iClass.getName());
        wCtorTakingI = ThrowableFunctionUtils.wrap(wConstructor::newInstance);

        Constructor<? extends W> wListConstructor = getOrNull(() -> getConstructor(List.class, wClass));
        Objects.requireNonNull(wListConstructor, () -> "no t constructor for argument j.u.List where t = " + wClass.getName());
        wCtorTakingIList = ThrowableFunctionUtils.wrap(wListConstructor::newInstance);
    }

    @Override
    public W deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode treeNode = p.getCodec().readTree(p);

        // if we are a string, then it's the only one, otherwise it is an array
        if (!(treeNode instanceof ArrayNode)) {
            return wCtorTakingI.apply(objectMapper.treeToValue(treeNode, iClass));
        } else {
            return wCtorTakingIList.apply(objectMapper.convertValue(treeNode, typeReference));
        }
    }

    private <T> T getOrNull(Callable<T> supplier) {
        try {
            return supplier.call();
        } catch (Exception ignored) {
            return null;
        }
    }

    private Constructor<? extends W> getConstructor(Class<?> iClass,
                                                    Class<W> wClass) {
        List<JsonSubTypes.Type> subTypes = Optional.ofNullable(wClass.getAnnotation(JsonSubTypes.class))
                .map(JsonSubTypes::value)
                .map(List::of)
                .orElseGet(List::of);

        for (JsonSubTypes.Type subType : subTypes) {
            @SuppressWarnings("unchecked")
            Class<? extends W> subTypeValue = (Class<? extends W>) subType.value();

            Constructor<? extends W> orNull = getOrNull(() -> subTypeValue.getConstructor(iClass));
            if (orNull != null) return orNull;
        }


        throw new MissingCompatibleSubTypeException();
    }

    private static class MissingCompatibleSubTypeException extends RuntimeException {
    }
}
