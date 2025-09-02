package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class UnionTest {
	@Test
	void alias() {
		assertAllValidWithPrelude("type Simple = I32; let value : Simple = readInt(); value", "10", "10");
	}

	@Test
	void isOperator() {
		assertAllValidWithPrelude("type Simple = I32; let value : Simple = readInt(); value is I32", "10", "true");
	}

	@Test
	void twoTypesTrue() {
		assertAllValidWithPrelude("type Simple = I32 | Bool; let value : Simple = 100; value is I32", "", "true");
	}
	
	@Test
	void twoTypesFalse() {
		assertAllValidWithPrelude("type Simple = I32 | Bool; let value : Simple = 100; value is Bool", "", "false");
	}
}
