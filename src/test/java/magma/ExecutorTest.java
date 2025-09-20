package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExecutorTest {
	@Test
	public void testAdd() {
		assertEquals(5, Executor.add(2, 3));
	}
}
