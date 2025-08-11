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

	@Test
	void classWithMethod() {
		assertValid("class fn Point(x : I32, y : I32) => {fn manhattan() => {return x + y;}}",
							"struct Point {int32_t x; int32_t y;}; int32_t manhattan_Point(struct Point* this){return this->x + this->y;} struct Point Point(int32_t x, int32_t y){struct Point this; this.x = x; this.y = y; return this;}");
	}

	@Test
	void emptyClassConstructorCall() {
		assertValid("class fn Empty() => {} let value = Empty();",
							"struct Empty {}; struct Empty Empty(){struct Empty this; return this;} struct Empty value = Empty();");
	}
}