package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class ClassImplTest {

	@Test
	void implOnClassAllowsMethods() {
		String prog = "class fn Point(x : I32, y : I32) : Point => { let z : I32; z = x + y; this }; impl Point { fn sum() : I32 => z; } Point(3, 4).sum()";
		assertValid(prog, "7");
	}

	@Test
	void implOnUnknownNameIsInvalid() {
		String prog = "impl Foo { fn a() : I32 => 1; } 0";
		assertInvalid(prog);
	}

	@Test
	void emptyImplOnClassIsInvalid() {
		String prog = "class fn C() : C => this; impl C { } 0";
		assertInvalid(prog);
	}
}
