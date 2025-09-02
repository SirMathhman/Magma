package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class BasicTest {
	@Test
	void empty() {
		assertAllValidWithPrelude("", "", "");
	}

	@Test
	void readInt() {
		assertAllValidWithPrelude("readInt()", "10", "10");
	}

	@Test
	void undefined() {
		assertAllInvalid("readInt");
	}

	@Test
	void readIntTooManyArguments() {
		assertAllInvalidWithPrelude("readInt(1, 2)");
	}
}
