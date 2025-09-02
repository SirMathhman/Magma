package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class BoolOperatorTest {
	@Test
	void equalsTrueTest() {
		assertAllValidWithPrelude("readInt() == readInt()", "100\r\n100", "true");
	}

	@Test
	void equalsFalseTest() {
		assertAllValidWithPrelude("readInt() == readInt()", "100\r\n200", "false");
	}

	@Test
	void equalsInvalidMismatchedTypes() {
		assertAllInvalidWithPrelude("5 == readInt");
	}

	@Test
	void lessThan() {
		assertAllValidWithPrelude("readInt() < readInt()", "100\r\n200", "true");
	}
}
