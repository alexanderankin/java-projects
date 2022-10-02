package info.ankin.projects.jsonschema.model.util;

import lombok.SneakyThrows;

import java.util.function.Function;

public interface ThrowableFunctionUtils {
    static <T, R> Function<T, R> wrap(ThrowableFunction<T, R> tf) {
        return tf::apply;
    }

    interface ThrowableFunction<T, R> {
        R applyThrowable(T t) throws Exception;

        @SneakyThrows
        default R apply(T t) {
            return applyThrowable(t);
        }
    }
}
