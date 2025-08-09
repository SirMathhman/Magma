package magma.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertInvalid;
import static magma.core.CompileAssert.assertValid;

/**
 * Tests for double-precision float (F64) type declarations in the Magma compiler.
 * Verifies the conversion of F64 type declarations from Magma syntax to C syntax.
 */
public class F64DeclarationTest {

    /**
     * Tests basic F64 variable declaration with type annotation.
     * Tests that F64 type annotations are correctly compiled to C double type.
     */
    @Test
    @DisplayName("Should compile F64 type annotation")
    public void shouldCompileF64TypeAnnotation() {
        // Basic F64 variable declaration with type annotation
        assertValid("let value : F64 = 0.0;", "double value = 0.0;");

        // F64 variable with non-zero value
        assertValid("let pi : F64 = 3.14159265359;", "double pi = 3.14159265359;");

        // F64 variable with negative value
        assertValid("let temp : F64 = -273.15;", "double temp = -273.15;");
    }

    /**
     * Tests basic F64 variable declaration with type suffix.
     * Tests that F64 type suffix is correctly compiled to C double type.
     */
    @Test
    @DisplayName("Should compile F64 type suffix")
    public void shouldCompileF64TypeSuffix() {
        // Basic F64 variable declaration with type suffix
        assertValid("let value = 0.0F64;", "double value = 0.0;");

        // F64 variable with non-zero value
        assertValid("let pi = 3.14159265359F64;", "double pi = 3.14159265359;");

        // F64 variable with negative value
        assertValid("let temp = -273.15F64;", "double temp = -273.15;");
    }

    /**
     * Tests mutable F64 variable declarations.
     * Tests that mutable F64 variables can be declared and reassigned.
     */
    @Test
    @DisplayName("Should support mutable F64 variables")
    public void shouldSupportMutableF64Variables() {
        // Mutable F64 variable with type annotation
        assertValid("let mut value : F64 = 0.0; value = 1.5;", "double value = 0.0; value = 1.5;");

        // Mutable F64 variable with type suffix
        assertValid("let mut temp = 20.5F64; temp = 25.0;", "double temp = 20.5; temp = 25.0;");
    }

    /**
     * Tests F64 variable references.
     * Tests that F64 variables can be referenced in other F64 variable declarations.
     */
    @Test
    @DisplayName("Should support F64 variable references")
    public void shouldSupportF64VariableReferences() {
        // Reference to F64 variable in another F64 variable declaration
        assertValid("let x : F64 = 3.14; let y : F64 = x;", "double x = 3.14; double y = x;");
    }

    /**
     * Tests type compatibility validation for F64 variables.
     * Tests that type incompatibility is caught when assigning values of different types.
     */
    @Test
    @DisplayName("Should enforce F64 type compatibility")
    public void shouldEnforceTypeCompatibility() {
        // Type incompatibility with integer literal
        assertInvalid("let x : F64 = 42I32;");

        // Type incompatibility with variable reference
        assertInvalid("let x = 42I32; let y : F64 = x;");

        // Type incompatibility in reassignment
        assertInvalid("let mut x : F64 = 3.14; let y = 42I32; x = y;");
        
        // Type incompatibility with F32
        assertInvalid("let x = 3.14F32; let y : F64 = x;");
    }
}