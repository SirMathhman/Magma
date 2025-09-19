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

    @Test
    public void testInterpretNumericPrefix() throws Exception {
        assertEquals("5", App.interpret("5U8"));
        assertEquals("123", App.interpret("123abc"));
        assertEquals("+42", App.interpret("+42xyz"));
        assertEquals("-7", App.interpret("-7-foo"));
        // if no leading digits, previous behavior applies (exception)
        assertThrows(magma.InterpretException.class, () -> App.interpret("a123"));
    }
}
