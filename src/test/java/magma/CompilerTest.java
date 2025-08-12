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
		// Update this to test U8 to U64, and I8 through I64.
		assertValid("let x = 100I64;", "int64_t x = 100;");
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
}