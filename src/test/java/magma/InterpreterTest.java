package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class InterpreterTest {
	@Test
	void valid() {
		assertValid("5", "5");
	}

	@Test
	void invalid() {
		assertInvalid("test");
	}

	@ParameterizedTest
	@ValueSource(strings = { "U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64" })
	void intWithSuffix(String suffix) {
		assertValid("5" + suffix, "5");
	}

	@Test
	void add() {
		assertValid("3 + 5", "8");
	}

	@Test
	void subtract() {
		assertValid("5 - 3", "2");
	}

	@Test
	void multiply() {
		assertValid("3 * 5", "15");
	}

	@Test
	void division() {
		assertValid("10 / 2", "5");
	}

	@Test
	void modulo() {
		assertValid("10 % 3", "1");
	}

	@Test
	void let() {
		assertValid("let x = 10; x", "10");
	}

	@Test
	void letWithExplicitType() {
		assertValid("let x : I32 = 10; x", "10");
	}

	@ParameterizedTest
	@ValueSource(strings = { "U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64" })
	void letWithExplicitTypeAndSuffix(String suffix) {
		assertValid("let x : " + suffix + " = 10" + suffix + "; x", "10");
	}

	@Test
	void letInvalidWithMismatchedTypes() {
		assertInvalid("let x : I32 = 10U8;");
	}

	@Test
	void letInvalidWhenInitializedWithUndefined() {
		assertInvalid("let x : I32 = test;");
	}

	@Test
	void assign() {
		assertValid("let mut x = 0; x = 20; x", "20");
	}

	@Test
	void assignInvalidWithoutMut() {
		assertInvalid("let x = 0; x = 20;");
	}

	@Test
	void assignInvalidMismatchedTypes() {
		assertInvalid("let mut x : I32 = 0; x = 20U8;");
	}

	@Test
	void braces() {
		assertValid("{5}", "5");
	}

	@Test
	void letStatementBeforeBrace() {
		assertValid("let x = 10; { x }", "10");
	}

	@Test
	void letStatementDoesNotLeak() {
		assertInvalid("{ let x = 20; } x");
	}

	@Test
	void letStatementIsPreserved() {
		assertValid("let x = 10; {} x", "10");
	}

	@Test
	void letHasBlockValue() {
		assertValid("let x = { 10 }; x", "10");
	}

	@Test
	void letHasBlockValueWithLet() {
		assertValid("let x = { let y = 20; y }", "20");
	}

	@Test
	void trueTest() {
		assertValid("true", "true");
	}

	@Test
	void falseTest() {
		assertValid("false", "false");
	}

	@Test
	void ifTrue() {
		assertValid("if (true) 5 else 3", "5");
	}

	@Test
	void ifFalse() {
		assertValid("if (false) 5 else 3", "3");
	}

	@Test
	void postIncrement() {
		assertValid("let mut x = 0; x++; x", "1");
	}

	@Test
	void lessThan() {
		assertValid("1 < 2", "true");
	}

	@Test
	void whileTest() {
		assertValid("let mut i = 0; while (i < 10) i++; i", "10");
	}

	@Test
	void addAssign() {
		assertValid("let mut x = 0; x += 5; x", "5");
	}

	@Test
	void forTest() {
		assertValid("let mut sum = 0; for(let mut i = 0; i < 10; i++) sum += i; sum", "45");
	}

	@Test
	void function() {
		assertValid("fn get() => 100; get()", "100");
	}

	@Test
	void functionReturnsBraces() {
		assertValid("fn get() => { 100 } get()", "100");
	}

	@Test
	void functionReturnsAddition() {
		assertValid("fn add() => 5 + 5; add()", "10");
	}

	@Test
	void functionHasOneParameter() {
		assertValid("fn get(x : I32) => x; get(5)", "5");
	}

	@Test
	void functionHasOneTypeParameter() {
		assertValid("fn get<T>(x : T) => x; get(5)", "5");
	}

	@Test
	void struct() {
		assertValid("struct Container { value : I32 } let container = Container { 100 }; container.value", "100");
	}

	@Test
	void equalsTest() {
		assertValid("5 == 5", "true");
	}

	@Test
	void equalsInIfCondition() {
		assertValid("if (5 == 5) 10 else 20", "10");
	}

	@Test
	void enumTest() {
		assertValid("enum MyEnum { Variant } if (MyEnum.Variant == MyEnum.Variant) 1 else 2", "1");
	}

	@Test
	void impl() {
		assertValid("struct Empty {} impl Empty { fn get() => 100; } let empty = Empty {}; empty.get()", "100");
	}

	@Test
	void typeAlias() {
		assertValid("type MyInt = I32; let x : MyInt = 10; x", "10");
	}

	@Test
	void traitTest() {
		assertValid("trait Bound {} fn get<T : Bound>() => 100; get()", "100");
	}

	@Test
	void functionReturnsThisWithParameter() {
		assertValid("fn get(x : I32) => this; get(100).x", "100");
	}

	@Test
	void functionWithVariableDeclarationInside() {
		assertValid("fn get() => { let x = 100; this }; get().x", "100");
	}

	@Test
	void functionReturnsThisWithVariableDeclaration() {
		assertValid("fn get() => { let x = 100; this }; get().x", "100");
	}

	@Test
	void classWithOneParameter() {
		assertValid("class fn Container(x : I32) => {}; Container(100).x", "100");
	}

	@Test
	void classWithOneField() {
		assertValid("class fn Container() => { let x = 100; }; Container().x", "100");
	}
}