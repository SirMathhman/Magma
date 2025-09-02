package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class ConditionalTest {
	@Test
	void ifTrue() {
		assertAllValidWithPrelude("if (readInt() == 100) 3 else 4", "100", "3");
	}

	@Test
	void ifFalse() {
		assertAllValidWithPrelude("if (readInt() == 100) 3 else 4", "200", "4");
	}

	@Test
	void ifInvalidWhenConditionNotBool() {
		assertAllInvalidWithPrelude("if (5) 3 else 4");
	}

	@Test
	void ifStatement() {
		assertAllValidWithPrelude("let x : I32; if (readInt() == 100) x = 10; else x = 20; x", "100", "10");
	}

	@Test
	void letInitWithIf() {
		assertAllValidWithPrelude("let x : I32 = if (readInt() == 100) 3 else 4; x", "100", "3");
	}

	@Test
	void whileTest() {
		assertAllValidWithPrelude(
				"let mut sum = 0; let mut counter = 0; let amount = readInt(); while (counter < amount) { sum += counter; counter++; } sum",
				"10", "45");
	}

}
