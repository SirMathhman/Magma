package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

public class BoolTest {
	@Test
	void trueTest() {
		assertValid("true", "", 1);
	}

	@Test
	void falseTest() {
		assertValid("false", "", 0);
	}

	@Test
	void letBoolTest() {
		assertValid("let x : Bool = true; x", "", 1);
	}

	@Test
	void letBoolWithImplicitType() {
		assertValid("let x = true; x", "", 1);
	}
}