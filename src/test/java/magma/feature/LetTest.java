package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class LetTest {
	@Test
	void let() {
		assertAllValidWithPrelude("let x : I32 = readInt(); x", "10", "10");
	}

	@Test
	void letWithImplicitType() {
		assertAllValidWithPrelude("let x = readInt(); x", "10", "10");
	}

	@Test
	void letInvalidWithDuplicateName() {
		assertAllInvalidWithPrelude("let x : I32 = readInt(); let x : I32 = readInt();");
	}

	@Test
	void letInvalidWithMismatchedTypes() {
		assertAllInvalidWithPrelude("let x : I32 = readInt;");
	}

	@Test
	void functionType() {
		assertAllValidWithPrelude("let func : () => I32 = readInt; func()", "100", "100");
	}

	@Test
	void brokenInitialization() {
		assertAllValidWithPrelude("let x : I32; x = readInt(); x", "100", "100");
	}

	@Test
	void brokenInitializationWithMut() {
		assertAllValidWithPrelude("let mut x : I32; x = 10; x = readInt(); x", "100", "100");
	}

	@Test
	void brokenInitializationInvalidWithoutMut() {
		assertAllInvalidWithPrelude("let x : I32; x = 10; x = readInt(); x");
	}

	@Test
	void letInvalidWhenNotInitialized() {
		assertAllInvalidWithPrelude("let x : I32;");
	}

	@Test
	void letMultiple() {
		assertAllValidWithPrelude("let mut x = 0; let mut y = 1; x = readInt(); y = readInt(); x + y", "100\r\n200",
															"300");
	}
}
