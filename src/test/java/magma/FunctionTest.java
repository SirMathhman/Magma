package magma;

import org.junit.jupiter.api.Test;

class FunctionTest extends CompilerTestBase {
	@Test
	void function() {
		assertValid("fn empty() => {}", "void empty(){}");
	}

	@Test
	void functionBody() {
		assertValid("fn body() => {return 100;}", "int32_t body(){return 100;}");
	}

	@Test
	void returns() {
		assertValid("fn empty() : Void => {}", "void empty(){}");
	}

	@Test
	void param() {
		assertValid("fn consume(value : I32) => {}", "void consume(int32_t value){}");
	}

	@Test
	void params() {
		assertValid("fn validate(x : I32, y : I32) => {}", "void validate(int32_t x, int32_t y){}");
	}

	@Test
	void callStatement() {
		assertValid("fn empty() => {} empty();", "void empty(){} empty();");
	}

	@Test
	void callExpression() {
		assertValid("fn empty() => {return 100;} let value = empty();",
							"int32_t empty(){return 100;} int32_t value = empty();");
	}

	@Test
	void innerFunction() {
		assertValid("fn outer() : Void => {fn inner() : Void => {}}",
							"void inner_outer(){} void outer(){}");
	}

	@Test
	void declarationToField() {
		assertValid("fn outer() : Void => {let x = 100; fn inner() : Void => {}}",
								"struct outer_t {int32_t x;}; void inner_outer(){} void outer(){struct outer_t this; this.x = 100;}");
	}

	@Test
	void functionWithArithmetic() {
		assertValid("fn add(a : I32, b : I32) : I32 => {return a + b;}",
							"int32_t add(int32_t a, int32_t b){return a + b;}");
	}
}