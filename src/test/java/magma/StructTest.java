package magma;

import org.junit.jupiter.api.Test;

public class StructTest {
	@Test
	public void structFieldAccess() {
		TestUtils.assertValid("struct Wrapper { field : I32 } Wrapper { 100 }.field", "100");
	}

	@Test
	public void structLetFieldAccess() {
		TestUtils.assertValid("struct Wrapper { field : I32 } let value = Wrapper { 100 }; value.field", "100");
	}

	@Test
	public void dupStructDeclInvalid() {
		TestUtils.assertInvalid("struct Duplicate { field : I32 } struct Duplicate { field : I32 }");
	}

	@Test
	public void dupFieldInvalid() {
		TestUtils.assertInvalid("struct Point { x : I32, x : I32 }");
	}

	@Test
	public void structMissingFields() {
		TestUtils.assertInvalid("struct Point { x : I32, y : I32 } let test = Point {}; ");
	}
}
