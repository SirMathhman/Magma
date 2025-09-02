package magma.feature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

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

	@Test
	void classTest() {
		/*
		 * class fn Wrapper(field : I32) => {
		 * }
		 * 
		 * should be equivalent to:
		 * 
		 * fn Wrapper(field : I32) => {
		 *   this;
		 * }
		 */
		assertAllValidWithPrelude("""
			class fn Wrapper(field : I32) => {
			}

			let wrapper = Wrapper(readInt());
			wrapper.field
			""", "100", "100");
	}

	@Disabled("Pending: support for nested functions in `class fn` to be implemented")
	@Test
	void classWithInnerFunction() {
		// Future test: class factory that defines an inner fn should expose the method
		// once `class fn` nested-fn-to-method conversion is implemented.
		assertAllValidWithPrelude("""
			class fn Maker(field : I32) => {
				fn getField() => this.field
			}

			let m = Maker(readInt());
			m.getField()
			""", "42", "42");
	}
}
