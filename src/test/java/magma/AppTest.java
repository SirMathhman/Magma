package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {
    @Test
    public void testGreet() {
        assertEquals("Hello, magma", App.greet());
    }

    @Test
    public void testInterpretNull() {
        try {
            assertEquals("", App.interpret(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInterpretHello() {
    assertThrows(magma.InterpretException.class, () -> App.interpret("hello"));
    assertThrows(magma.InterpretException.class, () -> App.interpret("  HeLLo  "));
    }

    @Test
    public void testInterpretPing() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("ping"));
    }

    @Test
    public void testInterpretRepeat() {
    assertThrows(magma.InterpretException.class, () -> App.interpret("repeat:abc"));
    assertThrows(magma.InterpretException.class, () -> App.interpret("repeat:123 456"));
    }

    @Test
    public void testInterpretUnknown() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("foobar"));
    }
}
