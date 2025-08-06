import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for string support in the Magma compiler.
 */
public class StringTest {
    /**
     * Test basic string declarations.
     */
    @Test
    public void testBasicStringDeclarations() {
        // Test simple string
        assertEquals(
            "int8_t message[5] = {72, 101, 108, 108, 111};",
            Main.compile("let message : I8 = \"Hello\";")
        );
        
        // Test empty string
        assertEquals(
            "int8_t empty[0] = {};",
            Main.compile("let empty : I8 = \"\";")
        );
        
        // Test string with spaces
        assertEquals(
            "int8_t greeting[13] = {72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33};",
            Main.compile("let greeting : I8 = \"Hello, World!\";")
        );
    }
    
    /**
     * Test strings with escape sequences.
     */
    @Test
    public void testEscapeSequences() {
        // Test string with newline
        assertEquals(
            "int8_t withNewline[6] = {72, 101, 108, 108, 111, 10};",
            Main.compile("let withNewline : I8 = \"Hello\\n\";")
        );
        
        // Test string with tab
        assertEquals(
            "int8_t withTab[7] = {72, 101, 108, 108, 111, 9, 33};",
            Main.compile("let withTab : I8 = \"Hello\\t!\";")
        );
        
        // Test string with multiple escape sequences
        assertEquals(
            "int8_t multiEscape[10] = {72, 101, 108, 108, 111, 10, 87, 111, 114, 108};",
            Main.compile("let multiEscape : I8 = \"Hello\\nWorl\";")
        );
    }
    
    /**
     * Test strings with special characters.
     */
    @Test
    public void testSpecialCharacters() {
        // Test string with special characters
        assertEquals(
            "int8_t special[5] = {33, 64, 35, 36, 37};",
            Main.compile("let special : I8 = \"!@#$%\";")
        );
        
        // Test string with numbers
        assertEquals(
            "int8_t numbers[10] = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57};",
            Main.compile("let numbers : I8 = \"0123456789\";")
        );
        
        // Test string with mixed characters
        assertEquals(
            "int8_t mixed[10] = {97, 49, 98, 50, 99, 51, 100, 52, 101, 53};",
            Main.compile("let mixed : I8 = \"a1b2c3d4e5\";")
        );
    }
    
    /**
     * Test whitespace variations in string declarations.
     */
    @Test
    public void testWhitespaceInStringDeclarations() {
        // Test with extra spaces around variable name
        assertEquals(
            "int8_t message[5] = {72, 101, 108, 108, 111};",
            Main.compile("let  message  : I8 = \"Hello\";")
        );
        
        // Test with extra spaces around type declaration
        assertEquals(
            "int8_t message[5] = {72, 101, 108, 108, 111};",
            Main.compile("let message  :  I8  = \"Hello\";")
        );
        
        // Test with extra spaces around assignment
        assertEquals(
            "int8_t message[5] = {72, 101, 108, 108, 111};",
            Main.compile("let message : I8  =  \"Hello\";")
        );
        
        // Test with extra spaces everywhere
        assertEquals(
            "int8_t message[5] = {72, 101, 108, 108, 111};",
            Main.compile("let  message  :  I8  =  \"Hello\"  ;")
        );
    }
    
    /**
     * Test that non-string declarations still work.
     */
    @Test
    public void testNonStringDeclarations() {
        // Test that primitive type declarations still work
        assertEquals(
            "int32_t value = 42;",
            Main.compile("let value : I32 = 42;")
        );
        
        // Test that character literals still work
        assertEquals(
            "int8_t charA = 97;",
            Main.compile("let charA : I8 = 'a';")
        );
        
        // Test that array declarations still work
        assertEquals(
            "int32_t numbers[5] = {1, 2, 3, 4, 5};",
            Main.compile("let numbers : [I32; 5] = [1, 2, 3, 4, 5];")
        );
    }
}