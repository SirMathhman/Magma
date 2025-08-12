package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void let() {
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}

	@Test
	void letWithDifferentName() {
		assertValid("let y : I32 = 100;", "int32_t y = 100;");
	}

	@Test
	void letWithTypeInference() {
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letWithDifferentValue() {
		assertValid("let x = 200;", "int32_t x = 200;");
	}

	@Test
	void letWithMutModifier() {
		assertValid("let mut x = 100; x = 200;", "int32_t x = 100; x = 200;");
	}

	@Test
	void letWithoutMutModifier() {
		assertInvalid("let x = 100; x = 200;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.run(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.run(input));
	}

	@Test
	void intWithTypeSuffix() {
		assertValid("let x : I32 = 100I32;", "int32_t x = 100;");
	}

	@Test
	void intWithDifferentType() {
		assertValid("let x = 100I64;", "int64_t x = 100;");
	}

	@Test
	void testU8Type() {
		assertValid("let x = 100U8;", "uint8_t x = 100;");
	}

	@Test
	void testU16Type() {
		assertValid("let x = 100U16;", "uint16_t x = 100;");
	}

	@Test
	void testU32Type() {
		assertValid("let x = 100U32;", "uint32_t x = 100;");
	}

	@Test
	void testU64Type() {
		assertValid("let x = 100U64;", "uint64_t x = 100;");
	}

	@Test
	void testI8Type() {
		assertValid("let x = 100I8;", "int8_t x = 100;");
	}

	@Test
	void testI16Type() {
		assertValid("let x = 100I16;", "int16_t x = 100;");
	}

	@Test
	void testTrue() {
		assertValid("let x : Bool = true;", "bool x = true;");
	}

	@Test
	void testFalse() {
		assertValid("let x : Bool = false;", "bool x = false;");
	}

	@Test
	void boolWithImplicitType() {
		assertValid("let x = true;", "bool x = true;");
	}

	@Test
	void float32() {
		assertValid("let x : F32 = 3.14F32;", "float x = 3.14f;");
	}

	@Test
	void float64() {
		assertValid("let x : F64 = 3.14F64;", "double x = 3.14;");
	}

	@Test
	void floatDefaultsToF32() {
		assertValid("let x = 3.14;", "float x = 3.14f;");
	}

	@Test
	void float32WithExplicitSuffix() {
		assertValid("let x = 3.14F32;", "float x = 3.14f;");
	}

	@Test
	void float64WithExplicitSuffix() {
		assertValid("let x = 3.14F64;", "double x = 3.14;");
	}

	@Test
	void testCharAsU8() {
		assertValid("let x : U8 = 'a';", "uint8_t x = 'a';");
	}

	@Test
	void letTypeMismatch() {
		assertInvalid("let x : I32 = 3.14F32;");
	}

	@Test
	void arrayEmpty() {
		assertValid("let x : [I32; 0] = [];", "int32_t x[0] = {};");
	}

	@Test
	void arrayWithOneElement() {
		assertValid("let x : [I32; 1] = [42];", "int32_t x[1] = {42};");
	}

	@Test
	void arrayWithMultipleElements() {
		assertValid("let x : [I32; 3] = [1, 2, 3];", "int32_t x[3] = {1, 2, 3};");
	}

	@Test
	void array2d() {
		assertValid("let x : [I32; 2, 2] = [[1, 2], [3, 4]];", "int32_t x[2][2] = {{1, 2}, {3, 4}};");
	}

	@Test
	void array2dNotSquare() {
		assertValid("let x : [I32; 2, 3] = [[1, 2, 3], [4, 5, 6]];", "int32_t x[2][3] = {{1, 2, 3}, {4, 5, 6}};");
	}

	@Test
	void stringsAsU8Array() {
		assertValid("let x : [U8; 5] = \"Hello\";", "uint8_t x[5] = {'H', 'e', 'l', 'l', 'o'};");
	}

	@Test
	void arrayIndexGet() {
		assertValid("let x : [I32; 3] = [1, 2, 3]; let y = x[1];", "int32_t x[3] = {1, 2, 3}; int32_t y = x[1];");
	}

	@Test
	void arrayIndexGetTypeMismatch() {
		assertInvalid("let x : [I32; 3] = [1, 2, 3]; let y : U32 = x[1];");
	}

	@Test
	void arrayIndexSetWithMut() {
		assertValid("let mut x : [I32; 3] = [1, 2, 3]; x[1] = 42;", "int32_t x[3] = {1, 2, 3}; x[1] = 42;");
	}

	@Test
	void arrayIndexSetWithoutMut() {
		assertInvalid("let x : [I32; 3] = [1, 2, 3]; x[1] = 42;");
	}

	@Test
	void arrayIndexSetTypeMismatch() {
		assertInvalid("let mut x : [I32; 3] = [1, 2, 3]; x[1] = 42U32;");
	}


	@Test
	void arrayLengthPresentAtCompileTime() {
		assertValid("let x : [I32; 3] = [1, 2, 3]; let len : USize = x.length;",
								"int32_t x[3] = {1, 2, 3}; usize_t len = 3;");
	}

	@Test
	void arrayLengthWithoutTypePresent() {
		assertValid("let x = [1, 2, 3]; let len = x.length;", "int32_t x[3] = {1, 2, 3}; usize_t len = 3;");
	}

	@Test
	void arrayLengthTypeMismatch() {
		assertInvalid("let x : [I32; 3] = [1, 2, 3]; let len : I32 = x.length;");
	}

	@Test
	void arrayLengthWithMut() {
		assertValid("let mut x : [I32; 3] = [1, 2, 3]; let len = x.length;", "int32_t x[3] = {1, 2, 3}; usize_t len = 3;");
	}

	@Test
	void lessThan() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : Bool = x < y;",
								"int32_t x = 5; int32_t y = 10; bool z = x < y;");
	}

	@Test
	void lessThanOrEqual() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : Bool = x <= y;",
								"int32_t x = 5; int32_t y = 10; bool z = x <= y;");
	}

	@Test
	void greaterThan() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : Bool = x > y;",
								"int32_t x = 5; int32_t y = 10; bool z = x > y;");
	}

	@Test
	void greaterThanOrEqual() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : Bool = x >= y;",
								"int32_t x = 5; int32_t y = 10; bool z = x >= y;");
	}

	@Test
	void equals() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : Bool = x == y;",
								"int32_t x = 5; int32_t y = 10; bool z = x == y;");
	}

	@Test
	void notEquals() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : Bool = x != y;",
								"int32_t x = 5; int32_t y = 10; bool z = x != y;");
	}

	@Test
	void logicalAnd() {
		assertValid("let x : Bool = true; let y : Bool = false; let z : Bool = x && y;",
								"bool x = true; bool y = false; bool z = x && y;");
	}

	@Test
	void logicalOr() {
		assertValid("let x : Bool = true; let y : Bool = false; let z : Bool = x || y;",
								"bool x = true; bool y = false; bool z = x || y;");
	}

	@Test
	void logicalNot() {
		assertValid("let x : Bool = true; let y : Bool = !x;", "bool x = true; bool y = !x;");
	}

	@Test
	void refinedTypeConstant() {
		assertValid("let x : 5I32 = 5;", "int32_t x = 5;");
	}

	@Test
	void refinedTypeComparison() {
		assertValid("let x : 5I32 = 5; let y : 10I32 = 10; let z : x < y = x < y;",
								"int32_t x = 5; int32_t y = 10; bool z = x < y;");
	}

	@Test
	void refinedTypeComparisonWithInlineTypes() {
		assertValid("let x : 5I32 = 5; let y : 10I32 = 10; let z : 5I32 < 10I32 = x < y;",
								"int32_t x = 5; int32_t y = 10; bool z = x < y;");
	}

	@Test
	void refinedTypeWithEvaluatedType() {
		assertValid("let x : 5I32 = 5; let y : 10I32 = 10; let z : true = x < y;",
								"int32_t x = 5; int32_t y = 10; bool z = x < y;");
	}

	@Test
	void refinedTypeMismatch() {
		assertInvalid("let x : 5I32 = 6;");
	}

	@Test
	void refinedTypeLessRestrictive() {
		assertValid("let x : 10I32 = 10; let y : I32 = x;", "int32_t x = 10; int32_t y = x;");
	}

	@Test
	void refinedTypeMoreRestrictive() {
		assertInvalid("let x : I32 = 10; let y : 5I32 = x;");
	}

	@Test
	void additionOperator() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : I32 = x + y;",
								"int32_t x = 5; int32_t y = 10; int32_t z = x + y;");
	}

	@Test
	void subtractionOperator() {
		assertValid("let x : I32 = 10; let y : I32 = 5; let z : I32 = x - y;",
								"int32_t x = 10; int32_t y = 5; int32_t z = x - y;");
	}

	@Test
	void multiplicationOperator() {
		assertValid("let x : I32 = 5; let y : I32 = 10; let z : I32 = x * y;",
								"int32_t x = 5; int32_t y = 10; int32_t z = x * y;");
	}

	@Test
	void divisionOperator() {
		assertValid("let x : I32 = 10; let y : I32 = 5; let z : I32 = x / y;",
								"int32_t x = 10; int32_t y = 5; int32_t z = x / y;");
	}

	@Test
	void moduloOperator() {
		assertValid("let x : I32 = 10; let y : I32 = 3; let z : I32 = x % y;",
								"int32_t x = 10; int32_t y = 3; int32_t z = x % y;");
	}

	@Test
	void unaryPlusOperator() {
		assertValid("let x : I32 = 5; let y : I32 = +x;", "int32_t x = 5; int32_t y = +x;");
	}

	@Test
	void unaryMinusOperator() {
		assertValid("let x : I32 = 5; let y : I32 = -x;", "int32_t x = 5; int32_t y = -x;");
	}

	@Test
	void bitwiseAndOperator() {
		assertValid("let x : I32 = 5; let y : I32 = 3; let z : I32 = x & y;",
								"int32_t x = 5; int32_t y = 3; int32_t z = x & y;");
	}

	@Test
	void bitwiseOrOperator() {
		assertValid("let x : I32 = 5; let y : I32 = 3; let z : I32 = x | y;",
								"int32_t x = 5; int32_t y = 3; int32_t z = x | y;");
	}

	@Test
	void bitwiseXorOperator() {
		assertValid("let x : I32 = 5; let y : I32 = 3; let z : I32 = x ^ y;",
								"int32_t x = 5; int32_t y = 3; int32_t z = x ^ y;");
	}

	@Test
	void leftShiftOperator() {
		assertValid("let x : I32 = 5; let y : I32 = 2; let z : I32 = x << y;",
								"int32_t x = 5; int32_t y = 2; int32_t z = x << y;");
	}

	@Test
	void rightShiftOperator() {
		assertValid("let x : I32 = 20; let y : I32 = 2; let z : I32 = x >> y;",
								"int32_t x = 20; int32_t y = 2; int32_t z = x >> y;");
	}

	@Test
	void bitwiseNotOperator() {
		assertValid("let x : I32 = 5; let y : I32 = ~x;", "int32_t x = 5; int32_t y = ~x;");
	}

	@Test
	void operatorPrecedenceArithmetic() {
		assertValid("let a : I32 = 2; let b : I32 = 3; let c : I32 = 4; let result : I32 = a + b * c;",
								"int32_t a = 2; int32_t b = 3; int32_t c = 4; int32_t result = a + b * c;");
	}

	@Test
	void operatorPrecedenceWithParens() {
		assertValid("let a : I32 = 2; let b : I32 = 3; let c : I32 = 4; let result : I32 = (a + b) * c;",
								"int32_t a = 2; int32_t b = 3; int32_t c = 4; int32_t result = (a + b) * c;");
	}

	@Test
	void complexExpressionWithComparison() {
		assertValid("let a : I32 = 10; let b : I32 = 5; let c : I32 = 3; let result : Bool = a > b + c;",
								"int32_t a = 10; int32_t b = 5; int32_t c = 3; bool result = a > b + c;");
	}

	@Test
	void complexExpressionWithLogical() {
		assertValid("let a : Bool = true; let b : Bool = false; let c : Bool = true; let result : Bool = a && b || c;",
								"bool a = true; bool b = false; bool c = true; bool result = a && b || c;");
	}
}