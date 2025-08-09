package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.CompileAssert.assertValid;

/**
 * Tests for automatic type inference of floating-point literals in the Magma compiler.
 * Verifies that literals with decimal points (like 0.0) are automatically inferred as F32 type.
 */
public class FloatInferenceTest {

    /**
     * Tests that floating-point literals are automatically inferred as F32 type.
     */
    @Test
    @DisplayName("Should infer F32 type for floating-point literals")
    public void shouldInferF32TypeForFloatingPointLiterals() {
        // Basic floating-point literal without explicit type annotation or suffix
        assertValid("let x = 0.0;", "float x = 0.0;");
        
        // Non-zero floating-point literal
        assertValid("let pi = 3.14159;", "float pi = 3.14159;");
        
        // Negative floating-point literal
        assertValid("let temp = -273.15;", "float temp = -273.15;");
    }

    /**
     * Tests that mutable variables with floating-point literals are inferred as F32.
     */
    @Test
    @DisplayName("Should infer F32 type for mutable floating-point variables")
    public void shouldInferF32TypeForMutableFloatingPointVariables() {
        // Mutable variable with floating-point literal
        assertValid("let mut value = 0.0; value = 1.5;", 
                    "float value = 0.0; value = 1.5;");
    }
}