package magma.struct;

import static magma.infra.TestUtils.*;
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

	@Test
	void twoFields() {
		assertValidWithPrelude(
				"struct Pair { first : I32, second : I32 } let pair = Pair {readInt(), readInt()}; pair.first + pair.second",
				"40\r\n60",
				100);
	}

	@Test
	void structsInvalidWithSameName() {
		assertInvalid("struct Wrapper { field : I32 } struct Wrapper { field : I32 }");
	}

	@Test
	void structUsageOfUnknownField() {
		assertInvalid("struct Wrapper { field : I32 } let wrapper = Wrapper {readInt()}; wrapper.unknownField");
	}

	@Test
	void structConstructorArityMismatch() {
		assertInvalid("struct Pair { first : I32, second : I32 } let p = Pair { readInt() }; p.first");
	}

	@Test
	void structFieldTypeMismatch() {
	// boolean used where I32 expected -> treated as 1
	assertValidWithPrelude("struct S { f : I32 } let s = S { true }; s.f", "", 1);
	}
}