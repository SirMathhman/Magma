package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class InterpreterTest {
	@Test
	void empty() {
		assertValid("", "");
	}

	@Test
	void undefined() {
		assertInvalid("test");
	}

	@Test
	void numberLiteral() {
		assertValid("5", "5");
	}

	@Test
	void numberLiteralWithTrailing() {
		assertValid("5I32", "5");
	}

	@Test
	void additionSimple() {
		assertValid("2 + 3", "5");
	}

	@Test
	void additionWithTrailingType() {
		assertValid("2 + 3I32", "5");
	}

	@Test
	void subtractionSimple() {
		assertValid("5 - 3", "2");
	}

	@Test
	void multiplicationSimple() {
		assertValid("5 * 3", "15");
	}

	@Test
	void chainedAddition() {
		assertValid("1 + 2 + 3", "6");
	}

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
	void additionMixedUnsignedAndSigned() {
		assertInvalid("2U8 + 3I32");
	}

	@Test
	void mixedSuffixesInChain() {
		assertInvalid("0I8 + 0 + 0U32");
	}

	@Test
	void booleanLiteral() {
		assertValid("true", "true");
	}

	@Test
	void letBoolAssignedNumberShouldBeInvalid() {
		assertInvalid("let x : Bool = 0;");
	}

	@Test
	void functionDeclarationAndCallSimple() {
		assertValid("fn get() : I32 => 100; get()", "100");
	}

	@Test
	void functionDeclarationWithoutTypeAndCall() {
		assertValid("fn get() => 100; get()", "100");
	}

	@Test
	void functionWithParamAndCall() {
		assertValid("fn get(param : I32) => param; get(100)", "100");
	}

	@Test
	void duplicateFunctionDeclarationShouldBeInvalid() {
		assertInvalid("fn get() => 0; fn get() => 0;");
	}

	@Test
	void functionCallOtherFunctionWithLetsMixed() {
		assertValid("fn get() => 100; let x = 0; fn next() => get(); next();", "100");
	}

	@Test
	void blockLiteralSimple() {
		assertValid("{5}", "5");
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
	void ifExpressionSimple() {
		assertValid("if (true) 3 else 5", "3");
	}

	@Test
	void ifExpressionWithBlockBranches() {
		assertValid("if (true) {3} else {5}", "3");
	}

	@Test
	void letIfAsRhs() {
		assertValid("let x = if (true) {3} else {5}; x", "3");
	}

	@Test
	void bareAssignmentIsInvalid() {
		assertInvalid("x = 10;");
	}

	@Test
	void assignToImmutableShouldBeInvalid() {
		assertInvalid("let x = 0; x = 100; x");
	}

	@Test
	void classConstructorFieldAccess() {
		assertValid("class fn Wrapper(field : I32) => {} Wrapper(100).field", "100");
	}

	@Test
	void genericClassConstructorFieldAccess() {
		assertValid("class fn Wrapper<T>(field : T) => {} Wrapper(100).field", "100");
	}

	@Test
	void structLiteralConstructorFieldAccess() {
		assertValid("struct Wrapper { field : I32 } Wrapper { 100 }.field", "100");
	}

	@Test
	void classDeclarationFollowedByLiteral() {
		// ensure a leading class declaration with empty body followed by a
		// top-level expression without a separating semicolon is handled.
		String input = "class fn Interpreter() => {\n}" + "\n5";
		assertValid(input, "5");
	}

	@Test
	void classWithInnerFunctionCall() {
		// ensure a class with inner function declaration can be called
		assertValid("class fn Empty() => {fn get() => 100;} Empty().get()", "100");
	}

	@Test
	void multipleClassesWithInnerFunctions() {
		// declare two classes each with an inner function and use both in one
		// expression
		String program = "class fn A() => {fn get() => 1;} class fn B() => {fn get() => 2;} A().get() + B().get()";
		assertValid(program, "3");
	}

	@Test
	void typeAliasAndLetWithAlias() {
		// ensure a simple type alias can be used in let declarations
		assertValid("type Temp = I32; let x : Temp = 100; x", "100");
	}

	// helpers delegated to TestHelpers
}
