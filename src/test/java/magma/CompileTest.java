package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class CompileTest {

	@Test
	void empty() {
		assertAllValidWithPrelude(this, "", "", "");
	}

	@Test
	void readInt() {
		assertAllValidWithPrelude(this, "readInt()", "10", "10");
	}

	@Test
	void undefined() {
		assertAllInvalid("readInt");
	}

	@Test
	void readIntTooManyArguments() {
		assertAllInvalidWithPrelude("readInt(1, 2)");
	}

	@Test
	void add() {
		assertAllValidWithPrelude(this, "readInt() + readInt()", "10\r\n20", "30");
	}

	@Test
	void subtract() {
		assertAllValidWithPrelude(this, "readInt() - readInt()", "10\r\n20", "-10");
	}

	@Test
	void multiply() {
		assertAllValidWithPrelude(this, "readInt() * readInt()", "10\r\n20", "200");
	}

	@Test
	void divide() {
		assertAllValidWithPrelude(this, "readInt() / readInt()", "20\r\n10", "2");
	}

	@Test
	void let() {
		assertAllValidWithPrelude(this, "let x : I32 = readInt(); x", "10", "10");
	}

	@Test
	void letWithImplicitType() {
		assertAllValidWithPrelude(this, "let x = readInt(); x", "10", "10");
	}

	@Test
	void letInvalidWithDuplicateName() {
		assertAllInvalidWithPrelude("let x : I32 = readInt(); let x : I32 = readInt();");
	}

	@Test
	void letInvalidWithMismatchedTypes() {
		assertAllInvalidWithPrelude("let x : I32 = readInt;");
	}

	@Test
	void functionType() {
		assertAllValidWithPrelude(this, "let func : () => I32 = readInt; func()", "100", "100");
	}

	@Test
	void assign() {
		assertAllValidWithPrelude(this, "let mut x = 5; x = readInt(); x", "10", "10");
	}

	@Test
	void assignInvalidWithoutMut() {
		assertAllInvalidWithPrelude("let x = 5; x = readInt(); x");
	}

	@Test
	void assignInvalidWhenLhsUndefined() {
		assertAllInvalidWithPrelude("let x = 5; y = readInt(); x");
	}

	@Test
	void assignInvalidTypeMismatch() {
		assertAllInvalidWithPrelude("let mut x = 5; x = readInt; x");
	}

	@Test
	void assignBetweenLet() {
		assertAllValidWithPrelude(this, "let mut x = 0; x = readInt(); let y = x; y", "100", "100");
	}

	@Test
	void assignInvalidWhenLhsIsFunction() {
		assertAllInvalidWithPrelude("readInt = 5;");
	}

	@Test
	void brokenInitialization() {
		assertAllValidWithPrelude(this, "let x : I32; x = readInt(); x", "100", "100");
	}

	@Test
	void brokenInitializationWithMut() {
		assertAllValidWithPrelude(this, "let mut x : I32; x = 10; x = readInt(); x", "100", "100");
	}

	@Test
	void brokenInitializationInvalidWithoutMut() {
		assertAllInvalidWithPrelude("let x : I32; x = 10; x = readInt(); x");
	}

	@Test
	void letInvalidWhenNotInitialized() {
		assertAllInvalidWithPrelude("let x : I32;");
	}

	@Test
	void letMultiple() {
		assertAllValidWithPrelude(this, "let mut x = 0; let mut y = 1; x = readInt(); y = readInt(); x + y", "100\r\n200",
															"300");
	}

	@Test
	void trueTest() {
		assertAllValid(this, "true", "", "true");
	}

	@Test
	void falseTest() {
		assertAllValid(this, "false", "", "false");
	}

	@Test
	void letHasBoolType() {
		assertAllValidWithPrelude(this, "let x : Bool = true; x", "", "true");
	}

	@Test
	void equalsTrueTest() {
		assertAllValidWithPrelude(this, "readInt() == readInt()", "100\r\n100", "true");
	}

	@Test
	void equalsFalseTest() {
		assertAllValidWithPrelude(this, "readInt() == readInt()", "100\r\n200", "false");
	}

	@Test
	void equalsInvalidMismatchedTypes() {
		assertAllInvalidWithPrelude("5 == readInt");
	}

	@Test
	void lessThan() {
		assertAllValidWithPrelude(this, "readInt() < readInt()", "100\r\n200", "true");
	}

	@Test
	void ifTrue() {
		assertAllValidWithPrelude(this, "if (readInt() == 100) 3 else 4", "100", "3");
	}

	@Test
	void ifFalse() {
		assertAllValidWithPrelude(this, "if (readInt() == 100) 3 else 4", "200", "4");
	}

	@Test
	void ifInvalidWhenConditionNotBool() {
		assertAllInvalidWithPrelude("if (5) 3 else 4");
	}

	@Test
	void ifStatement() {
		assertAllValidWithPrelude(this, "let x : I32; if (readInt() == 100) x = 10; else x = 20; x", "100", "10");
	}

	@Test
	void letInitWithIf() {
		assertAllValidWithPrelude(this, "let x : I32 = if (readInt() == 100) 3 else 4; x", "100", "3");
	}

	@Test
	void braces() {
		assertAllValidWithPrelude(this, "{readInt()}", "100", "100");
	}

	@Test
	void bracesContainsLet() {
		assertAllValidWithPrelude(this, "{let x = readInt(); x}", "100", "100");
	}

	@Test
	void bracesCanAccessLetBefore() {
		assertAllValidWithPrelude(this, "let x = readInt(); {x}", "100", "100");
	}

	@Test
	void bracesDoNotLeakDeclarations() {
		assertAllInvalidWithPrelude("{let x = readInt();} x");
	}

	@Test
	void bracesRhsLet() {
		assertAllValidWithPrelude(this, "let x = {readInt()}; x", "100", "100");
	}

	@Test
	void postIncrement() {
		assertAllValidWithPrelude(this, "let mut x = readInt(); x++; x", "0", "1");
	}

	@Test
	void postIncrementInvalidWithoutMut() {
		assertAllInvalidWithPrelude("let x = readInt(); x++; x");
	}

	@Test
	void postIncrementMustBeNumeric() {
		assertAllInvalidWithPrelude("let mut x = readInt; x++;");
	}

	@Test
	void addAssign() {
		assertAllValidWithPrelude(this, "let mut x = readInt(); x += 5; x", "0", "5");
	}

	@Test
	void addAssignInvalidWhenNotMutable() {
		assertAllInvalidWithPrelude("let x = readInt(); x += 5; x");
	}

	@Test
	void addAssignInvalidWhenNotNumber() {
		assertAllInvalidWithPrelude("let mut x = readInt; x += 5; x");
	}

	@Test
	void whileTest() {
		assertAllValidWithPrelude(this,
															"let mut sum = 0; let mut counter = 0; let amount = readInt(); while (counter < amount) { sum += counter; counter++; } sum",
															"10", "45");
	}

	@Test
	void functionTest() {
		assertAllValidWithPrelude(this, "fn get() => readInt(); get()", "100", "100");
	}

	@Test
	void twoFunctions() {
		assertAllValidWithPrelude(this, "fn get() => readInt(); fn get2() => get(); get2()", "100", "100");
	}

	@Test
	void functionsInvalidWithDuplicateNames() {
		assertAllInvalidWithPrelude("fn get() => 1; fn get() => 2; get()");
	}

	@Test
	void functionWithOneParameter() {
		assertAllValidWithPrelude(this, "fn get(x : I32) => x; get(100)", "100", "100");
	}

	@Test
	void functionWithTwoParameters() {
		assertAllValidWithPrelude(this, "fn get(x : I32, y : I32) => x + y; get(100, 200)", "100\r\n200", "300");
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
		assertAllValidWithPrelude(this, "fn get() : I32 => readInt(); get()", "100", "100");
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
		assertAllValidWithPrelude(this, "fn get() => { readInt() } get()", "100", "100");
	}

	@Test
	void functionHasLetStatementInBody() {
		assertAllValidWithPrelude(this, "fn get() => { let x = readInt(); x } get()", "100", "100");
	}

	@Test
	void structWithOneField() {
		assertAllValidWithPrelude(this, "struct Point { x : I32 } let p = Point { readInt() }; p.x", "100", "100");
	}

	@Test
	void structWithTwoFields() {
		assertAllValidWithPrelude(this,
															"struct Point { x : I32, y : I32 } let p = Point { readInt(), readInt() }; p.x + p.y",
															"100\r\n200", "300");
	}

	@Test
	void enumTest() {
		assertAllValidWithPrelude(this, "enum State { Valid } let s = State.Valid; s == State.Valid", "", "true");
	}

	@Test
	void thisContainsLocalDeclaration() {
		assertAllValidWithPrelude(this, "fn get() => { let x = readInt(); this } get().x", "100", "100");
	}

	@Test
	void thisContainsParameter() {
		assertAllValidWithPrelude(this, "fn get(x : I32) => { this } get(readInt()).x", "100", "100");
	}

	@Test
	void global() {
		assertAllValidWithPrelude(this, "let mut x = 0; fn inc() => x += readInt(); inc(); x", "10", "10");
	}

	@Test
	void functionWithinFunction() {
		assertAllValidWithPrelude(this, "fn outer() => { fn inner() => readInt(); inner() }; outer()", "100", "100");
	}

	@Test
	void thisContainsFunction() {
		assertAllValidWithPrelude(this, "fn get() => { fn inner() => readInt(); this } get().inner()", "100", "100");
	}

	@Test
	void thisReturnedByInnerFunction() {
		assertAllValidWithPrelude(this, "fn get() => { let value = readInt(); fn inner() => this; inner() }; get().value",
															"100", "100");
	}
}
