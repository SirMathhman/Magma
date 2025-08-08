package magma;

import org.junit.jupiter.api.Test;

/**
 * Debug test for generic struct handling.
 */
class GenericStructDebugTest extends BaseCompilerTest {
    
    // Main method for direct testing
    public static void main(String[] args) {
        GenericStructDebugTest test = new GenericStructDebugTest();
        System.out.println("=== RUNNING FOCUSED DEBUG TEST ===");
        test.focusedDebugTest();
    }
    
    /**
     * A focused test that examines specifically the issue with generic struct instantiations
     */
    @Test
    void focusedDebugTest() {
        System.out.println("\n=======================================================");
        System.out.println("FOCUSED DEBUG TEST: Generic struct instantiation with concrete type");
        System.out.println("=======================================================");
        
        // Simple case: just a struct declaration and instantiation
        String input = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 };";
        String expectedOutput = "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100};";
        
        try {
            System.out.println("Compiling input: '" + input + "'");
            String actual = Compiler.compile(input);
            System.out.println("Expected: '" + expectedOutput + "'");
            System.out.println("Actual: '" + actual + "'");
            System.out.println("Result: " + (expectedOutput.equals(actual) ? "PASSED" : "FAILED"));
            
            // If the test failed, show a character-by-character comparison
            if (!expectedOutput.equals(actual)) {
                System.out.println("\nDetailed comparison:");
                compareStrings(expectedOutput, actual);
            }
        } catch (CompileException e) {
            System.out.println("CompileException: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
    
    private static void compareStrings(String expected, String actual) {
        int minLength = Math.min(expected.length(), actual.length());
        
        System.out.println("Position | Expected | Actual");
        System.out.println("---------|----------|-------");
        
        boolean difference = false;
        for (int i = 0; i < minLength; i++) {
            char e = expected.charAt(i);
            char a = actual.charAt(i);
            if (e != a) {
                System.out.printf("%8d | %8c | %6c%n", i, e, a);
                difference = true;
            }
        }
        
        if (!difference && expected.length() != actual.length()) {
            System.out.println("Strings have different lengths:");
            System.out.println("Expected length: " + expected.length());
            System.out.println("Actual length: " + actual.length());
            
            if (expected.length() > actual.length()) {
                System.out.println("Missing from actual: '" + expected.substring(actual.length()) + "'");
            } else {
                System.out.println("Extra in actual: '" + actual.substring(expected.length()) + "'");
            }
        }
        
        if (!difference) {
            System.out.println("No character differences found, but strings are not equal.");
            System.out.println("This could be due to invisible characters or encoding issues.");
        }
    }
    
    @Test
    void debugGenericStructDeclaration() {
        System.out.println("[DEBUG_LOG] =======================================================");
        System.out.println("[DEBUG_LOG] Testing standalone generic struct declaration");
        System.out.println("[DEBUG_LOG] =======================================================");
        
        String input = "struct MyWrapper<T> { value : T }";
        String expectedOutput = "";
        
        try {
            String actual = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Input: '" + input + "'");
            System.out.println("[DEBUG_LOG] Expected: '" + expectedOutput + "'");
            System.out.println("[DEBUG_LOG] Actual: '" + actual + "'");
        } catch (CompileException e) {
            System.out.println("[DEBUG_LOG] CompileException: " + e.getMessage());
        }
    }
    
    @Test
    void debugGenericStructInstantiation() {
        System.out.println("[DEBUG_LOG] =======================================================");
        System.out.println("[DEBUG_LOG] Testing generic struct instantiation with concrete type");
        System.out.println("[DEBUG_LOG] =======================================================");
        
        String input = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 };";
        String expectedOutput = "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100};";
        
        try {
            String actual = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Input: '" + input + "'");
            System.out.println("[DEBUG_LOG] Expected: '" + expectedOutput + "'");
            System.out.println("[DEBUG_LOG] Actual: '" + actual + "'");
        } catch (CompileException e) {
            System.out.println("[DEBUG_LOG] CompileException: " + e.getMessage());
        }
    }
}