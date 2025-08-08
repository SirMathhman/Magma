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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	 * Helper method to assert compilation results.
	 * If expected is null, asserts that compilation fails with CompileException.
	 * Otherwise, asserts that compilation succeeds and produces the expected output.
	 *
	 * @param input    The input string to process
	 * @param expected The expected output after processing, or null if compilation should fail
	 */
	private void assertCompilation(String input, String expected) {
		Compiler processor = new Compiler();
		if (expected == null) {
			assertThrows(CompileException.class, () -> processor.process(input));
			return;
		}
		try { assertEquals(expected, processor.process(input)); } 
		catch (CompileException e) { throw new AssertionError("Expected success but failed: " + e.getMessage()); }
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
		assertCompilation(input, null);
	}

	/**
	 * Test that the process method returns an empty string when the input is empty.
	 */
	@Test
	@DisplayName("process() should return empty string for empty input")
	public void testProcessReturnsEmptyStringForEmptyInput() throws CompileException {
		assertCompilation("", "");
	}

	/**
	 * Test that the process method transforms "let x = 100;" to "int32_t x = 100;".
	 */
	@Test
	@DisplayName("process() should transform 'let x = 100;' to 'int32_t x = 100;'")
	public void testProcessTransformsLetToInt32t() throws CompileException {
		String input = "let x = 100;";
		String expected = "int32_t x = 100;";

		assertCompilation(input, expected);
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

		assertCompilation(input, expected);
	}

	/**
	 * Test that the process method throws a CompileException for an unsupported type.
	 */
	@Test
	@DisplayName("process() should throw CompileException for unsupported type")
	public void testProcessThrowsExceptionForUnsupportedType() {
		String input = "let x: Float = 100.0;";

		assertCompilation(input, null);
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
			assertCompilation(specificInput, specificExpected);
		}
		
		// Test the general case for all types
		String input = "let x = 100" + magmaType + ";";
		String expected = cType + " x = 100;";
		assertCompilation(input, expected);
	}
	
	/**
	 * Test that the process method gives precedence to type suffixes over type annotations.
	 */
	@Test
	@DisplayName("process() should give precedence to type suffixes over type annotations")
	public void testProcessGivesPrecedenceToTypeSuffixes() throws CompileException {
		String input = "let x: U32 = 100U64;";
		String expected = "uint64_t x = 100;";

		assertCompilation(input, expected);
	}
	
	/**
	 * Test that the process method correctly handles variable references.
	 */
	@Test
	@DisplayName("process() should handle variable references correctly")
	public void testProcessHandlesVariableReferences() throws CompileException {
		// Create a single compiler instance to maintain state between calls
		Compiler compiler = new Compiler();
		
		// Process both statements sequentially
		String result1 = compiler.process("let x = 100;");
		assertEquals("int32_t x = 100;", result1);
		String result2 = compiler.process("let y = x;");
		assertEquals("int32_t y = x;", result2);
	}
	
	/**
	 * Test that the process method correctly handles empty code blocks.
	 */
	@Test
	@DisplayName("process() should handle empty code blocks correctly")
	public void testProcessHandlesEmptyCodeBlocks() throws CompileException {
		Compiler compiler = new Compiler();
		String result = compiler.process("{}");
		assertEquals("{\n}", result);
	}
	
	/**
	 * Test that the process method correctly handles code blocks with a single statement.
	 */
	@Test
	@DisplayName("process() should handle code blocks with a single statement correctly")
	public void testProcessHandlesCodeBlocksWithSingleStatement() throws CompileException {
		Compiler compiler = new Compiler();
		String result = compiler.process("{let x = 100;}");
		assertEquals("{\n    int32_t x = 100;\n}", result);
	}
	
	/**
	 * Test that the process method correctly handles code blocks with multiple statements.
	 */
	@Test
	@DisplayName("process() should handle code blocks with multiple statements correctly")
	public void testProcessHandlesCodeBlocksWithMultipleStatements() throws CompileException {
		Compiler compiler = new Compiler();
		String result = compiler.process("{let x = 100; let y = 200;}");
		assertEquals("{\n    int32_t x = 100;\n    int32_t y = 200;\n}", result);
	}
	
	/**
	 * Test that variables declared inside a code block are not accessible outside the block.
	 */
	@Test
	@DisplayName("Variables declared inside a code block should not be accessible outside")
	public void testVariableScopingInCodeBlocks() throws CompileException {
		Compiler compiler = new Compiler();
		
		// Process a code block with a variable declaration
		compiler.process("{let x = 100;}");
		
		// Attempt to reference the variable outside the block should fail
		assertThrows(CompileException.class, () -> compiler.process("let y = x;"));
	}
	
	/**
	 * Test that variables declared outside a code block are accessible inside the block.
	 */
	@Test
	@DisplayName("Variables declared outside a code block should be accessible inside")
	public void testOuterVariablesAccessibleInCodeBlocks() throws CompileException {
		Compiler compiler = new Compiler();
		
		// Declare a variable outside the block
		compiler.process("let x = 100;");
		
		// Reference the variable inside a block
		String result = compiler.process("{let y = x;}");
		assertEquals("{\n    int32_t y = x;\n}", result);
	}
}