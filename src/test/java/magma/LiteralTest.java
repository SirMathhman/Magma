package magma;

import org.junit.jupiter.api.Test;

class LiteralTest extends CompilerTestBase {
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
}