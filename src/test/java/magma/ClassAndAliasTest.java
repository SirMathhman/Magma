package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class ClassAndAliasTest {
	@Test
	void classConstructorFieldAccess() {
		assertValid("class fn Wrapper(field : I32) => {} Wrapper(100).field", "100");
	}

	@Test
	void genericClassConstructorFieldAccess() {
		assertValid("class fn Wrapper<T>(field : T) => {} Wrapper(100).field", "100");
	}

	@Test
	void classDeclarationFollowedByLiteral() {
		String input = "class fn Interpreter() => {\n}" + "\n5";
		assertValid(input, "5");
	}

	@Test
	void classWithInnerFunctionCall() {
		assertValid("class fn Empty() => {fn get() => 100;} Empty().get()", "100");
	}

	@Test
	void multipleClassesWithInnerFunctions() {
		String program = "class fn A() => {fn get() => 1;} class fn B() => {fn get() => 2;} A().get() + B().get()";
		assertValid(program, "3");
	}

	@Test
	void typeAliasAndLetWithAlias() {
		assertValid("type Temp = I32; let x : Temp = 100; x", "100");
	}
}
