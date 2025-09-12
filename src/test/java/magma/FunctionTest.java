package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {
	@Test
	public void fnReturnAndCall() {
		String src = "fn get() : I32 => { return 100; } get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnNestedCall() {
		String src = "fn get() : I32 => { return 100; } fn getAnother() : I32 => { return get(); } getAnother()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnParamCall() {
		String src = "fn pass(param : I32) : I32 => { return param; } pass(100)";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnTwoParamCall() {
		String src = "fn first(a : I32, b : I32) : I32 => { return a; } first(100, 200)";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnOmitReturnType() {
		String src = "fn get() => { return 100; } get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnNoBraceBody() {
		String src = "fn get() => return 100; get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnCompactNoRet() {
		String src = "fn get() => 100; get()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnAssignedToVarCall() {
		String src = "fn get() => 100; let func = get; func()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnAssignedVarAnn() {
		String src = "fn get() => 100; let func : () => I32 = get; func()";
		TestUtils.assertValid(src, "100");
	}

	@Test
	public void fnDupParamInvalid() {
		String src = "fn first(a : I32, a : I32) : I32 => { return first; } first(100, 200)";
		TestUtils.assertInvalid(src);
	}

	@Test
	public void fnWrongArityInvalid() {
		String src = "fn pass(param : I32) : I32 => { return param; } pass()";
		TestUtils.assertInvalid(src);
	}

	@Test
	public void fnArgTypeMismatch() {
		String src = "fn pass(param : I32) : I32 => { return param; } pass(true)";
		TestUtils.assertInvalid(src);
	}

	@Test
	public void adderImplicitParams() {
		String src = "fn Adder(first : I32, second : I32) => {fn add() => first + second; this} Adder(3, 4).add()";
		assertEquals("7", TestUtils.runAndAssertOk(src));
	}

	@Test
	public void adderThisFields() {
		String src =
				"fn Adder(first : I32, second : I32) => {fn add() => this.first + this.second; this} Adder(3, 4).add()";
		assertEquals("7", TestUtils.runAndAssertOk(src));
	}

	@Test
	public void wrapperParamCapture() {
		String source = "fn Wrapper(result : I32) => {fn get() => result; this} Wrapper(100).get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}

	@Test
	public void wrapperThisMethodCap() {
		String source = "fn Wrapper() => {let result = 100; fn get() => result; this} Wrapper().get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}

	@Test
	public void wrapperThisMethod() {
		String source = "fn Wrapper() => {fn get() => 100; this} Wrapper().get()";
		assertEquals("100", TestUtils.runAndAssertOk(source));
	}
}
