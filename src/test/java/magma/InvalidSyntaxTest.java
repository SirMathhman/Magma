package magma;

import org.junit.jupiter.api.Test;

class InvalidSyntaxTest extends CompilerTestBase {

    // === General Syntax Tests ===
    @Test
    void invalidRandomTokens() {
        assertInvalid("random tokens that make no sense");
    }

    @Test
    void invalidEmptyBraces() {
        assertInvalid("{}");
    }

    @Test
    void invalidStrayBrace() {
        assertInvalid("}");
    }

    @Test
    void invalidStrayParen() {
        assertInvalid(")");
    }

    @Test
    void invalidStrayBracket() {
        assertInvalid("]");
    }

    @Test
    void invalidMultipleStatementErrors() {
        assertInvalid("let x =; fn test() { invalid }");
    }

    @Test
    void invalidNestedErrors() {
        assertInvalid("fn test() => { let x =; return; }");
    }

    @Test
    void invalidIfStatement() {
        assertInvalid("if true");
    }

    @Test
    void invalidWhileLoop() {
        assertInvalid("while true");
    }
}