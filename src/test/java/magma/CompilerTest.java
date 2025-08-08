package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void compileNumericTypes() {
		// Explicit I32 type declaration
		assertValid("let x : I32 = 0;", "int32_t x = 0;");

		// All integer types
		// Unsigned
		assertValid("let x : U8 = 0;", "uint8_t x = 0;");
		assertValid("let x : U16 = 0;", "uint16_t x = 0;");
		assertValid("let x : U32 = 0;", "uint32_t x = 0;");
		assertValid("let x : U64 = 0;", "uint64_t x = 0;");
		// Signed
		assertValid("let x : I8 = 0;", "int8_t x = 0;");
		assertValid("let x : I16 = 0;", "int16_t x = 0;");
		assertValid("let x : I64 = 0;", "int64_t x = 0;");

		// Type suffix literal
		assertValid("let x = 0U8;", "uint8_t x = 0;");

		// Untyped integer defaults to I32
		assertValid("let x = 200;", "int32_t x = 200;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void compileBooleanTypes() {
		// Typed boolean literals
		assertValid("let x : Bool = true;", "bool x = true;");

		// Comparisons in typed Bool
		assertValid("let x : Bool = 1 < 2;", "bool x = 1 < 2;");

		// Untyped boolean defaults to Bool
		assertValid("let x = false;", "bool x = false;");
	}

	@Test
	void compileLetFromIdentifier() {
		assertValid("let a = 1; let b = 2; let c = a >= b;", "int32_t a = 1; int32_t b = 2; bool c = a >= b;");
		assertValid("let x = 5; let y = x;", "int32_t x = 5; int32_t y = x;");
		// Also supports mutable let and subsequent assignment
		assertValid("let mut x = 5; x = 100;", "int32_t x = 5; x = 100;");
	}

	@Test
	void invalid() {
		assertInvalid("?");
		assertInvalid("let x = 5; x = 100;");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	@Test
	void compileBlockWithBraces() {
		assertValid("{}", "{}");
		assertValid("{let x = 100;}", "{int32_t x = 100;}");
		assertValid("let x = 100; {let y = x;}", "int32_t x = 100;{int32_t y = x;}");
		assertInvalid("{let x = 100;} let y = x;");
	}

	@Test
	void compileIfStatement() {
		assertValid("if (true) {}", "if (true) {}");
		assertValid("if (false) {}", "if (false) {}");
		assertValid("if (true) {let x = 100;}", "if (true) {int32_t x = 100;}");
		assertValid("let x = true; if (x) {let y = 200;}", "bool x = true; if (x) {int32_t y = 200;}");
		assertValid("let x = 1; let y = 2; if (x < y) {let z = 3;}",
								"int32_t x = 1; int32_t y = 2; if (x < y) {int32_t z = 3;}");
	}
}