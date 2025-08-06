import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for array support in the Magma compiler.
 */
public class ArrayTest {
    /**
     * Test basic array declarations with different element types.
     */
    @Test
    public void testBasicArrayDeclarations() {
        // Test U64 array
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let myArray : [U64; 3] = [1, 2, 3];")
        );
        
        // Test I32 array
        assertEquals(
            "int32_t numbers[5] = {-1, 0, 1, 2, 3};",
            Main.compile("let numbers : [I32; 5] = [-1, 0, 1, 2, 3];")
        );
        
        // Test U8 array
        assertEquals(
            "uint8_t bytes[4] = {255, 0, 128, 64};",
            Main.compile("let bytes : [U8; 4] = [255, 0, 128, 64];")
        );
        
        // Test Bool array
        assertEquals(
            "bool flags[2] = {true, false};",
            Main.compile("let flags : [Bool; 2] = [true, false];")
        );
    }
    
    /**
     * Test array declarations with different whitespace patterns.
     */
    @Test
    public void testWhitespaceInArrayDeclarations() {
        // Test with extra spaces around variable name
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let  myArray  : [U64; 3] = [1, 2, 3];")
        );
        
        // Test with extra spaces around type declaration
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let myArray  :  [U64; 3]  = [1, 2, 3];")
        );
        
        // Test with extra spaces inside type declaration
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let myArray : [ U64 ; 3 ] = [1, 2, 3];")
        );
        
        // Test with extra spaces around assignment
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let myArray : [U64; 3]  =  [1, 2, 3];")
        );
        
        // Test with extra spaces inside initializer
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let myArray : [U64; 3] = [ 1 , 2 , 3 ];")
        );
        
        // Test with extra spaces everywhere
        assertEquals(
            "uint64_t myArray[3] = {1, 2, 3};",
            Main.compile("let  myArray  :  [ U64 ; 3 ]  =  [ 1 , 2 , 3 ]  ;")
        );
    }
    
    /**
     * Test edge cases for array declarations.
     */
    @Test
    public void testEdgeCases() {
        // Test single element array
        assertEquals(
            "uint64_t singleElement[1] = {42};",
            Main.compile("let singleElement : [U64; 1] = [42];")
        );
        
        // Test array with large number of elements
        assertEquals(
            "int32_t largeArray[10] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};",
            Main.compile("let largeArray : [I32; 10] = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];")
        );
        
        // Test array with large values
        assertEquals(
            "uint64_t largeValues[3] = {18446744073709551615, 0, 9223372036854775807};",
            Main.compile("let largeValues : [U64; 3] = [18446744073709551615, 0, 9223372036854775807];")
        );
    }
    
    /**
     * Test that non-array declarations still work.
     */
    @Test
    public void testNonArrayDeclarations() {
        // Test that primitive type declarations still work
        assertEquals(
            "int32_t value = 42;",
            Main.compile("let value : I32 = 42;")
        );
        
        assertEquals(
            "bool flag = true;",
            Main.compile("let flag : Bool = true;")
        );
        
        // Test that character literals still work
        assertEquals(
            "int8_t charA = 97;",
            Main.compile("let charA : I8 = 'a';")
        );
    }
}