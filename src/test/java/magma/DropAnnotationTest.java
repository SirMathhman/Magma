package magma;

import org.junit.jupiter.api.Test;

public class DropAnnotationTest {

	@Test
	public void dropFnRunsOnAssign() {
		String src = "let mut wasDropped = false; fn dropImpl() => wasDropped = true; type Custom = I32 & drop<dropImpl>; let value : Custom = 100; wasDropped";
		// Expect the drop function to run during assignment so the final expression is
		// "true"
		TestUtils.assertValid(src, "true");
	}
}
