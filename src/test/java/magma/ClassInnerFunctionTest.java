package magma;

import org.junit.jupiter.api.Test;

public class ClassInnerFunctionTest {

	@Test
	public void classFnInnerCall() {
		String src = "class fn Outer() => { class fn Inner() => { fn get() => 100; this; }; this; } Outer().Inner().get()";
		TestUtils.assertValid(src, "100");
	}
}
