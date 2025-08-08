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
		Stream<Arguments> unsignedTypes = unsignedTypeProvider();
		Stream<Arguments> signedTypes = signedTypeProvider();
		return Stream.concat(unsignedTypes, signedTypes);
	}

	/**
	 * Provides test data for unsigned type transformations.
	 *
	 * @return A stream of arguments containing unsigned Magma types and corresponding C/C++ types
	 */
	private static Stream<Arguments> unsignedTypeProvider() {
		return Stream.of(Arguments.of("U8", "uint8_t"), Arguments.of("U16", "uint16_t"), Arguments.of("U32", "uint32_t"),
										 Arguments.of("U64", "uint64_t"));
	}

	/**
	 * Provides test data for signed type transformations.
	 *
	 * @return A stream of arguments containing signed Magma types and corresponding C/C++ types
	 */
	private static Stream<Arguments> signedTypeProvider() {
		return Stream.of(Arguments.of("I8", "int8_t"), Arguments.of("I16", "int16_t"), Arguments.of("I32", "int32_t"),
										 Arguments.of("I64", "int64_t"));
	}

	/**
	 * Test that the process method throws a CompileException.
	 */
	@Test
	@DisplayName("process() should throw CompileException")
	public void testProcessThrowsException() {
		Compiler processor = new Compiler();

		assertThrows(CompileException.class, () -> {
			processor.process("test input");
		});
	}

	/**
	 * Parameterized test to verify that the process method throws a
	 * CompileException for non-empty inputs.
	 *
	 * @param input The input string to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"hello", "123", "special!@#"})
	@DisplayName("process() should throw CompileException for non-empty inputs")
	public void testProcessThrowsExceptionForNonEmptyInputs(String input) {
		Compiler processor = new Compiler();

		assertThrows(CompileException.class, () -> {
			processor.process(input);
		});
	}

	/**
	 * Test that the process method returns an empty string when the input is empty.
	 */
	@Test
	@DisplayName("process() should return empty string for empty input")
	public void testProcessReturnsEmptyStringForEmptyInput() throws CompileException {
		Compiler processor = new Compiler();
		String result = processor.process("");
		assertEquals("", result, "Should return empty string for empty input");
	}

	/**
	 * Test that the process method transforms "let x = 100;" to "int32_t x = 100;".
	 */
	@Test
	@DisplayName("process() should transform 'let x = 100;' to 'int32_t x = 100;'")
	public void testProcessTransformsLetToInt32t() throws CompileException {
		Compiler processor = new Compiler();
		String input = "let x = 100;";
		String expected = "int32_t x = 100;";

		String result = processor.process(input);

		assertEquals(expected, result, "Should transform 'let x = 100;' to 'int32_t x = 100;'");
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
		Compiler processor = new Compiler();
		String input = "let x: " + magmaType + " = 100;";
		String expected = cType + " x = 100;";

		String result = processor.process(input);

		assertEquals(expected, result, "Should transform 'let x: " + magmaType + " = 100;' to '" + cType + " x = 100;'");
	}

	/**
	 * Test that the process method throws a CompileException for an unsupported type.
	 */
	@Test
	@DisplayName("process() should throw CompileException for unsupported type")
	public void testProcessThrowsExceptionForUnsupportedType() {
		Compiler processor = new Compiler();
		String input = "let x: Float = 100.0;";

		assertThrows(CompileException.class, () -> {
			processor.process(input);
		}, "Should throw CompileException for unsupported type");
	}
}