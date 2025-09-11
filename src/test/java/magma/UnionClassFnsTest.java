package magma;

import org.junit.jupiter.api.Test;

public class UnionClassFnsTest {

	@Test
	public void unionClassFnsIsCheck() {
		TestUtils.assertValid("class fn Ok() => Ok { } class fn Err() => Err { } type Result = Ok | Err; let a : Result = Ok(); a is Ok", "true");
		TestUtils.assertValid("class fn Ok() => Ok { } class fn Err() => Err { } type Result = Ok | Err; let b : Result = Err(); b is Err", "true");
	}
}
