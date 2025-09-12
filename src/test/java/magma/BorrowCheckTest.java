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
}
