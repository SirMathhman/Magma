package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static magma.CompileAssert.assertInvalid;
import static magma.CompileAssert.assertValid;

/**
 * Tests for the Compiler class.
 * Verifies the conversion of JavaScript and TypeScript syntax to C syntax.
 */
public class CompilerTest {
	@Test
	public void shouldReturnSameString() {
		// Arrange
		String input = "hello";
		assertValid(input, input);
	}

	/**
	 * Tests for basic JavaScript and TypeScript variable declarations.
	 * Tests both JavaScript let declarations and I32 type handling.
	 */
	@ParameterizedTest(name = "should convert {0} to {1}")
	@CsvSource({"'let x = 0;', 'int32_t x = 0;'", "'let test = 0;', 'int32_t test = 0;'",
			"'let x : I32 = 0;', 'int32_t x = 0;'", "'let x = 0I32;', 'int32_t x = 0;'"})
	public void shouldConvertBasicJavaScriptAndI32TypeScript(String input, String expected) {
		assertValid(input, expected);
	}

	/**
	 * Tests type annotations for all integer types.
	 *
	 * @param typeAnnotation the TypeScript type annotation (I8, I16, U8, U16, etc.)
	 * @param cType          the expected C type (int8_t, int16_t, uint8_t, uint16_t, etc.)
	 */
	@ParameterizedTest(name = "should convert {0} type annotation to {1}")
	@CsvSource({"I8, int8_t", "I16, int16_t", "I64, int64_t", "U8, uint8_t", "U16, uint16_t", "U32, uint32_t",
			"U64, uint64_t"})
	public void shouldConvertTypeAnnotations(String typeAnnotation, String cType) {
		assertValid("let x : " + typeAnnotation + " = 0;", cType + " x = 0;");
	}

	/**
	 * Tests type suffixes for all integer types (signed and unsigned).
	 *
	 * @param typeSuffix the TypeScript type suffix (I8, I16, U8, U16, etc.)
	 * @param cType      the expected C type (int8_t, int16_t, uint8_t, uint16_t, etc.)
	 */
	@ParameterizedTest(name = "should convert {0} suffix to {1}")
	@CsvSource({"I8, int8_t", "I16, int16_t", "I64, int64_t", "U8, uint8_t", "U16, uint16_t", "U32, uint32_t",
			"U64, uint64_t"})
	public void shouldConvertTypeSuffixes(String typeSuffix, String cType) {
		assertValid("let x = 0" + typeSuffix + ";", cType + " x = 0;");
	}

	/**
	 * Tests that a CompileException is thrown when incompatible types are used.
	 * Specifically, when a U64 value is assigned to an I32 variable.
	 */
	@Test
	public void shouldThrowCompileExceptionForIncompatibleTypes() {
		assertInvalid("let x : I32 = 0U64;");
	}

	/**
	 * Tests that a CompileException is thrown when a variable of one type
	 * is assigned to a variable of an incompatible type.
	 * Specifically, when a U64 variable is assigned to an I8 variable.
	 */
	@Test
	public void shouldThrowCompileExceptionForIncompatibleVariableReference() {
		// Arrange
		assertInvalid("let x = 0U64; let y : I8 = x;");
	}

	/**
	 * Tests that variable references are handled correctly.
	 * This test verifies that variables can reference other variables.
	 */
	@Test
	public void shouldSupportVariableReferences() {
		assertValid("let x = 100; let y = x;", "int32_t x = 100; int32_t y = x;");
	}

	/**
	 * Tests mutability behavior of variables.
	 * Tests that:
	 * - Mutable variables can be reassigned
	 * - Mutable variables with type annotations can be reassigned
	 * - Immutable variables cannot be reassigned
	 *
	 * @param input    the input string with variable declaration and optional reassignment
	 * @param expected the expected output or null if the input should be invalid
	 */
	@ParameterizedTest(name = "should handle mutability correctly: {0}")
	@CsvSource({"'let mut x = 200; x = 100;', 'int32_t x = 200; x = 100;'",
			"'let mut x : I32 = 200; x = 100;', 'int32_t x = 200; x = 100;'"})
	@DisplayName("Should handle variable mutability correctly")
	public void shouldHandleVariableMutability(String input, String expected) {
		assertValid(input, expected);
	}

	/**
	 * Tests that immutable variables cannot be reassigned.
	 * When a variable is declared without the 'mut' keyword, its value cannot be changed.
	 */
	@Test
	@DisplayName("Should not allow reassignment of immutable variables")
	public void shouldNotAllowReassignmentOfImmutableVariables() {
		assertInvalid("let x = 200; x = 100;");
	}

	/**
	 * Tests support for Bool type, true/false literals, and variable references.
	 * Verifies that Bool type annotations, boolean literals, and variable references are correctly compiled to C.
	 *
	 * @param input    the input string with Bool type
	 * @param expected the expected C output
	 */
	@ParameterizedTest(name = "should support Bool: {0} -> {1}")
	@CsvSource({"'let x : Bool = true;', 'bool x = true;'", "'let y : Bool = false;', 'bool y = false;'",
			"'let x : Bool = true; let y : Bool = x;', 'bool x = true; bool y = x;'"})
	@DisplayName("Should support Bool type with true/false literals and variable references")
	public void shouldSupportBoolType(String input, String expected) {
		assertValid(input, expected);
	}
}