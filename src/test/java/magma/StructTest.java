package magma;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValidWithPrelude;

import org.junit.jupiter.api.Test;

public class StructTest {
	@Test
	void empty() {
		assertInvalid("struct Empty {}");
	}

	@Test
	void oneField() {
		assertValidWithPrelude("struct Wrapper { field : I32 } let wrapper = Wrapper {readInt()}; wrapper.field", "100",
				100);
	}
}