package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValid;
import static magma.TestUtils.assertAllValidWithPrelude;

public class BoolTest {
	@Test
	void trueTest() {
		assertAllValid("true", "", "true");
	}

	@Test
	void falseTest() {
		assertAllValid("false", "", "false");
	}

	@Test
	void letHasBoolType() {
		assertAllValidWithPrelude("let x : Bool = true; x", "", "true");
	}
}
