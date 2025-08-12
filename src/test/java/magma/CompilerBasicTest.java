package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerBasicTest {
    @Test
    void valid() {
        assertValid("", "");
    }

    @Test
    void let() {
        assertValid("let x = 100;", "int32_t x = 100;");
    }

    @Test
    void letName() {
        assertValid("let y = 100;", "int32_t y = 100;");
    }

    @Test
    void letValue() {
        assertValid("let z = 100;", "int32_t z = 100;");
    }

    @Test
    void letExplicitType() {
        assertValid("let a : I32 = 100;", "int32_t a = 100;");
    }

    @Test
    void letAnnotatedTyped() {
        assertValid("let x = 100I32;", "int32_t x = 100;");
    }

    @Test
    void invalidTypeAnnotation() {
        assertInvalid("let x : I32 = 0U64;");
    }

    @Test
    void invalid() {
        assertInvalid("not empty");
    }

    private void assertValid(String input, String output) {
        Compiler compiler = new Compiler();
        String actual = compiler.compile(input);
        assertEquals(output, actual);
    }

    private void assertInvalid(String input) {
        try {
            new Compiler().compile(input);
            fail("Expected CompileException to be thrown");
        } catch (CompileException e) {
            // expected
        }
    }
}
