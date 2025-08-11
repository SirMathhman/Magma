package magma;

import org.junit.jupiter.api.Test;

class StructTest extends CompilerTestBase {
	@Test
	void struct() {
		assertValid("struct Empty {}", "struct Empty {};");
	}

	@Test
	void field() {
		assertValid("struct Wrapper {x : I32}", "struct Wrapper {int32_t x;};");
	}
}