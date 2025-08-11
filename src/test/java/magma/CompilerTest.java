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
		assertValid("let x = 10;", "int32_t x = 10;");
	}

	@Test
	void letName() {
		assertValid("let y = 10;", "int32_t y = 10;");
	}

	@Test
	void letValue() {
		assertValid("let y = 20;", "int32_t y = 20;");
	}

	@Test
	void letType() {
		assertValid("let y : I32 = 20;", "int32_t y = 20;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void letTypeSuffix() {
		assertValid("let x = 200U64;", "uint64_t x = 200;");
	}

	@Test
	void letTypeSuffixSigned() {
		assertValid("let y = 127I8;", "int8_t y = 127;");
	}

	@Test
	void conflictingTypes() {
		assertInvalid("let x : U64 = 100I32;");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	@Test
	void matchingTypes() {
		assertValid("let x : U32 = 100U32;", "uint32_t x = 100;");
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	@Test
	void identifier() {
		assertValid("let x = 100; let y = x;", "int32_t x = 100; int32_t y = x;");
	}

	@Test
	void mutValid() {
		assertValid("let mut x = 200; x = 100;", "int32_t x = 200; x = 100;");
	}

	@Test
	void mutInvalid() {
		assertInvalid("let x = 200; x = 200;");
	}

	@Test
	void trueValue() {
		assertValid("let value : Bool = true", "bool value = true;");
	}

	@Test
	void falseValue() {
		assertValid("let value : Bool = false;", "bool value = false;");
	}

	@Test
	void charLiteral() {
		assertValid("let x: U8 = 'a';", "uint8_t x = 'a';");
	}

	@Test
	void arrays() {
		assertValid("let x : [U8; 3] = [1, 2, 3]; ", "uint8_t x[3] = { 1, 2, 3 };");
	}

	@Test
	void arrays2D() {
		assertValid("let x : [U8; 2, 2] = [[1, 2], [3, 4]];", "uint8_t x[2][2] = { { 1, 2 }, { 3, 4 } };");
	}

	@Test
	void arraysDiffDimensions() {
		assertValid("let x : [U8; 2, 1] = [[1], [2]];", "uint8_t x[2][1] = { { 1 }, { 2 } };");
	}

	@Test
	void strings() {
		assertValid("let strings : [U8; 5] = \"Hello\";", "uint8_t strings[5] = \"Hello\";");
	}

	@Test
	void indexGet() {
		assertValid("let array = [1, 2, 3]; let value = array[0];",
								"uint8_t array[3] = { 1, 2, 3 }; uint8_t value = array[0];");
	}

	@Test
	void indexSet() {
		assertValid("let array = [1, 2, 3]; array[0] = 100;", "uint8_t array[3] = { 1, 2, 3 }; array[0] = 100;");
	}

	@Test
	void reference() {
		assertValid("let x = 100; let y : *I32 = &x;", "int32_t x = 100; int32_t* y = &x;");
	}

	@Test
	void dereference() {
		assertValid("let x = 100; let y = &x; let z = *y;", "int32_t x = 100; int32_t* y = &x; int32_t z = *y;");
	}

	@Test
	void braces() {
		assertValid("{}", "{}");
	}

	@Test
	void statementAndBraces() {
		assertValid("let x = 100; {}", "int32_t x = 100; {}");
	}

	@Test
	void statementInBraces() {
		assertValid("{let x = 100;}", "{int32_t x = 100;}");
	}

	@Test
	void testIf() {
		assertValid("if(true){let x = 100;}", "if(true){int32_t x = 100;}");
	}

	@Test
	void testElse() {
		assertValid("if(true){let x = 100;} else {}", "if(true){int32_t x = 100;} else {}");
	}

	@Test
	void testWhile() {
		assertValid("while(true){let x = 100;}", "while(true){int32_t x = 100;}");
	}
}