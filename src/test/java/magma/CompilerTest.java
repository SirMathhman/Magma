package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void compileI32Let() {
		assertValid("let x : I32 = 0;", "int32_t x = 0;");
	}

	@Test
	void compileAllIntegerLets() {
		// Unsigned
		assertValid("let x : U8 = 0;", "uint8_t x = 0;");
		assertValid("let x : U16 = 0;", "uint16_t x = 0;");
		assertValid("let x : U32 = 0;", "uint32_t x = 0;");
		assertValid("let x : U64 = 0;", "uint64_t x = 0;");
		// Signed
		assertValid("let x : I8 = 0;", "int8_t x = 0;");
		assertValid("let x : I16 = 0;", "int16_t x = 0;");
		assertValid("let x : I64 = 0;", "int64_t x = 0;");
	}

	@Test
	void compileSuffixTypedLiteralU8() {
		assertValid("let x = 0U8;", "uint8_t x = 0;");
	}

	@Test
	void compileUntypedIntegerLiteralDefaultsToI32() {
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
	void compileTypedBoolLiteral() {
		assertValid("let x : Bool = true;", "bool x = true;");
		// Also allow comparisons in typed Bool
		assertValid("let x : Bool = 1 < 2;", "bool x = 1 < 2;");
	}

	@Test
	void compileUntypedBoolLiteralDefaultsToBool() {
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
}