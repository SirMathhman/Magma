package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalid;
import static magma.TestUtils.assertAllValidWithPrelude;

public class StructTest {
	@Test
	void structWithOneField() {
		assertAllValidWithPrelude("struct Point { x : I32 } let p = Point { readInt() }; p.x", "100", "100");
	}

	@Test
	void structWithTwoFields() {
		assertAllValidWithPrelude("struct Point { x : I32, y : I32 } let p = Point { readInt(), readInt() }; p.x + p.y",
				"100\r\n200", "300");
	}

	@Test
	void twoStructDefinitions() {
		assertAllValidWithPrelude(
				"struct Point { x : I32 } struct Circle { r : I32 } let p = Point { readInt() }; let c = Circle { readInt() }; p.x + c.r",
				"100\r\n200", "300");
	}

	@Test
	void twoStructsInvalidWhenSameName() {
		assertAllInvalid("struct Point { x : I32 } struct Point { y : I32 }");
	}

	@Test
	void structDuplicateMemberName() {
		assertAllInvalid("struct Point { x : I32, x : I32 }");
	}

	@Test
	void structInvalidEmpty() {
		assertAllInvalid("struct Point { }");
	}

	@Test
	void constructionWithInsufficientArgument() {
		assertAllInvalid("struct Point { x : I32 } let p = Point { }; p.x");
	}

	@Test
	void constructionWithMismatchedArgumentType() {
		assertAllInvalid("struct Point { x : I32 } let p = Point { true }; p.x");
	}

	@Test
	void structuresImmutable() {
		assertAllInvalid("struct Point { x : I32 } let p = Point { readInt() }; p.x = 100;");
	}

	@Test
	void structLetStructSequence() {
		// struct, then let, then another struct — ensure parser accepts this order
		assertAllValidWithPrelude(
				"struct One { a : I32 } let x : I32 = readInt(); struct Two { b : I32 } let t = Two { readInt() }; x + t.b",
				"10\r\n20",
				"30");
	}
}
