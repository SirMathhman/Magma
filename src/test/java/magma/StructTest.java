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

	@Test
	void parameterUsage() {
		assertValid("fn outer(a : I32) : Void => {fn inner() : Void => {let y = a;}}",
							"struct outer_t {int32_t a;}; void inner_outer(struct outer_t* this){int32_t y = this->a;} void outer(int32_t a){struct outer_t this; this.a = a;}");
	}
}