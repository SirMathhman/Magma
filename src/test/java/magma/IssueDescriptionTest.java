package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class specifically for the issue description: "Fail: `let x = 200; x = 400;`"
 */
public class IssueDescriptionTest {

    /**
     * Test that the exact code from the issue description fails as expected.
     * The code `let x = 200; x = 400;` should fail because x is immutable.
     */
    @Test
    @DisplayName("Issue description: 'let x = 200; x = 400;' should fail")
    public void testIssueDescription() {
        Compiler compiler = new Compiler();
        
        // First, process the variable declaration
        try {
            compiler.process("let x = 200;");
        } catch (CompileException e) {
            throw new AssertionError("Variable declaration should succeed: " + e.getMessage());
        }
        
        // Then, attempt to reassign the variable - this should fail
        assertThrows(CompileException.class, () -> compiler.process("x = 400;"),
                "Reassignment to immutable variable should fail");
    }
    
    /**
     * Test that the code from the issue description works when using a mutable variable.
     * The code `let mut x = 200; x = 400;` should succeed because x is mutable.
     */
    @Test
    @DisplayName("For comparison: 'let mut x = 200; x = 400;' should succeed")
    public void testMutableVariableReassignment() {
        Compiler compiler = new Compiler();
        
        try {
            // Process the mutable variable declaration
            compiler.process("let mut x = 200;");
            
            // Process the variable reassignment - this should succeed
            compiler.process("x = 400;");
        } catch (CompileException e) {
            throw new AssertionError("Expected success but failed: " + e.getMessage());
        }
    }
    
    /**
     * Test that the code from the issue description fails when in a code block.
     * The code `{ let x = 200; x = 400; }` should fail because x is immutable.
     */
    @Test
    @DisplayName("Issue description in code block: '{ let x = 200; x = 400; }' should fail")
    public void testIssueDescriptionInCodeBlock() {
        Compiler compiler = new Compiler();
        
        // Process the code block - this should fail
        assertThrows(CompileException.class, () -> compiler.process("{ let x = 200; x = 400; }"),
                "Reassignment to immutable variable in code block should fail");
    }
}