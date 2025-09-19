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
    public void testNumPrefix() throws Exception {
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
    public void testAddWithSuffix() throws Exception {
        assertEquals("5", App.interpret("2 + 3I32"));
        // if both operands have suffixes, the operation is not allowed
        assertThrows(magma.InterpretException.class, () -> App.interpret("2I16+3U8"));
        // mixed invalid suffix should not parse
        assertThrows(magma.InterpretException.class, () -> App.interpret("2 + 3XYZ"));
    }

    @Test
    public void testBothSuffixesNA() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("2U8 + 3I32"));
    }

    @Test
    public void testSameSuffixAllowed() throws Exception {
        assertEquals("5", App.interpret("2I32 + 3I32"));
    }

    @Test
    public void testChainedAdd() throws Exception {
        assertEquals("6", App.interpret("1 + 2 + 3"));
        assertEquals("0", App.interpret("1 + -1 + 0"));
    }

    @Test
    public void testInterpretSubtraction() throws Exception {
        assertEquals("1", App.interpret("3 - 2"));
        assertEquals("-1", App.interpret("2 - 3"));
    }

    @Test
    public void testMultiplication() throws Exception {
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

    @Test
    public void testLetBinding() throws Exception {
        assertEquals("1", App.interpret("let x : I32 = 1; x"));
        assertEquals("14", App.interpret("let a:I32=2; let b:I32=3; a + b * 4"));
    }

    @Test
    public void testLetRedeclThrows() {
        // redeclaring the same variable in the same statement sequence should error
        assertThrows(magma.InterpretException.class, () -> App.interpret("let x = 1; let x = 1;"));
    }

    @Test
    public void testLetTypeMismatch() {
        // initializer has suffix U8 but declared type is I32 — should error
        assertThrows(magma.InterpretException.class, () -> App.interpret("let x : I32= 1U8;"));
    }

    @Test
    public void testLetNoTypeReturnsOne() throws Exception {
        assertEquals("1", App.interpret("let x = 1; x"));
    }

    @Test
    public void testLetAssignMismatchU() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("let x : U8 = 1; let y : U16 = x;"));
    }

    @Test
    public void testMutLetAssignThenRead() throws Exception {
        assertEquals("1", App.interpret("let mut x = 0; x = 1; x"));
    }

    @Test
    public void testLetDoubleSpaceInvalid() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("let  x = 0; x = 1; x"));
    }

    @Test
    public void testMutDeclNoInit() throws Exception {
        assertEquals("1", App.interpret("let mut x : I32; x = 0; x = 1; x"));
    }

    @Test
    public void testLetNoInitNotMutable() {
        assertThrows(magma.InterpretException.class, () -> App.interpret("let x : I32; x = 0; x = 1; x"));
    }

    @Test
    public void testPointerDeref() throws Exception {
        assertEquals("10", App.interpret("let x : I32 = 10; let y : *I32 = &x; *y"));
    }

    @Test
    public void testMutPointerDerefAssign() throws Exception {
        assertEquals("10", App.interpret("let mut x = 0; let y : *mut I32 = &mut x; *y = 10; x"));
    }
}
