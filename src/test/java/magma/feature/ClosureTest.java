package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class ClosureTest {
	@Test
	void thisContainsLocalDeclaration() {
		assertAllValidWithPrelude("fn get() => { let x = readInt(); this } get().x", "100", "100");
	}

	@Test
	void thisContainsParameter() {
		assertAllValidWithPrelude("fn get(x : I32) => { this } get(readInt()).x", "100", "100");
	}

	@Test
	void global() {
		assertAllValidWithPrelude("let mut x = 0; fn inc() => x += readInt(); inc(); x", "10", "10");
	}

	@Test
	void functionWithinFunction() {
		assertAllValidWithPrelude("fn outer() => { fn inner() => readInt(); inner() }; outer()", "100", "100");
	}

	@Test
	void thisContainsFunction() {
		assertAllValidWithPrelude("fn get() => { fn inner() => readInt(); this } get().inner()", "100", "100");
	}

	@Test
	void thisReturnedByInnerFunction() {
		assertAllValidWithPrelude("fn get() => { let value = readInt(); fn inner() => this; inner() }; get().value", "100",
															"100");
	}
}
