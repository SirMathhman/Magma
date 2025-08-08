package magma;

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
	 * Tests type suffixes for all unsigned integer types.
	 *
	 * @param typeSuffix the TypeScript type suffix (U8, U16, etc.)
	 * @param cType      the expected C type (uint8_t, uint16_t, etc.)
	 */
	@ParameterizedTest(name = "should convert {0} suffix to {1}")
	@CsvSource({"U8, uint8_t", "U16, uint16_t", "U32, uint32_t", "U64, uint64_t"})
	public void shouldConvertUnsignedTypeSuffixes(String typeSuffix, String cType) {
		assertValid("let x = 0" + typeSuffix + ";", cType + " x = 0;");
	}

	/**
	 * Tests type suffixes for all signed integer types.
	 *
	 * @param typeSuffix the TypeScript type suffix (I8, I16, etc.)
	 * @param cType      the expected C type (int8_t, int16_t, etc.)
	 */
	@ParameterizedTest(name = "should convert {0} suffix to {1}")
	@CsvSource({"I8, int8_t", "I16, int16_t", "I64, int64_t"})
	public void shouldConvertSignedTypeSuffixes(String typeSuffix, String cType) {
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
}