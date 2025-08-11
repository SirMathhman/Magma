package magma;

import org.junit.jupiter.api.Test;

class InvalidExpressionTest extends CompilerTestBase {

    @Test
    void invalidExpressionDanglingOperator() {
        assertInvalid("let x = 5 +;");
    }

    @Test
    void invalidExpressionMissingOperand() {
        assertInvalid("let x = + 5;");
    }

    @Test
    void invalidExpressionUnbalancedParens() {
        assertInvalid("let x = (5 + 3;");
    }

    @Test
    void invalidExpressionDoubleOperator() {
        assertInvalid("let x = 5 ++ 3;");
    }

    @Test
    void invalidReturnMissingValue() {
        assertInvalid("return;");
    }

    @Test
    void invalidStringLiteralUnterminated() {
        assertInvalid("let x = \"hello;");
    }

    @Test
    void invalidStringLiteralUnterminatedAtEOF() {
        assertInvalid("let x = \"hello");
    }
}