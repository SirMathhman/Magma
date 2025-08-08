package magma;

import org.junit.jupiter.api.Test;

/**
 * Tests for function declaration and usage in the Magma compiler.
 */
class FunctionTest extends BaseCompilerTest {
    @Test
    void compileFunctionDeclaration() {
        // Test the empty function declaration
        assertValid("fn empty() : Void => {}", "void empty() {}");
        
        // Test functions with different names
        assertValid("fn hello() : Void => {}", "void hello() {}");
        assertValid("fn calculateSum() : Void => {}", "void calculateSum() {}");
    }
}