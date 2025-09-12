package magma;

import org.junit.jupiter.api.Test;

public class BorrowCheckTest {
	@Test
	public void doubleMutInvalid() {
		TestUtils.assertInvalid("let mut x = 0; let y = &mut x; let z = &mut x;");
	}

	@Test
	public void singleMutValid() {
		TestUtils.assertValid("let mut x = 0; let y = &mut x; *y = 1; x", "1");
	}

	@Test
	public void fnMutThenTakeInvalid() {
		TestUtils.assertInvalid("let mut x = 0; fn doSomething() => { x += 1; x }; let z = &mut x;");
	}
}
