package magma;

import org.junit.jupiter.api.Test;

/**
 * Debug test for generic struct implementation.
 */
class GenericStructDebugTest extends BaseCompilerTest {
    @Test
    void debugSimpleGenericStruct() {
        String input = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 };";
        String expectedOutput = "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100};";
        
        try {
            System.out.println("[DEBUG_LOG] Input: " + input);
            System.out.println("[DEBUG_LOG] Expected output: " + expectedOutput);
            
            String actualOutput = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Actual output: " + actualOutput);
            
            // Print detailed debug info
            System.out.println("[DEBUG_LOG] Actual and expected are equal: " + actualOutput.equals(expectedOutput));
            
            if (!actualOutput.equals(expectedOutput)) {
                System.out.println("[DEBUG_LOG] Character-by-character comparison:");
                int minLength = Math.min(actualOutput.length(), expectedOutput.length());
                for (int i = 0; i < minLength; i++) {
                    if (actualOutput.charAt(i) != expectedOutput.charAt(i)) {
                        System.out.println("[DEBUG_LOG] Difference at position " + i + 
                            ": expected '" + expectedOutput.charAt(i) + 
                            "', got '" + actualOutput.charAt(i) + "'");
                        
                        // Print context around the difference
                        int start = Math.max(0, i - 10);
                        int end = Math.min(i + 10, minLength);
                        System.out.println("[DEBUG_LOG] Expected context: ..." + 
                            expectedOutput.substring(start, end) + "...");
                        System.out.println("[DEBUG_LOG] Actual context: ..." + 
                            actualOutput.substring(start, end) + "...");
                        break;
                    }
                }
                
                if (actualOutput.length() != expectedOutput.length()) {
                    System.out.println("[DEBUG_LOG] Length difference: expected " + 
                        expectedOutput.length() + ", got " + actualOutput.length());
                    
                    if (actualOutput.length() > expectedOutput.length()) {
                        System.out.println("[DEBUG_LOG] Extra characters in actual: " + 
                            actualOutput.substring(expectedOutput.length()));
                    } else {
                        System.out.println("[DEBUG_LOG] Missing characters in actual: " + 
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
    void debugMultipleTypeParams() {
        String input = "struct Pair<K, V> { key : K, value : V } let pair : Pair<I32, Bool> = Pair<I32, Bool> { 42, true };";
        String expectedOutput = "struct Pair_I32_Bool {int32_t key; bool value;}; Pair_I32_Bool pair = {42, true};";
        
        try {
            System.out.println("[DEBUG_LOG] Input: " + input);
            System.out.println("[DEBUG_LOG] Expected output: " + expectedOutput);
            
            String actualOutput = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Actual output: " + actualOutput);
            
            // Print detailed debug info
            System.out.println("[DEBUG_LOG] Actual and expected are equal: " + actualOutput.equals(expectedOutput));
            
            if (!actualOutput.equals(expectedOutput)) {
                System.out.println("[DEBUG_LOG] Character-by-character comparison:");
                int minLength = Math.min(actualOutput.length(), expectedOutput.length());
                for (int i = 0; i < minLength; i++) {
                    if (actualOutput.charAt(i) != expectedOutput.charAt(i)) {
                        System.out.println("[DEBUG_LOG] Difference at position " + i + 
                            ": expected '" + expectedOutput.charAt(i) + 
                            "', got '" + actualOutput.charAt(i) + "'");
                        
                        // Print context around the difference
                        int start = Math.max(0, i - 10);
                        int end = Math.min(i + 10, minLength);
                        System.out.println("[DEBUG_LOG] Expected context: ..." + 
                            expectedOutput.substring(start, end) + "...");
                        System.out.println("[DEBUG_LOG] Actual context: ..." + 
                            actualOutput.substring(start, end) + "...");
                        break;
                    }
                }
                
                if (actualOutput.length() != expectedOutput.length()) {
                    System.out.println("[DEBUG_LOG] Length difference: expected " + 
                        expectedOutput.length() + ", got " + actualOutput.length());
                    
                    if (actualOutput.length() > expectedOutput.length()) {
                        System.out.println("[DEBUG_LOG] Extra characters in actual: " + 
                            actualOutput.substring(expectedOutput.length()));
                    } else {
                        System.out.println("[DEBUG_LOG] Missing characters in actual: " + 
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
    void debugGenericStructFieldAccess() {
        String input = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { 100 }; let v = wrapper.value;";
        String expectedOutput = "struct MyWrapper_I32 {int32_t value;}; MyWrapper_I32 wrapper = {100}; int32_t v = wrapper.value;";
        
        try {
            System.out.println("[DEBUG_LOG] Input: " + input);
            System.out.println("[DEBUG_LOG] Expected output: " + expectedOutput);
            
            String actualOutput = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Actual output: " + actualOutput);
            
            // Print detailed debug info
            System.out.println("[DEBUG_LOG] Actual and expected are equal: " + actualOutput.equals(expectedOutput));
            
            if (!actualOutput.equals(expectedOutput)) {
                System.out.println("[DEBUG_LOG] Character-by-character comparison:");
                int minLength = Math.min(actualOutput.length(), expectedOutput.length());
                for (int i = 0; i < minLength; i++) {
                    if (actualOutput.charAt(i) != expectedOutput.charAt(i)) {
                        System.out.println("[DEBUG_LOG] Difference at position " + i + 
                            ": expected '" + expectedOutput.charAt(i) + 
                            "', got '" + actualOutput.charAt(i) + "'");
                        
                        // Print context around the difference
                        int start = Math.max(0, i - 10);
                        int end = Math.min(i + 10, minLength);
                        System.out.println("[DEBUG_LOG] Expected context: ..." + 
                            expectedOutput.substring(start, end) + "...");
                        System.out.println("[DEBUG_LOG] Actual context: ..." + 
                            actualOutput.substring(start, end) + "...");
                        break;
                    }
                }
                
                if (actualOutput.length() != expectedOutput.length()) {
                    System.out.println("[DEBUG_LOG] Length difference: expected " + 
                        expectedOutput.length() + ", got " + actualOutput.length());
                    
                    if (actualOutput.length() > expectedOutput.length()) {
                        System.out.println("[DEBUG_LOG] Extra characters in actual: " + 
                            actualOutput.substring(expectedOutput.length()));
                    } else {
                        System.out.println("[DEBUG_LOG] Missing characters in actual: " + 
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
    void debugNestedGenericStructs() {
        String input = "struct Inner<T> { value : T } struct Outer<U> { inner : Inner<U> } " +
                       "let inner : Inner<I32> = Inner<I32> { 42 }; " + 
                       "let outer : Outer<I32> = Outer<I32> { inner };";
        String expectedOutput = "struct Inner_I32 {int32_t value;}; struct Outer_I32 {Inner_I32 inner;}; " +
                               "Inner_I32 inner = {42}; " + 
                               "Outer_I32 outer = {inner};";
        
        try {
            System.out.println("[DEBUG_LOG] Input: " + input);
            System.out.println("[DEBUG_LOG] Expected output: " + expectedOutput);
            
            String actualOutput = Compiler.compile(input);
            System.out.println("[DEBUG_LOG] Actual output: " + actualOutput);
            
            // Print detailed debug info
            System.out.println("[DEBUG_LOG] Actual and expected are equal: " + actualOutput.equals(expectedOutput));
            
            if (!actualOutput.equals(expectedOutput)) {
                System.out.println("[DEBUG_LOG] Character-by-character comparison:");
                int minLength = Math.min(actualOutput.length(), expectedOutput.length());
                for (int i = 0; i < minLength; i++) {
                    if (actualOutput.charAt(i) != expectedOutput.charAt(i)) {
                        System.out.println("[DEBUG_LOG] Difference at position " + i + 
                            ": expected '" + expectedOutput.charAt(i) + 
                            "', got '" + actualOutput.charAt(i) + "'");
                        
                        // Print context around the difference
                        int start = Math.max(0, i - 10);
                        int end = Math.min(i + 10, minLength);
                        System.out.println("[DEBUG_LOG] Expected context: ..." + 
                            expectedOutput.substring(start, end) + "...");
                        System.out.println("[DEBUG_LOG] Actual context: ..." + 
                            actualOutput.substring(start, end) + "...");
                        break;
                    }
                }
                
                if (actualOutput.length() != expectedOutput.length()) {
                    System.out.println("[DEBUG_LOG] Length difference: expected " + 
                        expectedOutput.length() + ", got " + actualOutput.length());
                    
                    if (actualOutput.length() > expectedOutput.length()) {
                        System.out.println("[DEBUG_LOG] Extra characters in actual: " + 
                            actualOutput.substring(expectedOutput.length()));
                    } else {
                        System.out.println("[DEBUG_LOG] Missing characters in actual: " + 
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
    void debugInvalidGenericStructUsage() {
        try {
            // Missing type parameter in declaration
            String input1 = "struct MyWrapper { value : T }";
            System.out.println("[DEBUG_LOG] Testing: " + input1);
            try {
                Compiler.compile(input1);
                System.out.println("[DEBUG_LOG] ERROR: Should have thrown exception for missing type parameter");
            } catch (CompileException e) {
                System.out.println("[DEBUG_LOG] Correct exception thrown: " + e.getMessage());
            }

            // Using undeclared type parameter
            String input2 = "struct MyWrapper<T> { value : U }";
            System.out.println("[DEBUG_LOG] Testing: " + input2);
            try {
                Compiler.compile(input2);
                System.out.println("[DEBUG_LOG] ERROR: Should have thrown exception for undeclared type parameter");
            } catch (CompileException e) {
                System.out.println("[DEBUG_LOG] Correct exception thrown: " + e.getMessage());
            }

            // Missing type argument in usage
            String input3 = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper = MyWrapper { 100 };";
            System.out.println("[DEBUG_LOG] Testing: " + input3);
            try {
                Compiler.compile(input3);
                System.out.println("[DEBUG_LOG] ERROR: Should have thrown exception for missing type argument");
            } catch (CompileException e) {
                System.out.println("[DEBUG_LOG] Correct exception thrown: " + e.getMessage());
            }

            // Wrong number of type arguments
            String input4 = "struct Pair<K, V> { key : K, value : V } let pair : Pair<I32> = Pair<I32> { 42 };";
            System.out.println("[DEBUG_LOG] Testing: " + input4);
            try {
                Compiler.compile(input4);
                System.out.println("[DEBUG_LOG] ERROR: Should have thrown exception for wrong number of type arguments");
            } catch (CompileException e) {
                System.out.println("[DEBUG_LOG] Correct exception thrown: " + e.getMessage());
            }

            // Type mismatch in initialization
            String input5 = "struct MyWrapper<T> { value : T } let wrapper : MyWrapper<I32> = MyWrapper<I32> { true };";
            System.out.println("[DEBUG_LOG] Testing: " + input5);
            try {
                Compiler.compile(input5);
                System.out.println("[DEBUG_LOG] ERROR: Should have thrown exception for type mismatch");
            } catch (CompileException e) {
                System.out.println("[DEBUG_LOG] Correct exception thrown: " + e.getMessage());
            }

            // Type parameters with invalid names
            String input6 = "struct MyWrapper<1T> { value : 1T }";
            System.out.println("[DEBUG_LOG] Testing: " + input6);
            try {
                Compiler.compile(input6);
                System.out.println("[DEBUG_LOG] ERROR: Should have thrown exception for invalid type parameter name");
            } catch (CompileException e) {
                System.out.println("[DEBUG_LOG] Correct exception thrown: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}