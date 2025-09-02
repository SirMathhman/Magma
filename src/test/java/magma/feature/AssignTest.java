package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class AssignTest {
	@Test
	void assign() {
		assertAllValidWithPrelude("let mut x = 5; x = readInt(); x", "10", "10");
	}

	@Test
	void assignInvalidWithoutMut() {
		assertAllInvalidWithPrelude("let x = 5; x = readInt(); x");
	}

	@Test
	void assignInvalidWhenLhsUndefined() {
		assertAllInvalidWithPrelude("let x = 5; y = readInt(); x");
	}

	@Test
	void assignInvalidTypeMismatch() {
		assertAllInvalidWithPrelude("let mut x = 5; x = readInt; x");
	}

	@Test
	void assignBetweenLet() {
		assertAllValidWithPrelude("let mut x = 0; x = readInt(); let y = x; y", "100", "100");
	}

	@Test
	void assignInvalidWhenLhsIsFunction() {
		assertAllInvalidWithPrelude("readInt = 5;");
	}
}
