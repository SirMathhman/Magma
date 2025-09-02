package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class CompactTest {
	@Test
	void postIncrement() {
		assertAllValidWithPrelude("let mut x = readInt(); x++; x", "0", "1");
	}

	@Test
	void postIncrementInvalidWithoutMut() {
		assertAllInvalidWithPrelude("let x = readInt(); x++; x");
	}

	@Test
	void postIncrementMustBeNumeric() {
		assertAllInvalidWithPrelude("let mut x = readInt; x++;");
	}

	@Test
	void addAssign() {
		assertAllValidWithPrelude("let mut x = readInt(); x += 5; x", "0", "5");
	}

	@Test
	void addAssignInvalidWhenNotMutable() {
		assertAllInvalidWithPrelude("let x = readInt(); x += 5; x");
	}

	@Test
	void addAssignInvalidWhenNotNumber() {
		assertAllInvalidWithPrelude("let mut x = readInt; x += 5; x");
	}
}
