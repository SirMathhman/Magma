package org.example;

import org.junit.jupiter.api.Test;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

public class CommentsTest {
	@Test
	public void standaloneLineCommentShouldBeIgnored() {
		assertValid("// just a comment\n10", "10");
	}

	@Test
	public void endOfLineCommentAfterStatement() {
		assertValid("let x = 5; // set x\nx", "5");
	}

	@Test
	public void commentAfterClosingBrace() {
		String src = "{ let x = 2; } // end block\n10";
		assertValid(src, "10");
	}

	@Test
	public void commentsInsideFunctionBody() {
		String program = String.join("\n",
				"fn add(a, b) => {",
				"  // early note",
				"  let mut c = a; // set c",
				"  c = c + b;",
				"  return c; // done",
				"}",
				"add(2, 3)");
		assertValid(program, "5");
	}

	@Test
	public void commentOnlyProgramIsInvalid() {
		assertInvalid("// only comment");
	}
}
