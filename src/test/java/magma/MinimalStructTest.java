package magma;

import org.junit.jupiter.api.Test;

/**
 * Minimal test for debugging monomorphic struct issues.
 */
class MinimalStructTest extends BaseCompilerTest {
    
    private void debugCompile(String input, String expectedOutput) {
        System.out.println("\n[DEBUG_LOG] ======= Testing =======");
        System.out.println("[DEBUG_LOG] Input: " + input);
        System.out.println("[DEBUG_LOG] Expected output: " + expectedOutput);
        
        try {
            String actualOutput = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Actual output: " + actualOutput);
            System.out.println("[DEBUG_LOG] Equals? " + expectedOutput.equals(actualOutput));
            System.out.println("[DEBUG_LOG] Length: expected=" + expectedOutput.length() + ", actual=" + actualOutput.length());
            
            // Show character-by-character differences
            if (!expectedOutput.equals(actualOutput)) {
                for (int i = 0; i < Math.min(expectedOutput.length(), actualOutput.length()); i++) {
                    if (expectedOutput.charAt(i) != actualOutput.charAt(i)) {
                        System.out.println("[DEBUG_LOG] First difference at index " + i + 
                                          ": expected='" + expectedOutput.charAt(i) + 
                                          "', actual='" + actualOutput.charAt(i) + "'");
                        
                        // Show context around the difference
                        int start = Math.max(0, i - 10);
                        int end = Math.min(i + 10, Math.min(expectedOutput.length(), actualOutput.length()));
                        System.out.println("[DEBUG_LOG] Expected context: ..." + 
                                          expectedOutput.substring(start, end) + "...");
                        System.out.println("[DEBUG_LOG] Actual context: ..." + 
                                          actualOutput.substring(start, end) + "...");
                        break;
                    }
                }
                
                // Handle different lengths
                if (expectedOutput.length() != actualOutput.length()) {
                    System.out.println("[DEBUG_LOG] Strings have different lengths!");
                    if (expectedOutput.length() < actualOutput.length()) {
                        System.out.println("[DEBUG_LOG] Extra content in actual: " + 
                                          actualOutput.substring(expectedOutput.length()));
                    } else {
                        System.out.println("[DEBUG_LOG] Missing content in actual: " + 
                                          expectedOutput.substring(actualOutput.length()));
                    }
                }
            }
            
            assertValid(input, expectedOutput);
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Exception: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    void sequentialGenericStructTest() {
        // Test case 1: Primitive type
        String input1 = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 };";
        String expectedOutput1 = "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100};";
        debugCompile(input1, expectedOutput1);
        
        // Test case 2: Different primitive type
        String input2 = "struct MyWrapper<T> { value : T } let b : MyWrapper<Bool> = MyWrapper<Bool> { true };";
        String expectedOutput2 = "struct MyWrapper_Bool {bool value;}; MyWrapper_Bool b = {true};";
        debugCompile(input2, expectedOutput2);
        
        // Test case 3: Multiple type parameters
        String input3 = "struct Pair<K, V> { key : K, value : V } let pair : Pair<I32, Bool> = Pair<I32, Bool> { 42, true };";
        String expectedOutput3 = "struct Pair_I32_Bool {int32_t key; bool value;}; Pair_I32_Bool pair = {42, true};";
        debugCompile(input3, expectedOutput3);
        
        // Test case 4: Field access
        String input4 = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 }; let v = wrapper.value;";
        String expectedOutput4 = "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100}; int32_t v = wrapper.value;";
        debugCompile(input4, expectedOutput4);
        
        // Test case 5: Nested structs
        String input5 = "struct Inner<T> { value : T } struct Outer<U> { inner : Inner<U> } " +
                        "let inner : Inner<I32> = Inner<I32> { 42 }; " + 
                        "let outer : Outer<I32> = Outer<I32> { inner };";
        String expectedOutput5 = "struct Inner_I32 {int32_t value;}; struct Outer_I32 {Inner_I32 inner;}; " +
                                "Inner_I32 inner = {42}; " + 
                                "Outer_I32 outer = {inner};";
        debugCompile(input5, expectedOutput5);
    }
}