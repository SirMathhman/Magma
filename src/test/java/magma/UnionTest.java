package magma;

import org.junit.jupiter.api.Test;

public class UnionTest {
	@Test
	public void unionClassFnsIsCheck() {
		TestUtils.assertValid("class fn Ok() => Ok { } class fn Err() => Err { } type Result = Ok | Err; let a : Result = Ok(); a is Ok", "true");
		TestUtils.assertValid("class fn Ok() => Ok { } class fn Err() => Err { } type Result = Ok | Err; let b : Result = Err(); b is Err", "true");
	}

	@Test
	public void unionStructsIsCheck() {
		TestUtils.assertValid("struct Ok { } struct Err { } type Result = Ok | Err; let a : Result = Ok { }; a is Ok", "true");
		TestUtils.assertValid("struct Ok { } struct Err { } type Result = Ok | Err; let b : Result = Err { }; b is Err", "true");
	}

	@Test
	public void unionIsPositive() {
		String src = "type MyUnion = I32 | Bool; let x : MyUnion = 100; x is I32";
		TestUtils.assertValid(src, "true");
	}

	@Test
	public void unionIsNegative() {
		String src = "type MyUnion = I32 | Bool; let x : MyUnion = true; x is I32";
		TestUtils.assertValid(src, "false");
	}
}
