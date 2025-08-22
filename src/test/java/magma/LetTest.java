package magma;// helper imports moved to TestUtils

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValidWithPrelude;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LetTest {
	@Test
	void let() {
		assertValidWithPrelude("let x = readInt(); x", "100", 100);
	}

	@Test
	void letWithExplicitType() {
		assertValidWithPrelude("let x: I32 = readInt(); x", "100", 100);
	}

	@Test
	void letMultiple() {
		assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "100\r\n200", 300);
	}

	@Test
	void letWithMut() {
		assertValidWithPrelude("let mut x = 0; x = readInt(); x", "100", 100);
	}

	@Test
	void letWithoutMut() {
		assertInvalid("let x = 0; x = 1;");
	}

	@Test
	void letAssignLet() {
		assertValidWithPrelude("let x = 0; x = readInt(); let y = x; y", "100", 100);
	}
}
