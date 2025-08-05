package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
	@Test
	void invalid() {
		assertThrows(ApplicationException.class, () -> Application.run("test"));
	}

	@Test
	void valid() throws ApplicationException {
		var value = String.valueOf((int) (Math.random() * 0x1000));
		assertEquals(value, Application.run(value));
	}
}
