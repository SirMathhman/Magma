package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest {
    @Test
    public void testInterpretEmpty() throws Exception {
        assertEquals("", App.interpret(null));
        assertEquals("", App.interpret("   "));
    }

    @Test
    public void testInterpretThrows() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("hello"));
    }

    @Test
    public void testInterpretNumeric() throws Exception {
        assertEquals("5", App.interpret("5"));
        assertEquals("123", App.interpret("123"));
        assertEquals("+42", App.interpret("+42"));
        assertEquals("-7", App.interpret("-7"));
    }
}
