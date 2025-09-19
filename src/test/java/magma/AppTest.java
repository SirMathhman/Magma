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
        // non-allowed suffixes should NOT be accepted
        assertThrows(magma.InterpretException.class, () -> App.interpret("123abc"));
        assertThrows(magma.InterpretException.class, () -> App.interpret("+42xyz"));
        assertThrows(magma.InterpretException.class, () -> App.interpret("-7-foo"));
        // if no leading digits, previous behavior applies (exception)
        assertThrows(magma.InterpretException.class, () -> App.interpret("a123"));
    }

    @Test
    public void testAllowedSuffixes() throws Exception {
        assertEquals("5", App.interpret("5U8"));
        assertEquals("10", App.interpret("10U32"));
        assertEquals("7", App.interpret("7I16"));
    }

    @Test
    public void testDisallowedSuffix() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("5XYZ"));
    }

    @Test
    public void testInterpretAddition() throws Exception {
        assertEquals("5", App.interpret("2 + 3"));
        assertEquals("0", App.interpret("1 + -1"));
        assertEquals("100", App.interpret("40+60"));
        // invalid addition forms should not be parsed
        assertThrows(magma.InterpretException.class, () -> App.interpret("+ 3 + 2"));
    }

    @Test
    public void testInterpretAdditionWithSuffix() throws Exception {
        assertEquals("5", App.interpret("2 + 3I32"));
        // if both operands have suffixes, the operation is not allowed
        assertThrows(magma.InterpretException.class, () -> App.interpret("2I16+3U8"));
        // mixed invalid suffix should not parse
        assertThrows(magma.InterpretException.class, () -> App.interpret("2 + 3XYZ"));
    }

    @Test
    public void testAdditionBothSuffixesNotAllowed() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("2U8 + 3I32"));
    }

    @Test
    public void testAdditionSameSuffixAllowed() throws Exception {
        assertEquals("5", App.interpret("2I32 + 3I32"));
    }

    @Test
    public void testInterpretChainedAddition() throws Exception {
        assertEquals("6", App.interpret("1 + 2 + 3"));
        assertEquals("0", App.interpret("1 + -1 + 0"));
    }

    @Test
    public void testInterpretSubtraction() throws Exception {
        assertEquals("1", App.interpret("3 - 2"));
        assertEquals("-1", App.interpret("2 - 3"));
    }

    @Test
    public void testInterpretMultiplication() throws Exception {
        assertEquals("6", App.interpret("3 * 2"));
        // multiplication has higher precedence than addition
        assertEquals("14", App.interpret("2 + 3 * 4"));
    }

    @Test
    public void testOperatorPrecedence() throws Exception {
        assertEquals("7", App.interpret("1 + 2 * 3"));
    }

    @Test
    public void testParentheses() throws Exception {
        assertEquals("1", App.interpret("(1)"));
        assertEquals("9", App.interpret("(2 + 1) * 3"));
    }
}
