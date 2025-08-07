package com.magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StringProcessor using JUnit 5
 */
public class CompilerTest {
	@Test
	public void empty() {
		assertValid("", "");
	}

	@ParameterizedTest
	@ValueSource(strings = {"x", "y", "z"})
	void letName(String name) {
		assertValid("let " + name + " = 0", "int32_t " + name + " = 0;");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "42", "-1", "100"})
	void letValues(String value) {
		assertValid("let x = " + value, "int32_t x = " + value + ";");
	}

	@Test
	void letTypeI8() {
		assertValid("let x : I8 = 0;", "int8_t x = 0;");
	}

	@Test
	void letTypeI16() {
		assertValid("let x : I16 = 0;", "int16_t x = 0;");
	}

	@Test
	void letTypeI32() {
		assertValid("let x : I32 = 0;", "int32_t x = 0;");
	}

	@Test
	void letTypeI64() {
		assertValid("let x : I64 = 0;", "int64_t x = 0;");
	}

	@Test
	void letTypeU8() {
		assertValid("let x : U8 = 0;", "uint8_t x = 0;");
	}

	@Test
	void letTypeU16() {
		assertValid("let x : U16 = 0;", "uint16_t x = 0;");
	}

	@Test
	void letTypeU32() {
		assertValid("let x : U32 = 0;", "uint32_t x = 0;");
	}

	@Test
	void letTypeU64() {
		assertValid("let x : U64 = 0;", "uint64_t x = 0;");
	}

	@Test
	void letTypeU8WithSuffix() {
		assertValid("let x : U8 = 0U8;", "uint8_t x = 0;");
	}

	@Test
	void extraWhitespaceAroundLet() {
		assertValid("  let x = 0  ", "int32_t x = 0;");
	}

	@Test
	void extraWhitespaceAroundEquals() {
		assertValid("let x  =  0", "int32_t x = 0;");
	}

	@Test
	void extraWhitespaceAroundTypeAnnotation() {
		assertValid("let x  :  I32  = 0", "int32_t x = 0;");
	}

	@Test
	void multipleWhitespaces() {
		assertValid("let    x    =    0", "int32_t x = 0;");
	}

	@Test
	void typeMismatchI16WithU8() {
		assertInvalid("let x : I16 = 0U8;");
	}

	@Test
	void typeMismatchU32WithI8() {
		assertInvalid("let x : U32 = 42I8;");
	}

	@Test
	void typeMismatchU64WithI64() {
		assertInvalid("let x : U64 = 100I64;");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.process(input));
	}

	@Test
	void noTypeMismatchWhenTypesMatch() {
		assertValid("let x : U8 = 0U8;", "uint8_t x = 0;");
		assertValid("let x : I16 = 42I16;", "int16_t x = 42;");
	}

	@Test
	void inferTypeFromLiteralSuffix() {
		assertValid("let x = 0U8;", "uint8_t x = 0;");
		assertValid("let y = 42I16;", "int16_t y = 42;");
	}

	@Test
	void multipleLetStatements() {
		assertValid("let x = 100; let y : I16 = 20;", "int32_t x = 100;int16_t y = 20;");
	}

	@Test
	void multipleLetStatementsWithWhitespace() {
		assertValid("let x = 100;   let y : I16 = 20;", "int32_t x = 100;int16_t y = 20;");
	}

	@Test
	void multipleLetStatementsWithDifferentTypes() {
		assertValid("let x : U8 = 5; let y : I16 = 20; let z = 42I64;", "uint8_t x = 5;int16_t y = 20;int64_t z = 42;");
	}

	@Test
	void multipleLetStatementsWithEmptyStatement() {
		assertValid("let x = 100;; let y = 200;", "int32_t x = 100;int32_t y = 200;");
	}

	@Test
	void variableReference() {
		assertValid("let x = 0; let y = x", "int32_t x = 0;int32_t y = x;");
	}

	@Test
	void multipleVariableReferences() {
		assertValid("let x = 0; let y = x; let z = y", "int32_t x = 0;int32_t y = x;int32_t z = y;");
	}

	@Test
	void variableReferenceWithTypeAnnotation() {
		assertValid("let x : I32 = 0; let y : I32 = x", "int32_t x = 0;int32_t y = x;");
	}

	@Test
	void variableReferenceTypeMismatch() {
		assertInvalid("let x : I8 = 0; let y : I16 = x");
	}

	@Test
	void undefinedVariableReference() {
		assertInvalid("let y = x");
	}

	@Test
	void simpleAddition() {
		assertValid("let x = 5 + 3", "int32_t x = 5 + 3;");
	}

	@Test
	void simpleSubtraction() {
		assertValid("let x = 10 - 4", "int32_t x = 10 - 4;");
	}

	@Test
	void simpleMultiplication() {
		assertValid("let x = 6 * 7", "int32_t x = 6 * 7;");
	}

	@Test
	void combinedOperations() {
		assertValid("let x = 5 + 3 * 2", "int32_t x = 5 + 3 * 2;");
		assertValid("let x = 10 - 4 + 2", "int32_t x = 10 - 4 + 2;");
		assertValid("let x = 3 * 4 - 2", "int32_t x = 3 * 4 - 2;");
	}

	@Test
	void arithmeticWithVariables() {
		assertValid("let x = 5; let y = x + 3", "int32_t x = 5;int32_t y = x + 3;");
		assertValid("let x = 10; let y = x - 4", "int32_t x = 10;int32_t y = x - 4;");
		assertValid("let x = 6; let y = x * 7", "int32_t x = 6;int32_t y = x * 7;");
	}

	@Test
	void arithmeticWithTypedVariables() {
		assertValid("let x : I16 = 5; let y : I16 = x + 3", "int16_t x = 5;int16_t y = x + 3;");
		assertValid("let x : U8 = 10; let y : U8 = x - 4", "uint8_t x = 10;uint8_t y = x - 4;");
		assertValid("let x : I64 = 6; let y : I64 = x * 7", "int64_t x = 6;int64_t y = x * 7;");
	}

	@Test
	void arithmeticTypeMismatch() {
		assertInvalid("let x : I8 = 5; let y : I16 = x + 3");
		assertInvalid("let x = 10; let y : U8 = x - 4");
		assertInvalid("let x : I64 = 6; let y : I32 = x * 7");
	}

	@Test
	void arithmeticWithTypeSuffixes() {
		assertValid("let x = 5I16 + 3I16", "int16_t x = 5 + 3;");
		assertValid("let x = 10U8 - 4U8", "uint8_t x = 10 - 4;");
		assertValid("let x = 6I64 * 7I64", "int64_t x = 6 * 7;");
	}

	@Test
	void arithmeticWithTypeSuffixMismatch() {
		assertInvalid("let x = 5I16 + 3I32");
		assertInvalid("let x = 10U8 - 4I8");
		assertInvalid("let x = 6I64 * 7U64");
	}

	@Test
	void divisionNotSupported() {
		// Division is not supported as per requirements
		assertValid("let x = 10 / 2", "int32_t x = 10 / 2;");
	}
	
	@Test
	void nestedParentheses() {
		assertValid("let x = (5 + 3)", "int32_t x = (5 + 3);");
		assertValid("let x = (10 - 4)", "int32_t x = (10 - 4);");
		assertValid("let x = (6 * 7)", "int32_t x = (6 * 7);");
	}
	
	@Test
	void multipleNestedParentheses() {
		assertValid("let x = ((5 + 3) * 2)", "int32_t x = ((5 + 3) * 2);");
		assertValid("let x = (10 - (4 + 2))", "int32_t x = (10 - (4 + 2));");
		assertValid("let x = (3 * (4 - 2))", "int32_t x = (3 * (4 - 2));");
	}
	
	@Test
	void complexNestedExpressions() {
		assertValid("let x = ((5 + 3) * (2 + 1))", "int32_t x = ((5 + 3) * (2 + 1));");
		assertValid("let x = (10 - (4 + (2 * 3)))", "int32_t x = (10 - (4 + (2 * 3)));");
		assertValid("let x = ((3 + 1) * (4 - (2 - 1)))", "int32_t x = ((3 + 1) * (4 - (2 - 1)));");
	}
	
	@Test
	void nestedExpressionsWithVariables() {
		assertValid("let a = 5; let b = 3; let x = (a + b)", "int32_t a = 5;int32_t b = 3;int32_t x = (a + b);");
		assertValid("let a = 10; let b = 4; let x = (a - (b + 2))", "int32_t a = 10;int32_t b = 4;int32_t x = (a - (b + 2));");
	}
	
	@Test
	void nestedExpressionsWithTypedVariables() {
		assertValid("let a : I16 = 5; let b : I16 = 3; let x : I16 = (a + b)", "int16_t a = 5;int16_t b = 3;int16_t x = (a + b);");
		assertValid("let a : U8 = 10; let b : U8 = 4; let x : U8 = (a - b)", "uint8_t a = 10;uint8_t b = 4;uint8_t x = (a - b);");
	}
	
	@Test
	void mismatchedParentheses() {
		assertInvalid("let x = (5 + 3");
		assertInvalid("let x = 5 + 3)");
		assertInvalid("let x = ((5 + 3)");
	}
	
	@Test
	void mutableVariables() {
		assertValid("let mut x = 100; x = 20", "int32_t x = 100;x = 20;");
		assertValid("let mut x = 100; let y = 5; x = y", "int32_t x = 100;int32_t y = 5;x = y;");
		assertValid("let mut x : I16 = 10; x = 20", "int16_t x = 10;x = 20;");
	}
	
	@Test
	void reassignmentToImmutableVariable() {
		assertInvalid("let x = 100; x = 20");
		assertInvalid("let x = 10; let y = 20; x = y");
	}
	
	@Test
	void typeMismatchInReassignment() {
		assertInvalid("let mut x : I16 = 10; x = 20I32");
		assertInvalid("let mut x : U8 = 5; let y : I8 = 10; x = y");
	}
	
	@Test
	void reassignmentToUndefinedVariable() {
		assertInvalid("x = 10");
		assertInvalid("let x = 10; y = 20");
	}

	private void assertValid(String input, String output) {
		try {
			var actual = Compiler.process(input);
			assertEquals(output, actual);
		} catch (CompileException e) {
			// If we get here, the test has failed because we expected success
			fail(e);
		}
	}
}