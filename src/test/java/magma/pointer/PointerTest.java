package magma.pointer;

import static magma.infra.TestUtils.assertInvalid;
import static magma.infra.TestUtils.assertValidWithPrelude;

import org.junit.jupiter.api.Test;

public class PointerTest {
	@Test
	void referenceAndDereference() {
		assertValidWithPrelude("let x : I32 = readInt(); let y : *I32 = &x; let z : I32 = *y; z", "42", 42);
	}

	@Test
	void mutable() {
		assertValidWithPrelude("let mut x = 0; let y : mut *I32 = &mut x; *y = readInt(); *x", "100", 100);
	}

	@Test
	void immutable() {
		assertInvalid("let x = 0; let y = &mut x;");
	}
}