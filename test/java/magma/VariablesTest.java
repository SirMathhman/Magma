package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.TestUtils.assertRun;

/**
 * Tests for variable declarations and operations in Magma.
 */
public class VariablesTest {
    @ParameterizedTest
    @ValueSource(strings = {"x", "y", "z"})
    void let(String name) {
        assertRun("let " + name + " = 10; " + name, "10");
    }

    @ParameterizedTest
    @ValueSource(strings = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"})
    void letWithType(String type) {
        assertRun("let test : " + type + " = 10; test", "10");
    }

    @Test
    void empty() {
        assertRun("", "");
    }

    @Test
    void block() {
        assertRun("{}", "");
    }

    @Test
    void blockLet() {
        assertRun(" { let x = 10; x }", "10");
    }

    @Test
    void multipleVariables() {
        assertRun("let x = 5; let y = 10; x + y", "15");
    }

    @Test
    void nestedBlocks() {
        assertRun("{ { let x = 5; x } }", "5");
    }

    @Test
    void complexArithmeticInNestedBlocks() {
        assertRun("{ let x = 5; { let y = 10; x * y } }", "50");
    }
}