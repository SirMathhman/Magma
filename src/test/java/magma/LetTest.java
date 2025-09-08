package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class LetTest {
	@Test
	void letBinding() {
		assertValid("let x : I32 = 10; x", "10");
	}

	@Test
	void letBindingNoType() {
		assertValid("let x = 10; x", "10");
	}

	@Test
	void letChain() {
		assertValid("let x = 10; let y = x; y", "10");
	}

	@Test
	void letTwoBindings() {
		assertValid("let x = 10; let y = 40; x", "10");
	}

	@Test
	void letOnlyStatement() {
		assertValid("let x = 10;", "");
	}

	@Test
	void letDuplicateBinding() {
		assertInvalid("let x = 0; let x = 0;");
	}

	@Test
	void letTypeMismatch() {
		assertInvalid("let x : U8 = 10I32;");
	}

	@Test
	void letIdentifierTypeMismatch() {
		assertInvalid("let x : I32 = 10; let y : U8 = x;");
	}

	@Test
	void letBlockRhs() {
		assertValid("let x = {5}; x", "5");
	}

	@Test
	void letBlockWithInnerLet() {
		assertValid("let x = {let y = 5; y}; x", "5");
	}

	@Test
	void letBindingWithBlockExpression() {
		assertValid("let x = 10; {x}", "10");
	}

	@Test
	void blockLetNotVisibleOutside() {
		assertInvalid("{let x = 0;} x");
	}

	@Test
	void letBindConstructorLiteralThenAccessField() {
		assertValid("struct Empty { field : I32 } let empty = Empty {100}; empty.field", "100");
	}
}
