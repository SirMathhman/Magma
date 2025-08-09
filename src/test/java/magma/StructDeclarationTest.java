package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.CompileAssert.assertInvalid;
import static magma.CompileAssert.assertValid;

/**
 * Tests for struct declaration implementation in the Magma compiler.
 * Verifies the conversion of struct declarations from Magma syntax to C syntax.
 */
public class StructDeclarationTest {

    /**
     * Tests basic empty struct declaration.
     * Tests that empty struct declarations are correctly compiled to C.
     */
    @Test
    @DisplayName("Should compile empty struct declaration")
    public void shouldCompileEmptyStructDeclaration() {
        // Empty struct declaration
        assertValid("struct Empty {}", "struct Empty {};");
    }

    /**
     * Tests that struct declarations require proper syntax.
     * Verifies that syntax errors are caught when required elements are missing.
     */
    @Test
    @DisplayName("Should require proper syntax for struct declarations")
    public void shouldRequireProperStructSyntax() {
        // Missing struct keyword
        assertInvalid("Empty {}");
        
        // Missing struct name
        assertInvalid("struct {}");
        
        // Missing opening curly brace
        assertInvalid("struct Empty }");
        
        // Missing closing curly brace
        assertInvalid("struct Empty {");
    }
}