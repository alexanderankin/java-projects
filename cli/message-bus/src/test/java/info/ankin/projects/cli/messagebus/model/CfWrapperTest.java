package info.ankin.projects.cli.messagebus.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CfWrapperTest {

    @Test
    void completesSuccesfully() {
        CfWrapper<Object> w = new CfWrapper<>();
        w.getCallback().accept("abc", null);
        Object join = w.getCf().join();
        assertInstanceOf(String.class, join);
    }

    @Test
    void completesExceptionally() {
        CfWrapper<Object> w = new CfWrapper<>();
        w.getCallback().accept(null, new RuntimeException());
        assertThrows(RuntimeException.class, w.getCf()::join);
    }

}
