package magma;

// helper assertions are in TestHelpers

import org.junit.jupiter.api.Test;

public class GenericsTest {
	@Test
	public void genericFnPassesValue() {
		TestHelpers.assertInterpEquals("fn pass<T>(value : T) => value; pass(100)", "100");
	}

	@Test
	public void genericFnTwoParams() {
		TestHelpers.assertInterpEquals("fn pass2<T, U>(a : T, b : U) => a; pass2(100, true)", "100");
	}
}
