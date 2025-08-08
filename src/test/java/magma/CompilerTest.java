package magma;

import org.junit.jupiter.api.Test;

/**
 * Main test class for Magma compiler.
 * Most tests have been moved to specialized test classes:
 * - PrimitiveTypeTest: for basic types and variables
 * - ControlFlowTest: for if/else and loop statements
 * - StructTest: for struct declarations and usage
 * - FunctionTest: for function declarations and usage
 * 
 * @see BaseCompilerTest for shared test utilities
 */
class CompilerTest extends BaseCompilerTest {
    @Test
    void emptyProgramCompiles() {
        assertValid("", "");
    }
}