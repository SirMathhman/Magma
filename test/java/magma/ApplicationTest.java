package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {
	private static final Path SOURCE = Paths.get(".", "Test.java");
	private static final Path TARGET = Paths.get(".", "Test.c");

	private static void run() {
		try {
			if (Files.exists(ApplicationTest.SOURCE)) Files.createFile(ApplicationTest.TARGET);
		} catch (final IOException e) {
			Assertions.fail(e);
		}
	}

	@AfterEach
	final void tearDown() throws IOException {
		Files.deleteIfExists(ApplicationTest.TARGET);
		Files.deleteIfExists(ApplicationTest.SOURCE);
	}

	@Test
	final void doesNotCreateTarget() {
		ApplicationTest.run();
		assertFalse(Files.exists(ApplicationTest.TARGET));
	}

	@Test
	final void createsTarget() throws IOException {
		Files.createFile(ApplicationTest.SOURCE);
		ApplicationTest.run();
		assertTrue(Files.exists(ApplicationTest.TARGET));
	}
}
