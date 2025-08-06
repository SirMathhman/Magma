import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Manual test for string support in the Magma compiler.
 */
public class StringTestManual {
    @Test
    public void testStringSupport() {
        // Test simple string
        String result1 = Main.compile("let message : I8 = \"Hello\";");
        assertEquals("int8_t message[5] = {72, 101, 108, 108, 111};", result1);
        
        // Test empty string
        String result2 = Main.compile("let empty : I8 = \"\";");
        assertEquals("int8_t empty[0] = {};", result2);
        
        // Test string with spaces
        String result3 = Main.compile("let greeting : I8 = \"Hello, World!\";");
        assertEquals("int8_t greeting[13] = {72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33};", result3);
        
        // Test string with escape sequences
        String result4 = Main.compile("let withNewline : I8 = \"Hello\\n\";");
        assertEquals("int8_t withNewline[6] = {72, 101, 108, 108, 111, 10};", result4);
        
        // Test that non-string declarations still work
        String result5 = Main.compile("let value : I32 = 42;");
        assertEquals("int32_t value = 42;", result5);
    }
}