package info.ankin.projects.throwables;

import lombok.SneakyThrows;

import java.util.function.*;

public class T {
    public static Runnable of(T.ThrowingRunnable other) {
        return other;
    }

    public static <T, U> BiConsumer<T, U> of(ThrowingBiConsumer<T, U> other) {
        return other;
    }

    public static <T, U, R> BiFunction<T, U, R> of(ThrowingBiFunction<T, U, R> other) {
        return other;
    }

    public static <T> BinaryOperator<T> of(ThrowingBinaryOperator<T> other) {
        return other;
    }

    public static <T, U> BiPredicate<T, U> of(ThrowingBiPredicate<T, U> other) {
        return other;
    }

    public static <T> Consumer<T> of(ThrowingConsumer<T> other) {
        return other;
    }

    public static <T, U> Function<T, U> of(ThrowingFunction<T, U> other) {
        return other;
    }

    public static <T> Predicate<T> of(ThrowingPredicate<T> other) {
        return other;
    }

    public static <T> Supplier<T> of(ThrowingSupplier<T> other) {
        return other;
    }

    public static <T> UnaryOperator<T> of(ThrowingUnaryOperator<T> other) {
        return other;
    }

    @FunctionalInterface
    public interface ThrowingRunnable extends Runnable {
        @SneakyThrows
        @Override
        default void run() {
            runUnsafely();
        }

        void runUnsafely() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingBiConsumer<T, U> extends BiConsumer<T, U> {
        @SneakyThrows
        @Override
        default void accept(T t, U u) {
            acceptUnsafely(t, u);
        }

        void acceptUnsafely(T t, U u) throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R> extends BiFunction<T, U, R> {
        @SneakyThrows
        @Override
        default R apply(T t, U u) {
            return applyUnsafely(t, u);
        }

        R applyUnsafely(T t, U u) throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingBinaryOperator<T> extends BinaryOperator<T> {
        @SneakyThrows
        @Override
        default T apply(T t, T t2) {
            return applyUnsafely(t, t2);
        }

        T applyUnsafely(T t, T t2) throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingBiPredicate<T, U> extends BiPredicate<T, U> {
        @SneakyThrows
        @Override
        default boolean test(T t, U u) {
            return testUnsafely(t, u);
        }

        boolean testUnsafely(T t, U u) throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> extends Consumer<T> {
        @SneakyThrows
        @Override
        default void accept(T t) {
            acceptUnsafely(t);
        }

        void acceptUnsafely(T t) throws Throwable;
    }


    @FunctionalInterface
    public interface ThrowingFunction<T, U> extends Function<T, U> {
        @SneakyThrows
        @Override
        default U apply(T t) {
            return applyUnsafely(t);
        }

        U applyUnsafely(T t) throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingPredicate<T> extends Predicate<T> {
        @SneakyThrows
        @Override
        default boolean test(T t) {
            return testUnsafely(t);
        }

        boolean testUnsafely(T t) throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> extends Supplier<T> {
        @SneakyThrows
        @Override
        default T get() {
            return getUnsafely();
        }

        T getUnsafely() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingUnaryOperator<T> extends UnaryOperator<T>, ThrowingFunction<T, T> {
        @SneakyThrows
        @Override
        default T apply(T t) {
            return applyUnsafely(t);
        }

        @Override
        T applyUnsafely(T t) throws Throwable;
    }
}
