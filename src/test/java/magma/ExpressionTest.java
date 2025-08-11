package magma;

import org.junit.jupiter.api.Test;

class ExpressionTest extends CompilerTestBase {
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
	void arithmetic() {
		assertValid("fn add(a : I32, b : I32) : I32 => {return a + b;}",
							"int32_t add(int32_t a, int32_t b){return a + b;}");
	}
}