package magma;

import org.junit.jupiter.api.Test;

class InvalidVariableTest extends CompilerTestBase {

    @Test
    void invalidVariableDeclarationMissingType() {
        assertInvalid("let x =;");
    }

    @Test
    void invalidVariableDeclarationMissingValue() {
        assertInvalid("let x : I32 =;");
    }

    @Test
    void invalidVariableDeclarationMissingName() {
        assertInvalid("let = 42;");
    }

    @Test
    void invalidVariableDeclarationMissingColon() {
        assertInvalid("let x I32 = 42;");
    }

    @Test
    void invalidVariableDeclarationMissingEquals() {
        assertInvalid("let x : I32 42;");
    }

    @Test
    void invalidVariableDeclarationEmptyName() {
        assertInvalid("let : I32 = 42;");
    }

    @Test
    void invalidAssignmentMissingValue() {
        assertInvalid("x =;");
    }

    @Test
    void invalidAssignmentToLiteral() {
        assertInvalid("42 = x;");
    }
}