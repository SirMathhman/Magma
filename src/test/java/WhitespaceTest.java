import org.junit.jupiter.api.Test;

/**
 * Test class for whitespace handling in variable declarations in the Magma compiler.
 * Tests that the compiler correctly handles various whitespace patterns in different types
 * of variable declarations, including explicit type declarations, typeless declarations,
 * array declarations, and string declarations.
 */
public class WhitespaceTest {

    /**
     * Test that the compiler correctly handles various whitespace patterns in explicit type declarations.
     * This includes different spacing around colons, equals signs, and variable names.
     */
    @Test
    public void testWhitespaceInExplicitTypeDeclarations() {
        // Arrange
        String magmaCode = """
                let a : I32 = 10;
                let b:I32=20;
                let c : I32=30;
                let d:I32 = 40;
                let    e    :    I32    =    50   ;
                let f: I32 = 60;
                let g :I32 = 70;""";

        // Act & Assert
        String expectedCode = """
                #include <stdint.h>

                int main() {
                    int32_t a = 10;
                    int32_t b = 20;
                    int32_t c = 30;
                    int32_t d = 40;
                    int32_t e = 50;
                    int32_t f = 60;
                    int32_t g = 70;
                    return 0;
                }""";
        
        TestUtil.assertCompiles(magmaCode, expectedCode, "C code should match expected output for explicit type declarations with various whitespace patterns");
    }
    
    /**
     * Test that the compiler correctly handles various whitespace patterns in typeless declarations.
     * This includes different spacing around equals signs and variable names.
     */
    @Test
    public void testWhitespaceInTypelessDeclarations() {
        // Arrange
        String magmaCode = """
                let a = 10;
                let b=20;
                let c= 30;
                let d =40;
                let    e    =    50   ;
                let f = 60I8;
                let g =70U16;
                let h= true;
                let i =false;
                let j = 'a';
                let k='b';""";

        // Act & Assert
        String expectedCode = """
                #include <stdint.h>
                #include <stdbool.h>

                int main() {
                    int32_t a = 10;
                    int32_t b = 20;
                    int32_t c = 30;
                    int32_t d = 40;
                    int32_t e = 50;
                    int8_t f = 60;
                    uint16_t g = 70;
                    bool h = true;
                    bool i = false;
                    uint8_t j = 'a';
                    uint8_t k = 'b';
                    return 0;
                }""";
        
        TestUtil.assertCompiles(magmaCode, expectedCode, "C code should match expected output for typeless declarations with various whitespace patterns");
    }
    
    /**
     * Test that the compiler correctly handles various whitespace patterns in array declarations.
     * This includes different spacing around brackets, colons, equals signs, and variable names.
     */
    @Test
    public void testWhitespaceInArrayDeclarations() {
        // Arrange
        String magmaCode = """
                let arr1 : [I32; 3] = [1, 2, 3];
                let arr2:[I32;3]=[4,5,6];
                let arr3 : [I32;3]= [7, 8, 9];
                let arr4:[I32; 3] = [10,11,12];
                let    arr5    :    [I32;    3]    =    [13,    14,    15]   ;
                let arr6 : [ I32 ; 3 ] = [ 16, 17, 18 ];""";

        // Act & Assert
        String expectedCode = """
                #include <stdint.h>

                int main() {
                    int32_t arr1[3] = {1, 2, 3};
                    int32_t arr2[3] = {4, 5, 6};
                    int32_t arr3[3] = {7, 8, 9};
                    int32_t arr4[3] = {10, 11, 12};
                    int32_t arr5[3] = {13, 14, 15};
                    int32_t arr6[3] = {16, 17, 18};
                    return 0;
                }""";
        
        TestUtil.assertCompiles(magmaCode, expectedCode, "C code should match expected output for array declarations with various whitespace patterns");
    }
    
    /**
     * Test that the compiler correctly handles various whitespace patterns in string declarations.
     * This includes different spacing around brackets, colons, equals signs, and variable names.
     */
    @Test
    public void testWhitespaceInStringDeclarations() {
        // Arrange
        String magmaCode = """
                let str1 : [U8; 5] = "hello";
                let str2:[U8;5]="world";
                let str3 : [U8;5]= "test1";
                let str4:[U8; 5] = "test2";
                let    str5    :    [U8;    5]    =    "test3"   ;
                let str6 : [ U8 ; 5 ] = "test4";""";

        // Act & Assert
        String expectedCode = """
                #include <stdint.h>

                int main() {
                    uint8_t str1[5] = "hello";
                    uint8_t str2[5] = "world";
                    uint8_t str3[5] = "test1";
                    uint8_t str4[5] = "test2";
                    uint8_t str5[5] = "test3";
                    uint8_t str6[5] = "test4";
                    return 0;
                }""";
        
        TestUtil.assertCompiles(magmaCode, expectedCode, "C code should match expected output for string declarations with various whitespace patterns");
    }
    
    /**
     * Test that the compiler correctly handles mixed whitespace patterns in a single program.
     * This includes different types of declarations with various whitespace patterns.
     */
    @Test
    public void testMixedWhitespacePatterns() {
        // Arrange
        String magmaCode = """
                let a : I32 = 10;
                let b=20;
                let arr:[I32;2]=[1,2];
                let str : [U8; 5] = "hello";
                let    c    :    Bool    =    true   ;
                let d='x';""";

        // Act & Assert
        String expectedCode = """
                #include <stdint.h>
                #include <stdbool.h>

                int main() {
                    int32_t a = 10;
                    int32_t b = 20;
                    int32_t arr[2] = {1, 2};
                    uint8_t str[5] = "hello";
                    bool c = true;
                    uint8_t d = 'x';
                    return 0;
                }""";
        
        TestUtil.assertCompiles(magmaCode, expectedCode, "C code should match expected output for mixed declarations with various whitespace patterns");
    }
    
    /**
     * Test that the compiler correctly handles extreme whitespace patterns in variable declarations.
     * This includes excessive whitespace and unusual spacing patterns.
     */
    @Test
    public void testExtremeWhitespacePatterns() {
        // Arrange
        String magmaCode = """
                let                a                 :                 I32                 =                 10                 ;
                let b:I32=20;
                let c : I32 = 30;
                let\td\t:\tI32\t=\t40\t;""";  // Using tabs

        // Act & Assert
        String expectedCode = """
                #include <stdint.h>

                int main() {
                    int32_t a = 10;
                    int32_t b = 20;
                    int32_t c = 30;
                    int32_t d = 40;
                    return 0;
                }""";
        
        TestUtil.assertCompiles(magmaCode, expectedCode, "C code should match expected output for extreme whitespace patterns");
    }
}