package magma;

import org.junit.jupiter.api.Test;

class InvalidClassTest extends CompilerTestBase {

    @Test
    void invalidClassMissingName() {
        assertInvalid("class fn () => {}");
    }

    @Test
    void invalidClassMissingFn() {
        assertInvalid("class Calculator() => {}");
    }

    @Test
    void invalidClassMissingParens() {
        assertInvalid("class fn Calculator => {}");
    }

    @Test
    void invalidClassMissingBraces() {
        assertInvalid("class fn Calculator() =>");
    }

    @Test
    void invalidClassMismatchedBraces() {
        assertInvalid("class fn Calculator() => { fn method() => { }");
    }

    @Test
    void invalidStructMissingName() {
        assertInvalid("struct {};");
    }

    @Test
    void invalidStructMissingBraces() {
        assertInvalid("struct Point;");
    }

    @Test
    void invalidStructUnmatchedBraces() {
        assertInvalid("struct Point { x : I32;");
    }
}