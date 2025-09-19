package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    @Test
    public void testGreet() {
        assertEquals("Hello, magma", App.greet());
    }

    @Test
    public void testInterpretNull() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class, () -> App.interpret(null));
    }

    @Test
    public void testInterpretHello() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> App.interpret("hello"));
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> App.interpret("  HeLLo  "));
    }

    @Test
    public void testInterpretPing() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class, () -> App.interpret("ping"));
    }

    @Test
    public void testInterpretRepeat() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> App.interpret("repeat:abc"));
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> App.interpret("repeat:123 456"));
    }

    @Test
    public void testInterpretUnknown() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class,
                () -> App.interpret("foobar"));
    }
}
