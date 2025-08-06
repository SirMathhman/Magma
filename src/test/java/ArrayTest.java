import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for array handling in the Magma compiler.
 * Tests the compilation of Java arrays to C arrays.
 */
public class ArrayTest {
    
    /**
     * Test that the compiler can generate C code for a simple integer array.
     */
    @Test
    public void testCompileIntArray() {
        // Arrange
        String javaCode = "public class ArrayExample {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        int[] numbers = {1, 2, 3, 4, 5};\n" +
                          "        for (int i = 0; i < numbers.length; i++) {\n" +
                          "            System.out.println(numbers[i]);\n" +
                          "        }\n" +
                          "    }\n" +
                          "}";
        
        // Act
        String cCode = Main.compile(javaCode);
        
        // Assert
        assertNotNull(cCode, "Compiled C code should not be null");
        assertTrue(cCode.contains("#include <stdio.h>"), "C code should include stdio.h");
        assertTrue(cCode.contains("int main("), "C code should have a main function");
        assertTrue(cCode.contains("int numbers[5] = {1, 2, 3, 4, 5};"), "C code should declare and initialize the array");
        assertTrue(cCode.contains("for (int i = 0; i < 5; i++)"), "C code should have a loop to iterate over the array");
        assertTrue(cCode.contains("printf(\"%d\\n\", numbers[i]);"), "C code should print each array element");
        assertTrue(cCode.contains("return 0;"), "C code should return 0");
    }
    
    /**
     * Test that the compiler can generate C code for a simple string array.
     */
    @Test
    public void testCompileStringArray() {
        // Arrange
        String javaCode = "public class StringArrayExample {\n" +
                          "    public static void main(String[] args) {\n" +
                          "        String[] names = {\"Alice\", \"Bob\", \"Charlie\"};\n" +
                          "        for (int i = 0; i < names.length; i++) {\n" +
                          "            System.out.println(names[i]);\n" +
                          "        }\n" +
                          "    }\n" +
                          "}";
        
        // Act
        String cCode = Main.compile(javaCode);
        
        // Assert
        assertNotNull(cCode, "Compiled C code should not be null");
        assertTrue(cCode.contains("#include <stdio.h>"), "C code should include stdio.h");
        assertTrue(cCode.contains("#include <string.h>"), "C code should include string.h");
        assertTrue(cCode.contains("int main("), "C code should have a main function");
        assertTrue(cCode.contains("char* names[3] = {\"Alice\", \"Bob\", \"Charlie\"};"), 
                  "C code should declare and initialize the string array");
        assertTrue(cCode.contains("for (int i = 0; i < 3; i++)"), "C code should have a loop to iterate over the array");
        assertTrue(cCode.contains("printf(\"%s\\n\", names[i]);"), "C code should print each array element");
        assertTrue(cCode.contains("return 0;"), "C code should return 0");
    }
}