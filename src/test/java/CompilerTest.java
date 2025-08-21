import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {
	@Test
	public void valid() throws CompileException {
		String input = "5";
		String result = Compiler.compile(input);
		assertEquals("5", result);
	}

	@Test
	public void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("test"));
	}
}
