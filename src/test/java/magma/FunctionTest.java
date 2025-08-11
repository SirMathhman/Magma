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
		assertValid("fn outer() : I32 => {fn inner() : I32 => {return 42;} return inner();}",
							"int32_t inner_outer(){return 42;} int32_t outer(){return inner_outer();}");
	}
}