package magma;

import org.junit.jupiter.api.Test;

class InvalidFunctionTest extends CompilerTestBase {

    @Test
    void invalidFunctionMissingName() {
        assertInvalid("fn () => {}");
    }

    @Test
    void invalidFunctionMissingParens() {
        assertInvalid("fn test => {}");
    }

    @Test
    void invalidFunctionMissingArrow() {
        assertInvalid("fn test() {}");
    }

    @Test
    void invalidFunctionMissingBody() {
        assertInvalid("fn test() =>");
    }

    @Test
    void invalidFunctionMismatchedBraces() {
        assertInvalid("fn test() => {");
    }

    @Test
    void invalidFunctionExtraBraces() {
        assertInvalid("fn test() => {}}");
    }

    @Test
    void invalidFunctionParameterSyntax() {
        assertInvalid("fn test(x y : I32) => {}");
    }

    @Test
    void invalidFunctionCallMissingParens() {
        assertInvalid("test 42");
    }

    @Test
    void invalidFunctionCallUnbalancedParens() {
        assertInvalid("test(42, 34;");
    }
}