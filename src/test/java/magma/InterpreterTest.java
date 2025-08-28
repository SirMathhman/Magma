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
	void multiplyPrecedence() {
		// multiplication and division should bind tighter than addition/subtraction
		// multiplication should bind tighter than addition/subtraction
		assertValid("2 + 3 * 4", "14");
	}

	@Test
	void dividePrecedence() {
		// division should bind tighter than addition/subtraction
		assertValid("8 / 4 + 2", "4");
	}

	@Test
	void modulo() {
		assertValid("10 % 3", "1");
	}

	@Test
	void addInvalidMismatchedTypes() {
		assertInvalid("0U8 + 10I32");
	}

	@Test
	void subtractInvalidMismatchedTypes() {
		assertInvalid("5U16 - 3I8");
	}

	@Test
	void multiplyInvalidMismatchedTypes() {
		assertInvalid("2U32 * 4I64");
	}

	@Test
	void divideInvalidMismatchedTypes() {
		assertInvalid("8I16 / 2U32");
	}

	@Test
	void moduloInvalidMismatchedTypes() {
		assertInvalid("10U64 % 3I32");
	}

	@Test
	void addValidSameTypes() {
		assertValid("5U8 + 3U8", "8");
	}

	@Test
	void addValidMixedTypedAndUntypedOperands() {
		assertValid("5U8 + 3", "8");
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
	void nestedBraces() {
		assertValid("{{{5}}}", "5");
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
	void andTest() {
		assertValid("true && false", "false");
	}

	@Test
	void orTest() {
		assertValid("true || false", "true");
	}

	@Test
	void complexBooleanTest() {
		assertValid("(true || false) && true", "true");
	}

	@Test
	void orWithParenthesesTest() {
		assertValid("(true || false)", "true");
	}

	@Test
	void andRequiresBooleans() {
		// both operands must be booleans
		assertInvalid("true && 5");
		assertInvalid("5 && true");
	}

	@Test
	void orRequiresBooleans() {
		// both operands must be booleans
		assertInvalid("true || 5");
		assertInvalid("5 || true");
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
	void functionReturnsBoolean() {
		// function returning a boolean should work
		assertValid("fn isTrue() => true; isTrue()", "true");
	}

	@Test
	void functionMutatesOuterVariable() {
		// function should be able to assign to an outer mutable variable
		assertValid("let mut x = true; fn doSomething() => x = false; doSomething(); x", "false");
	}

	@Test
	void eagernessShortCircuitAvoidsInvoke() {
		// ensure short-circuit && does not evaluate RHS (no eager invocation)
		assertValid("let mut invoked = false; fn invoke() => invoked = true; let discard = false && invoke(); invoked",
				"false");
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
	void structWithMultipleTypeParameters() {
		// struct with two type parameters A and B; verify field access works
		assertValid("struct Pair<A, B> { first : A, second : B } let p = Pair { 5, true }; p.first", "5");
	}

	@Test
	void unionTypeIsOperatorWithSuffixLiteral() {
		// declare a union type, initialize with a suffixed literal and test 'is'
		assertValid("type MyUnion = I32 | U8; let u : MyUnion = 5U8; let result : Bool = u is U8; result", "true");
	}

	@Test
	void isOperatorOnInstance() {
		assertValid("struct S {} let s = S {}; let r : Bool = s is S; r", "true");
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

	@Test
	void dropSemantics() {
		assertValid(
				"let mut wasDropped = false; fn dropFn() => wasDropped = true; type DropI32 = I32 & drop(dropFn); let myValue : DropI32 = 100; wasDropped",
				"true");
	}

	@Test
	void pointer() {
		assertValid("let x : I32 = 100; let y : *I32 = &x; let z : I32 = *y; z", "100");
	}

	@Test
	void mutPointer() {
		assertValid("let mut x = 0; let y : mut *I32 = &mut x; *y = 100; x", "100");
	}

	@Test
	void arrayIndexing() {
		assertValid("let x : [I32; 3] = [1, 2, 3]; x[1]", "2");
	}

	@Test
	void arrayElementAssignment() {
		assertValid("let mut array = [1, 2, 3]; array[0] = 100; array[0]", "100");
	}
}