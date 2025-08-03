package magma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApplicationTest {
	@Test
	void invalid() {
		Assertions.assertThrows(ApplicationException.class, Application::run);
	}
}