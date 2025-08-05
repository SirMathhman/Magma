package magma;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
	@Test
	void invalid() {
		assertThrows(ApplicationException.class, () -> Application.run("test"));
	}

	@RepeatedTest(3)
	void test() throws ApplicationException {
		var value = (int) (Math.random() * 0x1000);
		final var input = String.valueOf(value);
		final var output = Application.run(input);
		assertEquals(output, input);
	}
}
