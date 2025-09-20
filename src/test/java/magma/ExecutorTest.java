package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExecutorTest {
	@Test
	public void emptyInputReturnsEmpty() throws Exception {
		assertEquals("", Executor.execute(""));
	}

	@Test
	public void nonEmptyInputThrows() {
		assertThrows(ExecutionException.class, () -> Executor.execute("data"));
	}
}
