package magma.feature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static magma.TestUtils.assertAllValidWithPrelude;

@Disabled("pointer feature not yet implemented")
public class PointerTest {
	@Test
	void basicPointerDereference() {
		// let x : I32 = 0; let y : *I32 = &x; let z : I32 = *y; z
		assertAllValidWithPrelude("let x : I32 = 0; let y : *I32 = &x; let z : I32 = *y; z", "", "0");
	}
}
