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
	 * Tests validation rules for variables and assignments.
	 * Verifies that:
	 * - Type incompatibility is caught when assigning values of different types
	 * - Immutable variables cannot be reassigned
	 * - Type errors are properly reported
	 */
	@Test
	@DisplayName("Should enforce variable and assignment validation rules")
	public void shouldEnforceValidationRules() {
		// Test incompatible literal value assignment
		assertInvalid("let x : I32 = 0U64;");

		// Test incompatible variable reference assignment
		assertInvalid("let x = 0U64; let y : I8 = x;");

		// Test that immutable variables cannot be reassigned
		assertInvalid("let x = 200; x = 100;");
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
	 *
	 * @param input    the input string with variable declaration and optional reassignment
	 * @param expected the expected output
	 */
	@ParameterizedTest(name = "should handle mutability correctly: {0}")
	@CsvSource({"'let mut x = 200; x = 100;', 'int32_t x = 200; x = 100;'",
			"'let mut x : I32 = 200; x = 100;', 'int32_t x = 200; x = 100;'"})
	@DisplayName("Should handle variable mutability correctly")
	public void shouldHandleVariableMutability(String input, String expected) {
		assertValid(input, expected);
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

	/**
	 * Tests addition operations with type checking.
	 * Verifies that:
	 * - Two numbers of the same type can be added (for all supported types)
	 * - Multiple numbers of the same type can be added in sequence (chained addition)
	 * - Numbers of different types cannot be added (throws CompileException)
	 *
	 * @param type  the type annotation (I8, I16, I32, etc.)
	 * @param cType the corresponding C type
	 */
	@ParameterizedTest(name = "should support addition of same types: {0}")
	@CsvSource({"I8, int8_t", "I16, int16_t", "I32, int32_t", "I64, int64_t", "U8, uint8_t", "U16, uint16_t",
			"U32, uint32_t", "U64, uint64_t"})
	@DisplayName("Should support addition operations with proper type checking")
	public void shouldHandleAdditionOperations(String type, String cType) {
		// Test addition with default I32 type
		if (type.equals("I32")) {
			// Basic addition
			assertValid("let x = 5; let y = 10; let z = x + y;", "int32_t x = 5; int32_t y = 10; int32_t z = x + y;");
			
			// Chained addition with literals
			assertValid("let x = 3 + 5 + 7;", "int32_t x = 3 + 5 + 7;");
			
			// Chained addition with variables
			assertValid(
				"let a = 3; let b = 5; let c = 7; let d = a + b + c;",
				"int32_t a = 3; int32_t b = 5; int32_t c = 7; int32_t d = a + b + c;"
			);
			
			// Chained addition with mixed literals and variables
			assertValid(
				"let a = 3; let b = 5; let c = a + b + 7;",
				"int32_t a = 3; int32_t b = 5; int32_t c = a + b + 7;"
			);
			
			// Test type incompatibility in addition
			assertInvalid("let x : I32 = 5; let y : I64 = 10; let z = x + y;");
			
			// Test type incompatibility in chained addition
			assertInvalid("let a : I32 = 3; let b : I64 = 5; let c = a + b + 7;");
		}

		// Test addition with explicit type
		assertValid("let x : " + type + " = 5; let y : " + type + " = 10; let z : " + type + " = x + y;",
								cType + " x = 5; " + cType + " y = 10; " + cType + " z = x + y;");
		
		// Test chained addition with explicit type (only for I16 to demonstrate with a different type)
		if (type.equals("I16")) {
			assertValid(
				"let a : I16 = 3; let b : I16 = 5; let c : I16 = a + b + 7;",
				"int16_t a = 3; int16_t b = 5; int16_t c = a + b + 7;"
			);
		}
	}

	/**
	 * Tests logical operations (|| and &&) with type checking.
	 * Verifies that:
	 * - Bool values can be combined with logical operators
	 * - Only Bool types can be used with logical operators
	 *
	 * @param operator     the logical operator to test ("||" or "&&")
	 * @param operatorName the name of the operator for display ("OR" or "AND")
	 */
	@ParameterizedTest(name = "should support logical {1} operations with proper type checking")
	@CsvSource({"||, OR", "&&, AND"})
	@DisplayName("Should support logical operations with proper type checking")
	public void shouldHandleLogicalOperations(String operator, String operatorName) {
		// Test logical operation with boolean literals
		assertValid("let x : Bool = true " + operator + " false;", "bool x = true " + operator + " false;");

		// Test logical operation with boolean variables
		assertValid("let a : Bool = true; let b : Bool = false; let c : Bool = a " + operator + " b;",
								"bool a = true; bool b = false; bool c = a " + operator + " b;");

		// Test type incompatibility in logical operations (should not allow non-Bool types)
		assertInvalid("let x : I32 = 1; let y : Bool = true; let z : Bool = x " + operator + " y;");
		assertInvalid("let x : Bool = true; let y : I32 = 1; let z : Bool = x " + operator + " y;");
	}
}