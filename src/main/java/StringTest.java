/**
 * Simple test program for string support in the Magma compiler.
 */
import java.util.Arrays;
import java.util.Optional;
public class StringTest {
    public static void main(String[] args) {
        // Test simple string
        System.out.println("Test 1 (simple string):");
        System.out.println(Main.compile("let message : I8 = \"Hello\";"));
        System.out.println();
        
        // Test empty string
        System.out.println("Test 2 (empty string):");
        System.out.println(Main.compile("let empty : I8 = \"\";"));
        System.out.println();
        
        // Test string with spaces
        System.out.println("Test 3 (string with spaces):");
        System.out.println(Main.compile("let greeting : I8 = \"Hello, World!\";"));
        System.out.println();
        
        // Test string with escape sequences
        System.out.println("Test 4 (string with escape sequences):");
        System.out.println(Main.compile("let withNewline : I8 = \"Hello\\n\";"));
        System.out.println();
        
        // Test that non-string declarations still work
        System.out.println("Test 5 (non-string declaration):");
        System.out.println(Main.compile("let value : I32 = 42;"));
    }
}