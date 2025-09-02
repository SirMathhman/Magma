package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class EnumTest {
	@Test
	void enumTest() {
		assertAllValidWithPrelude("enum State { Valid } let s = State.Valid; s == State.Valid", "", "true");
	}
}
