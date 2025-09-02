package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class UnionTest {
	@Test
	void simple() {
		assertAllValidWithPrelude("type Simple = I32; let value : Simple = readInt(); value", "10", "10");
	}
}
