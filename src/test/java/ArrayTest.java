import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for array handling in the Magma compiler.
 * Tests the compilation of Magma arrays to C arrays.
 * Includes tests for edge cases and boundary conditions to ensure robustness.
 */
public class ArrayTest {
	/**
	 * Test that the compiler can generate C code for a simple array declaration with the new syntax.
	 * Tests array declaration and initialization with the syntax: let myArray : [U8; 3] = [1, 2, 3];
	 */
	@Test
	public void testCompileArrayDeclaration() {
		// Arrange
		String javaCode = "let myArray : [U8; 3] = [1, 2, 3];";
		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint8_t myArray[3] = {1, 2, 3};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}

	/**
	 * Test that the compiler can generate C code for arrays of different types and sizes.
	 * Tests array declarations with the syntax: let arrayName : [Type; Size] = [elements];
	 */
	@Test
	public void testCompileArraysOfDifferentTypes() {
		// Arrange
		String javaCode = """
				let byteArray : [U8; 4] = [10, 20, 30, 40];
				let intArray : [I32; 2] = [100, 200];
				let boolArray : [Bool; 3] = [true, false, true];""";

		// Act
		String cCode = Main.compile(javaCode);

		// Assert
		String expectedCode = """
				#include <stdint.h>
				#include <stdbool.h>

				int main() {
				    uint8_t byteArray[4] = {10, 20, 30, 40};
				    int32_t intArray[2] = {100, 200};
				    bool boolArray[3] = {true, false, true};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can generate C code for a 2D array declaration.
	 * Tests array declaration and initialization with the syntax: let matrix : [I32; 2, 3] = [[1, 2, 3], [4, 5, 6]];
	 */
	@Test
	public void testCompile2DArrayDeclaration() {
		// Arrange
		String javaCode = "let matrix : [I32; 2, 3] = [[1, 2, 3], [4, 5, 6]];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Debug output
		System.out.println("Generated C code for 2D array:");
		System.out.println(cCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t matrix[2][3] = {{1, 2, 3}, {4, 5, 6}};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can generate C code for a 3D array declaration.
	 * Tests array declaration and initialization with the syntax: 
	 * let cube : [I32; 2, 2, 2] = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]];
	 */
	@Test
	public void testCompile3DArrayDeclaration() {
		// Arrange
		String javaCode = "let cube : [I32; 2, 2, 2] = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t cube[2][2][2] = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output");
	}
	
	/**
	 * Test that the compiler can handle empty array declarations.
	 * Tests array declaration and initialization with no elements.
	 */
	@Test
	public void testCompileEmptyArrayDeclaration() {
		// Arrange
		String javaCode = "let emptyArray : [I32; 0] = [];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t emptyArray[0] = {};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for empty array");
	}
	
	/**
	 * Test that the compiler can handle single element array declarations.
	 * Tests array declaration and initialization with just one element.
	 */
	@Test
	public void testCompileSingleElementArrayDeclaration() {
		// Arrange
		String javaCode = "let singleElementArray : [U64; 1] = [42];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    uint64_t singleElementArray[1] = {42};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for single element array");
	}
	
	/**
	 * Test that the compiler can handle large array declarations.
	 * Tests array declaration and initialization with many elements.
	 */
	@Test
	public void testCompileLargeArrayDeclaration() {
		// Arrange
		String javaCode = "let largeArray : [I16; 20] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20];";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int16_t largeArray[20] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for large array");
	}
	
	/**
	 * Test that the compiler can handle arrays with boundary values.
	 * Tests array declaration and initialization with min/max values for different types.
	 */
	@Test
	public void testCompileArrayWithBoundaryValues() {
		// Arrange
		String javaCode = """
				let i8Array : [I8; 2] = [-128, 127];
				let u8Array : [U8; 2] = [0, 255];
				let i16Array : [I16; 2] = [-32768, 32767];
				let u16Array : [U16; 2] = [0, 65535];""";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int8_t i8Array[2] = {-128, 127};
				    uint8_t u8Array[2] = {0, 255};
				    int16_t i16Array[2] = {-32768, 32767};
				    uint16_t u16Array[2] = {0, 65535};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for arrays with boundary values");
	}
	
	/**
	 * Test that the compiler can handle multiple array declarations of different dimensions.
	 * Tests a mix of 1D, 2D, and 3D arrays in the same program.
	 */
	@Test
	public void testCompileMixedDimensionArrays() {
		// Arrange
		String javaCode = """
				let array1D : [I32; 3] = [1, 2, 3];
				let array2D : [I32; 2, 2] = [[4, 5], [6, 7]];
				let array3D : [I32; 1, 2, 2] = [[[8, 9], [10, 11]]];""";
		
		// Act
		String cCode = Main.compile(javaCode);
		
		// Assert
		String expectedCode = """
				#include <stdint.h>

				int main() {
				    int32_t array1D[3] = {1, 2, 3};
				    int32_t array2D[2][2] = {{4, 5}, {6, 7}};
				    int32_t array3D[1][2][2] = {{{8, 9}, {10, 11}}};
				    return 0;
				}""";
		
		assertEquals(expectedCode, cCode, "C code should match expected output for mixed dimension arrays");
	}
}