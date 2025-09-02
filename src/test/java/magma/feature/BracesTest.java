package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class BracesTest {
	@Test
	void braces() {
		assertAllValidWithPrelude("{readInt()}", "100", "100");
	}

	@Test
	void bracesContainsLet() {
		assertAllValidWithPrelude("{let x = readInt(); x}", "100", "100");
	}

	@Test
	void bracesCanAccessLetBefore() {
		assertAllValidWithPrelude("let x = readInt(); {x}", "100", "100");
	}

	@Test
	void bracesDoNotLeakDeclarations() {
		assertAllInvalidWithPrelude("{let x = readInt();} x");
	}

	@Test
	void bracesRhsLet() {
		assertAllValidWithPrelude("let x = {readInt()}; x", "100", "100");
	}
}
