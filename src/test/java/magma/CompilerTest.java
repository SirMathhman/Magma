package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for StringProcessor.
 */
public class CompilerTest {

	/**
	 * Provides test data for type transformation tests.
	 *
	 * @return A stream of arguments containing Magma type and corresponding C/C++ type
	 */
	private static Stream<Arguments> typeTransformationProvider() {
		return Stream.of(
			Arguments.of("U8", "uint8_t"), Arguments.of("U16", "uint16_t"), 
			Arguments.of("U32", "uint32_t"), Arguments.of("U64", "uint64_t"),
			Arguments.of("I8", "int8_t"), Arguments.of("I16", "int16_t"), 
			Arguments.of("I32", "int32_t"), Arguments.of("I64", "int64_t")
		);
	}

	/**
	 * Helper method to assert that the given input compiles successfully and produces the expected output.
	 *
	 * @param input    The input string to process
	 * @param expected The expected output after processing
	 * @throws CompileException If compilation fails
	 */
	private void assertValid(String input, String expected) throws CompileException {
		Compiler processor = new Compiler();
		String result = processor.process(input);
		assertEquals(expected, result);
	}

	/**
	 * Helper method to assert that the given input fails to compile and throws a CompileException.
	 *
	 * @param input The input string that should fail to process
	 */
	private void assertInvalid(String input) {
		Compiler processor = new Compiler();
		assertThrows(CompileException.class, () -> processor.process(input));
	}

	/**
	 * Parameterized test to verify that the process method throws a
	 * CompileException for non-empty inputs.
	 *
	 * @param input The input string to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"test input", "hello", "123", "special!@#"})
	@DisplayName("process() should throw CompileException for non-empty inputs")
	public void testProcessThrowsExceptionForNonEmptyInputs(String input) {
		assertInvalid(input);
	}

	/**
	 * Test that the process method returns an empty string when the input is empty.
	 */
	@Test
	@DisplayName("process() should return empty string for empty input")
	public void testProcessReturnsEmptyStringForEmptyInput() throws CompileException {
		assertValid("", "");
	}

	/**
	 * Test that the process method transforms "let x = 100;" to "int32_t x = 100;".
	 */
	@Test
	@DisplayName("process() should transform 'let x = 100;' to 'int32_t x = 100;'")
	public void testProcessTransformsLetToInt32t() throws CompileException {
		String input = "let x = 100;";
		String expected = "int32_t x = 100;";

		assertValid(input, expected);
	}

	/**
	 * Test that the process method correctly transforms variable declarations with different types.
	 *
	 * @param magmaType The Magma type to test
	 * @param cType     The expected C/C++ type
	 */
	@ParameterizedTest
	@MethodSource("typeTransformationProvider")
	@DisplayName("process() should transform type declarations correctly")
	public void testProcessTransformsTypes(String magmaType, String cType) throws CompileException {
		String input = "let x: " + magmaType + " = 100;";
		String expected = cType + " x = 100;";

		assertValid(input, expected);
	}

	/**
	 * Test that the process method throws a CompileException for an unsupported type.
	 */
	@Test
	@DisplayName("process() should throw CompileException for unsupported type")
	public void testProcessThrowsExceptionForUnsupportedType() {
		String input = "let x: Float = 100.0;";

		assertInvalid(input);
	}
	
	/**
	 * Test that the process method correctly transforms variable declarations with type suffixes.
	 * Includes both a specific test case and parameterized tests for all type suffixes.
	 *
	 * @param magmaType The Magma type to test (null for the specific test case)
	 * @param cType     The expected C/C++ type (null for the specific test case)
	 */
	@ParameterizedTest
	@MethodSource("typeTransformationProvider")
	@DisplayName("process() should transform literals with type suffixes correctly")
	public void testProcessTransformsLiteralsWithTypeSuffixes(String magmaType, String cType) throws CompileException {
		// Test the specific U64 case first
		if ("U64".equals(magmaType)) {
			String specificInput = "let x = 100U64;";
			String specificExpected = "uint64_t x = 100;";
			assertValid(specificInput, specificExpected);
		}
		
		// Test the general case for all types
		String input = "let x = 100" + magmaType + ";";
		String expected = cType + " x = 100;";
		assertValid(input, expected);
	}
	
	/**
	 * Test that the process method gives precedence to type suffixes over type annotations.
	 */
	@Test
	@DisplayName("process() should give precedence to type suffixes over type annotations")
	public void testProcessGivesPrecedenceToTypeSuffixes() throws CompileException {
		String input = "let x: U32 = 100U64;";
		String expected = "uint64_t x = 100;";

		assertValid(input, expected);
	}
}