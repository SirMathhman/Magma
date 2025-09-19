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
}
