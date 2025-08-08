package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests for control flow structures in the Magma compiler.
 */
class ControlFlowTest extends BaseCompilerTest {
    @Test
    void compileBlockWithBraces() {
        assertValid("{}", "{}");
        assertValid("{let x = 100;}", "{int32_t x = 100;}");
        assertValid("let x = 100; {let y = x;}", "int32_t x = 100;{int32_t y = x;}");
        assertInvalid("{let x = 100;} let y = x;");
    }

    @Test
    void compileIfStatement() {
        assertValid("if (true) {}", "if (true) {}");
        assertValid("if (false) {}", "if (false) {}");
        assertValid("if (true) {let x = 100;}", "if (true) {int32_t x = 100;}");
        assertValid("let x = true; if (x) {let y = 200;}", "bool x = true; if (x) {int32_t y = 200;}");
        assertValid("let x = 1; let y = 2; if (x < y) {let z = 3;}",
                "int32_t x = 1; int32_t y = 2; if (x < y) {int32_t z = 3;}");
    }

    @Test
    void compileIfElseStatement() {
        assertValid("if (true) {} else {}", "if (true) {} else {}");
        assertValid("if (false) {} else {}", "if (false) {} else {}");
        assertValid("if (true) {let x = 100;} else {let y = 200;}", 
                "if (true) {int32_t x = 100;} else {int32_t y = 200;}");
        assertValid("let x = true; if (x) {let y = 200;} else {let z = 300;}",
                "bool x = true; if (x) {int32_t y = 200;} else {int32_t z = 300;}");
        assertValid("let x = 1; let y = 2; if (x < y) {let z = 3;} else {let z = 4;}",
                "int32_t x = 1; int32_t y = 2; if (x < y) {int32_t z = 3;} else {int32_t z = 4;}");

        // Test that else requires braces
        assertInvalid("if (true) {} else let x = 5;");
        assertInvalid("if (true) {let x = 1;} else let y = 2;");
    }

    @Test
    void compileWhileStatement() {
        assertValid("while (true) {}", "while (true) {}");
        assertValid("while (false) {}", "while (false) {}");
        assertValid("while (true) {let x = 100;}", "while (true) {int32_t x = 100;}");
        assertValid("let x = true; while (x) {let y = 200;}", "bool x = true; while (x) {int32_t y = 200;}");
        assertValid("let x = 1; let y = 2; while (x < y) {let z = 3;}",
                "int32_t x = 1; int32_t y = 2; while (x < y) {int32_t z = 3;}");
    }
}