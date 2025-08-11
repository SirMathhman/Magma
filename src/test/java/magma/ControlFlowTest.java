package magma;

import org.junit.jupiter.api.Test;

class ControlFlowTest extends CompilerTestBase {
	@Test
	void braces() {
		assertValid("{}", "{}");
	}

	@Test
	void statementAndBraces() {
		assertValid("let x = 100; {}", "int32_t x = 100; {}");
	}

	@Test
	void statementInBraces() {
		assertValid("{let x = 100;}", "{int32_t x = 100;}");
	}

	@Test
	void testIf() {
		assertValid("if(true){let x = 100;}", "if(true){int32_t x = 100;}");
	}

	@Test
	void testElse() {
		assertValid("if(true){let x = 100;} else {}", "if(true){int32_t x = 100;} else {}");
	}

	@Test
	void testWhile() {
		assertValid("while(true){let x = 100;}", "while(true){int32_t x = 100;}");
	}
}