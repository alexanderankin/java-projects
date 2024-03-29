package info.ankin.projects.throwables;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class TTest {
    static Exception e() {
        return new Exception("oh no");
    }

    @Test
    void showUsage() {
        class MyOperation {
            String process(String input) throws Exception {
                if (input == null) throw new Exception("'input' was null");
                return input.toUpperCase();
            }
        }

        MyOperation o = new MyOperation();
        Stream.of("a", "b", "c")
                .map(T.of(o::process))
                .forEach(log::debug);
    }

    @Test
    void test_ofThrowingRunnable() {
        T.ThrowingRunnable t = () -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).run());
    }

    @Test
    void test_ofThrowingBiConsumer() {
        T.ThrowingBiConsumer<String, Integer> t = (a, b) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).accept("hello", 1));
    }

    @Test
    void test_ofThrowingBiFunction() {
        T.ThrowingBiFunction<String, Integer, Void> t = (a, b) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).apply("hello", 1));
    }

    @Test
    void test_ofThrowingBinaryOperator() {
        T.ThrowingBinaryOperator<String> t = (a, b) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).apply("hello", "world"));
    }

    @Test
    void test_ofThrowingBiPredicate() {
        T.ThrowingBiPredicate<String, Integer> t = (a, b) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).test("hello", 1));
    }

    @Test
    void test_ofThrowingConsumer() {
        T.ThrowingConsumer<String> t = (a) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).accept("hello"));
    }

    @Test
    void test_ofThrowingFunction() {
        T.ThrowingFunction<String, Integer> t = (a) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).apply("hello"));
    }

    @Test
    void test_ofThrowingPredicate() {
        T.ThrowingFunction<String, Integer> t = (a) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).apply("hello"));
    }

    @Test
    void test_ofThrowingSupplier() {
        T.ThrowingSupplier<String> t = () -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).get());
    }

    @Test
    void test_ofThrowingUnaryOperator() {
        T.ThrowingUnaryOperator<String> t = (a) -> {
            throw e();
        };
        assertThrows(Exception.class, () -> T.of(t).apply("hello"));
    }
}
