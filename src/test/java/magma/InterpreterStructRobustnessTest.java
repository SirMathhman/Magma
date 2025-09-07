package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class InterpreterStructRobustnessTest {
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
		// structs use brace-style constructor; paren style should be rejected
		assertInvalid("struct Empty { field : I32 } Empty().field");
	}

	@Test
	void nestedStructAndLetAccess() {
		// valid: let-bound struct access inside block
		assertValid("struct S { f : I32 } let x = S { 7 }.f; x", "7");
	}

	@Test
	void structFollowedByExpressionWithoutSemicolon() {
		// ensure declarations followed by expression without semicolon are handled
		assertValid("struct Wrapper { field : I32 } Wrapper { 2 }.field", "2");
	}

	// helpers delegated to TestHelpers
}
