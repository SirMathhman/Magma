package magma;

import org.junit.jupiter.api.Test;

class VariadicFunctionTest extends CompilerTestBase {
	@Test
	void variadicFunctionDefinition() {
		assertValid("fn acceptsVariadic<Length: USize>(...array : [I32; Length]) => {}",
								"");
	}

	@Test
	void variadicFunctionCall() {
		assertValid("fn acceptsVariadic<Length: USize>(...array : [I32; Length]) => {} acceptsVariadic(1, 2, 3);",
								"void acceptsVariadic_3(int32_t arg0, int32_t arg1, int32_t arg2){} acceptsVariadic_3(1, 2, 3);");
	}

	@Test
	void variadicFunctionWithDifferentLengths() {
		assertValid("fn sum<Length: USize>(...values : [I32; Length]) => {return 0;} sum(1); sum(1, 2);",
								"int32_t sum_1(int32_t arg0){return 0;} int32_t sum_2(int32_t arg0, int32_t arg1){return 0;} sum_1(1); sum_2(1, 2);");
	}

	@Test
	void variadicFunctionWithBody() {
		assertValid("fn process<Length: USize>(...items : [I32; Length]) => {let x = 42;}",
								"");
	}
}