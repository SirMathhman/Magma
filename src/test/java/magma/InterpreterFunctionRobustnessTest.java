package magma;

import magma.interpret.InterpretError;
import magma.interpret.Interpreter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterFunctionRobustnessTest {
	@Test
	void zeroArgFunctionReturnsLiteral() {
		assertValid("fn get() : I32 => 100; get()", "100");
	}

	@Test
	void genericFunctionWithParam() {
		// ensure generics in header don't break parsing
		assertValid("fn wrap<T>(x : T) => x; wrap(5)", "5");
	}

	@Test
	void functionCallingAnotherFunction() {
		assertValid("fn a() => 1; fn b() => a(); b()", "1");
	}

	@Test
	void classWithInnerFunctionCalledAsMethod() {
		assertValid("class fn Wrapper() => {fn get() => 42;} Wrapper().get()", "42");
	}

	@Test
	void functionWithParamAndCalledWithLiteral() {
		assertValid("fn a(x : I32) => x; fn b() => a(5); b()", "5");
	}

	// Invalid cases

	@Test
	void missingArgumentShouldBeInvalid() {
		assertInvalid("fn id(x : I32) => x; id()");
	}

	@Test
	void directRecursiveFunctionShouldBeInvalid() {
		// direct recursion detection
		assertInvalid("fn a() => CALL:a; a()");
	}

	@Test
	void mutualRecursionShouldBeInvalid() {
		// mutual recursion between two functions
		assertInvalid("fn a() => CALL:b; fn b() => CALL:a; a()");
	}

	@Test
	void declaredBoolReturnButNumericBodyShouldBeInvalid() {
		// return type Bool but body is numeric literal
		assertInvalid("fn f() : Bool => 1; f()");
	}

	@Test
	void duplicateFunctionDeclarationsShouldBeInvalidVariant() {
		// duplicate name with different signatures still considered duplicate
		assertInvalid("fn x() => 1; fn x(a : I32) => 2;");
	}

	private static void assertValid(String input, String expected) {
		switch (new Interpreter().interpret(input)) {
			case Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}

	private static void assertInvalid(String input) {
		switch (new Interpreter().interpret(input)) {
			case Err<String, InterpretError>(InterpretError error) -> assertNotNull(error);
			case Ok<String, InterpretError>(String value) -> fail("Expected error but got: " + value);
		}
	}
}
