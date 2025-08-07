package magma;

public class TestMain {
    public static void main(String[] args) {
        // Test case 1: U8 array with 3 elements
        testCompilation(
            "let x : [U8; 3] = [1, 2, 3];",
            "uint8_t x[3] = {1, 2, 3};"
        );
        
        // Test case 2: I32 array with 2 elements
        testCompilation(
            "let y : [I32; 2] = [42, 100];",
            "int32_t y[2] = {42, 100};"
        );
        
        // Test case 3: U64 array with 5 elements
        testCompilation(
            "let z : [U64; 5] = [1, 2, 3, 4, 5];",
            "uint64_t z[5] = {1, 2, 3, 4, 5};"
        );
        
        // Test case 4: Array with spaces in the initializer
        testCompilation(
            "let a : [I16; 4] = [10,  20,30,   40];",
            "int16_t a[4] = {10,  20,30,   40};"
        );
    }
    
    private static void testCompilation(String input, String expected) {
        try {
            String output = Compiler.compile(input);
            System.out.println("Input: " + input);
            System.out.println("Output: " + output);
            System.out.println("Expected: " + expected);
            System.out.println("Test " + (output.equals(expected) ? "PASSED" : "FAILED"));
            System.out.println();
        } catch (CompileException e) {
            System.out.println("Compilation failed for input: " + input);
            System.out.println("Error: " + e.getMessage());
            System.out.println();
        }
    }
}