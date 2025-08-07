import org.junit.jupiter.api.Test;

/**
 * Test class for error handling in the Magma compiler.
 * Tests how the compiler handles invalid inputs and edge cases.
 * These tests ensure the compiler is robust against malformed input.
 */
public class ErrorHandlingTest {

	/**
	 * Test that the compiler handles mismatched array sizes and initializers.
	 * This tests that the compiler correctly handles the case where the declared array size
	 * doesn't match the number of elements in the initializer.
	 */
	@Test
	public void testMismatchedArraySizeAndInitializer() {
		// Arrange
		String magmaCode = "let mismatchedArray : [I32; 5] = [1, 2, 3];"; // Declared size 5, but only 3 elements

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Array size mismatch"
		);
	}

	/**
	 * Test that the compiler handles invalid type declarations.
	 * This tests that the compiler correctly handles the case where an invalid type is specified.
	 */
	@Test
	public void testInvalidTypeDeclaration() {
		// Arrange
		String magmaCode = "let x : InvalidType = 42;"; // InvalidType is not a valid type

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Invalid type"
		);
	}

	/**
	 * Test that the compiler handles invalid escape sequences in string literals.
	 * This tests that the compiler correctly handles the case where an invalid escape sequence is used.
	 */
	@Test
	public void testInvalidEscapeSequence() {
		// Arrange
		String magmaCode = "let s : [U8; 2] = \"\\z\";"; // \z is not a valid escape sequence

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Invalid escape sequence"
		);
	}

	/**
	 * Test that the compiler handles out-of-range values for types.
	 * This tests that the compiler correctly handles the case where a value is outside the valid range for its type.
	 */
	@Test
	public void testOutOfRangeValues() {
		// Arrange
		String magmaCode = """
				let a : I8 = 128;     // 128 is out of range for I8 (max is 127)
				let b : U8 = -1;      // -1 is out of range for U8 (min is 0)
				let c : I16 = 32768;  // 32768 is out of range for I16 (max is 32767)""";

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Value out of range"
		);
	}

	/**
	 * Test that the compiler handles malformed array declarations.
	 * This tests that the compiler correctly handles the case where an array declaration is malformed.
	 */
	@Test
	public void testMalformedArrayDeclaration() {
		// Arrange
		String magmaCode = "let badArray : [I32; -1] = [1, 2, 3];"; // Negative array size

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Invalid array size"
		);
	}

	/**
	 * Test that the compiler handles malformed multi-dimensional array declarations.
	 * This tests that the compiler correctly handles the case where a multi-dimensional array declaration is malformed.
	 */
	@Test
	public void testMalformedMultiDimArrayDeclaration() {
		// Arrange
		String magmaCode = "let badMatrix : [I32; 2, 0] = [[1, 2], [3, 4]];"; // Second dimension has size 0

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Invalid array dimensions"
		);
	}

	/**
	 * Test that the compiler handles invalid type assignments.
	 * This tests that the compiler correctly handles the case where a value of one type
	 * is assigned to a variable of an incompatible type.
	 */
	@Test
	public void testInvalidTypeAssignment() {
		// Arrange
		String magmaCode = "let x : U64 = false;"; // Boolean value assigned to U64 variable

		// Act & Assert
		TestUtil.assertCompilationError(
			magmaCode, 
			IllegalArgumentException.class, 
			"Type mismatch"
		);
	}
}