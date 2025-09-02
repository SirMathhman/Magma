package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalidWithPrelude;
import static magma.TestUtils.assertAllValidWithPrelude;

public class FunctionTest {
	@Test
	void functionTest() {
		assertAllValidWithPrelude("fn get() => readInt(); get()", "100", "100");
	}

	@Test
	void twoFunctions() {
		assertAllValidWithPrelude("fn get() => readInt(); fn get2() => get(); get2()", "100", "100");
	}

	@Test
	void functionsInvalidWithDuplicateNames() {
		assertAllInvalidWithPrelude("fn get() => 1; fn get() => 2; get()");
	}

	@Test
	void functionWithOneParameter() {
		assertAllValidWithPrelude("fn get(x : I32) => x; get(100)", "100", "100");
	}

	@Test
	void functionWithTwoParameters() {
		assertAllValidWithPrelude("fn get(x : I32, y : I32) => x + y; get(100, 200)", "100\r\n200", "300");
	}

	@Test
	void functionCallMissingArguments() {
		assertAllInvalidWithPrelude("fn get(x : I32) => x; get()");
	}

	@Test
	void functionCallInvalidOnNonFunction() {
		assertAllInvalidWithPrelude("fn get(x : I32) => x; 5()");
	}

	@Test
	void functionCallInvalidMismatchedArgumentType() {
		assertAllInvalidWithPrelude("fn get(x : I32) => x; get(true)");
	}

	@Test
	void functionWithExplicitReturnType() {
		assertAllValidWithPrelude("fn get() : I32 => readInt(); get()", "100", "100");
	}

	@Test
	void functionInvalidWhenTwoParamsWithSameName() {
		assertAllInvalidWithPrelude("fn get(x : I32, x : I32) => x; get(100, 200)");
	}

	@Test
	void functionReturnTypeMismatch() {
		assertAllInvalidWithPrelude("fn get() : I32 => 5; let x : Bool = get();");
	}

	@Test
	void functionHasBraces() {
		assertAllValidWithPrelude("fn get() => { readInt() } get()", "100", "100");
	}

	@Test
	void functionHasLetStatementInBody() {
		assertAllValidWithPrelude("fn get() => { let x = readInt(); x } get()", "100", "100");
	}
}
