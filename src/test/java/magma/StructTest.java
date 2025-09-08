package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class StructTest {
	@Test
	void structLiteralFieldAccessSimple() {
		assertValid("struct Wrapper { field : I32 } Wrapper { 100 }.field", "100");
	}

	@Test
	void structLiteralFieldAccessWithSpaces() {
		assertValid("struct Wrapper { field : I32 } Wrapper { 100 } . field", "100");
	}

	@Test
	void duplicateStructDeclarationShouldBeInvalid() {
		assertInvalid("struct A { f : I32 } struct A { f : I32 }");
	}

	@Test
	void missingFieldAccessShouldBeInvalid() {
		assertInvalid("struct Wrapper { field : I32 } Wrapper { 100 }.missing");
	}

	@Test
	void structUsedWithParenConstructorShouldBeInvalid() {
		assertInvalid("struct Empty { field : I32 } Empty().field");
	}

	@Test
	void nestedStructAndLetAccess() {
		assertValid("struct S { f : I32 } let x = S { 7 }.f; x", "7");
	}

	@Test
	void structFollowedByExpressionWithoutSemicolon() {
		assertValid("struct Wrapper { field : I32 } Wrapper { 2 }.field", "2");
	}

	@Test
	void letBindConstructorLiteralWithSpacesThenAccess() {
		assertValid("struct Empty { field : I32 } let empty = Empty { 100 }; empty.field", "100");
	}
}
