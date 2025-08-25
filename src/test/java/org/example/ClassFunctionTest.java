package org.example;

import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ClassFunctionTest {

	@Test
	void classFnEquivalentToReturningThis() {
		// class fn Point(x : I32, y : I32) => {} behaves like fn Point(...) => this;
		assertValid("class fn Point(x : I32, y : I32) => {}; Point(3,4).x", "3");
		assertValid("class fn Point(x : I32, y : I32) => {}; Point(3,4).y", "4");
	}

	@Test
	void classFnAllowsNestedMethodsViaThis() {
		// Methods declared in the body should be accessible via returned object
		String prog = "class fn Box(v : I32) => { fn get() => v; } Box(9).get()";
		assertValid(prog, "9");
	}

	@Test
	void classFnAndNormalFnEquivalentBehavior() {
		String prog1 = "class fn P(x : I32) => {}; P(7).x";
		String prog2 = "fn Q(x : I32) => this; Q(7).x";
		assertValid(prog1, "7");
		assertValid(prog2, "7");
	}
}
