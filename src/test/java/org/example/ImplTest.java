package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ImplTest {

	@Test
	void implAfterStructAllowsMethods() {
		String prog = "let x : I32; x = 1; struct Empty { }; impl Empty { fn id(a : I32) : I32 => a; } let value = Empty {}; value.id(5)";
		assertValid(prog, "5");
	}

	@Test
	void implWithMultipleFnsWorks() {
		String prog = "let x : I32; x = 1; struct S { }; impl S { fn one() : I32 => 1; fn two() : I32 => 2; } let value = S {}; value.two()";
		assertValid(prog, "2");
	}

	@Test
	void implOptionalSemicolonAfterBlockIsAllowed() {
		String prog = "let x : I32; x = 1; struct A { }; impl A { fn get() : I32 => 7; }; let value = A {}; value.get()";
		assertValid(prog, "7");
	}

	@Test
	void implUnknownStructIsInvalid() {
		assertInvalid("let x : I32; x = 0; impl Unknown { fn f() : I32 => 1; } x");
	}

	@Test
	void implWithoutFnsIsInvalid() {
		assertInvalid("let x : I32; x = 0; struct E { }; impl E { } x");
	}
}
