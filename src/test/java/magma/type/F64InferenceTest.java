package magma.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static magma.core.CompileAssert.assertValid;

/**
 * Tests for automatic type inference of double-precision floating-point literals in the Magma compiler.
 * Verifies that literals with F64 suffix are automatically inferred as F64 type.
 */
public class F64InferenceTest {

    /**
     * Tests that double-precision floating-point literals with F64 suffix are correctly inferred.
     */
    @Test
    @DisplayName("Should infer F64 type for F64-suffixed literals")
    public void shouldInferF64TypeForF64SuffixedLiterals() {
        // Basic double-precision floating-point literal with F64 suffix
        assertValid("let x = 0.0F64;", "double x = 0.0;");
        
        // Non-zero double-precision floating-point literal with F64 suffix
        assertValid("let pi = 3.14159265359F64;", "double pi = 3.14159265359;");
        
        // Negative double-precision floating-point literal with F64 suffix
        assertValid("let temp = -273.15F64;", "double temp = -273.15;");
    }

    /**
     * Tests that mutable variables with F64-suffixed literals are correctly inferred.
     */
    @Test
    @DisplayName("Should infer F64 type for mutable F64-suffixed variables")
    public void shouldInferF64TypeForMutableF64SuffixedVariables() {
        // Mutable variable with double-precision floating-point literal with F64 suffix
        assertValid("let mut value = 0.0F64; value = 1.5;", 
                   "double value = 0.0; value = 1.5;");
    }
    
    /**
     * Tests that F64 variables can be referenced in other F64 variable declarations through inference.
     */
    @Test
    @DisplayName("Should maintain F64 type in variable references")
    public void shouldMaintainF64TypeInVariableReferences() {
        // F64 variable used in another variable declaration
        assertValid("let x = 3.14F64; let y = x;", "double x = 3.14; double y = x;");
    }
}