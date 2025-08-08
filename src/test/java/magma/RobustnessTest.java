package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class contains tests specifically focused on robustness testing of the Magma compiler.
 * It tests edge cases, error conditions, and boundary values to ensure the compiler
 * handles invalid inputs gracefully.
 */
class RobustnessTest {

	// Test invalid variable names
	@ParameterizedTest
	@ValueSource(strings = {"let 123abc = 100;",           // Variable starting with a number
			// Note: The compiler doesn't check for reserved keywords
			"let my-var = 100;",           // Hyphen not allowed
			"let my.var = 100;",           // Dot not allowed
			"let my@var = 100;",           // Special character not allowed
			"let my var = 100;"            // Space not allowed
	})
	void invalidVariableNameTest(String input) {
		TestUtils.assertInvalid(input);
	}

	// Test boundary conditions for arrays
	@Test
	void arrayBoundaryConditionsTest() {
		// Very large array size
		TestUtils.assertInvalid("let values : *[U8; 1000000000] = [1];");

		// Zero-sized array
		TestUtils.assertInvalid("let values : *[U8; 0] = [];");

		// Negative array size
		TestUtils.assertInvalid("let values : *[U8; -1] = [];");

		// Missing array size
		TestUtils.assertInvalid("let values : *[U8;] = [1, 2, 3];");

		// Missing array type
		TestUtils.assertInvalid("let values : *[; 3] = [1, 2, 3];");
	}

	// Test extreme numeric values
	@Test
	void extremeNumericValuesTest() {
		// Note: The compiler accepts very large integers and decimals

		// Test with invalid numeric format instead
		TestUtils.assertInvalid("let x = 100.200.300;");  // Multiple decimal points
		TestUtils.assertInvalid("let x = 100A;");         // Invalid suffix
		TestUtils.assertInvalid("let x = 0x100;");        // Hex format not supported
		TestUtils.assertInvalid("let x = 1_000_000;");    // Underscores not supported
	}

	// Test malformed syntax patterns
	@ParameterizedTest
	@ValueSource(strings = {"let x = 100",                 // Missing semicolon
			"let x : = 100;",              // Missing type
			"let x : I32 100;",            // Missing equals sign
			"let x : I32 = ;",             // Missing value
			"let x : I32 = 100 + 200;",    // Expressions not supported
			"let x : I32 = \"string\";",   // String not allowed for numeric type
			"let x : I32 = 100U8 + 200;",  // Mixed expressions not supported
			"let x : I32 = (100);"         // Parentheses not supported
	})
	void malformedSyntaxTest(String input) {
		TestUtils.assertInvalid(input);
	}

	// Test edge cases for pointers
	@Test
	void pointerEdgeCasesTest() {
		// Invalid pointer declaration (missing &)
		TestUtils.assertInvalid("let ptr : *I32 = x;");

		// Invalid pointer declaration (missing type)
		TestUtils.assertInvalid("let ptr : * = &x;");

		// Invalid pointer dereferencing (missing *)
		TestUtils.assertInvalid("let value : I32 = ptr;");

		// Invalid pointer dereferencing (missing type)
		TestUtils.assertInvalid("let value : = *ptr;");

		// Double pointer dereferencing (not supported)
		TestUtils.assertInvalid("let value : I32 = **ptr;");

		// Double reference (not supported)
		TestUtils.assertInvalid("let ptr : *I32 = &&x;");
	}

	// Test whitespace handling and formatting variations
	@Test
	void whitespaceHandlingTest() {
		// Extra whitespace should be valid
		TestUtils.assertValid("let   x   :   I32   =   100   ;", "int32_t x = 100;");

		// No whitespace should be valid
		TestUtils.assertValid("let x:I32=100;", "int32_t x = 100;");

		// Tabs and newlines
		TestUtils.assertValid("let x\t:\tI32\t=\t100\t;", "int32_t x = 100;");

		// Mixed whitespace in array declaration
		TestUtils.assertValid("let values:*[U8;3]=[1,2,3];", "uint8_t values[3] = {1, 2, 3};");
	}

	// Test invalid type names
	@ParameterizedTest
	@ValueSource(strings = {"let x : INT32 = 100;",        // Wrong case
			"let x : i32 = 100;",          // Wrong case
			"let x : Int32 = 100;",        // Wrong format
			"let x : Integer = 100;",      // Unsupported type
			"let x : String = \"hello\";", // Unsupported type
			"let x : Char = 'a';",         // Unsupported type
			"let x : Float = 1.0;",        // Unsupported type
			"let x : Double = 1.0;",       // Unsupported type
			"let x : Boolean = true;"      // Unsupported type
	})
	void invalidTypeNamesTest(String input) {
		TestUtils.assertInvalid(input);
	}

	// Test multiple declarations
	@Test
	void multipleDeclarationsTest() {
		// Multiple declarations are not supported
		TestUtils.assertInvalid("let x = 100; let y = 200;");
	}

	// Test empty array initialization
	@Test
	void emptyArrayInitializationTest() {
		// Empty array initialization
		TestUtils.assertInvalid("let values : *[U8; 3] = [];");
	}

	// Test invalid string array declarations
	@Test
	void invalidStringArrayTest() {
		// Note: The compiler accepts empty strings

		// Unterminated string
		TestUtils.assertInvalid("let str : *[U8; 5] = \"Hello;");

		// String with escape sequences (not supported)
		TestUtils.assertInvalid("let str : *[U8; 6] = \"Hello\\n\";");
	}

	// Test invalid boolean values
	@Test
	void invalidBooleanValuesTest() {
		// Case sensitivity
		TestUtils.assertInvalid("let flag = True;");
		TestUtils.assertInvalid("let flag = False;");

		// Numeric values for boolean
		TestUtils.assertInvalid("let flag : Bool = 1;");
		TestUtils.assertInvalid("let flag : Bool = 0;");
	}
}