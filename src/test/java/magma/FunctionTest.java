package magma;

import org.junit.jupiter.api.Test;

import static magma.TestHelpers.*;

public class FunctionTest {
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
	void zeroArgFunctionReturnsLiteral() {
		assertValid("fn get() : I32 => 100; get()", "100");
	}

	@Test
	void genericFunctionWithParam() {
		assertValid("fn wrap<T>(x : T) => x; wrap(5)", "5");
	}

	@Test
	void functionCallingAnotherFunction() {
		assertValid("fn a() => 1; fn b() => a(); b()", "1");
	}

	@Test
	void missingArgumentShouldBeInvalid() {
		assertInvalid("fn id(x : I32) => x; id()");
	}

	@Test
	void directRecursiveFunctionShouldBeInvalid() {
		assertInvalid("fn a() => CALL:a; a()");
	}

	@Test
	void mutualRecursionShouldBeInvalid() {
		assertInvalid("fn a() => CALL:b; fn b() => CALL:a; a()");
	}

	@Test
	void declaredBoolReturnButNumericBodyShouldBeInvalid() {
		assertInvalid("fn f() : Bool => 1; f()");
	}

	@Test
	void duplicateFunctionDeclarationsShouldBeInvalidVariant() {
		assertInvalid("fn x() => 1; fn x(a : I32) => 2;");
	}
}
