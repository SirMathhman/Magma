package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ReturnTest {
	@Test
	void functionBlockCanReturnEarly() {
		assertValid("fn get() : I32 => { return 10; } ; get()", "10");
	}

	@Test
	void nestedBlockReturnPropagatesOutOfFunction() {
		assertValid("fn f() : I32 => { let x : I32; { x = 7; return x; } } ; f()", "7");
	}

	@Test
	void returnOutsideFunctionIsInvalid() {
		assertInvalid("{ return 1; }");
	}
}
