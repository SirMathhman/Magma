package magma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {
	private static final Path SOURCE = Paths.get(".", "Test.java");
	private static final Path TARGET = Paths.get(".", "Test.c");

	private static void tryRun() {
		ApplicationTest.runAsApplication().ifPresent(Assertions::fail);
	}

	private static Optional<IOException> runAsApplication() {
		return new Application(ApplicationTest.SOURCE).run();
	}

	private static Optional<IOException> runWithInput(final CharSequence input) throws IOException {
		Files.writeString(ApplicationTest.SOURCE, input);
		return ApplicationTest.runAsApplication();
	}

	@Test
	final void failInvalid() throws IOException {
		assertTrue(ApplicationTest.runWithInput("test").isPresent());
	}

	@Test
	final void empty() throws IOException {
		assertTrue(ApplicationTest.runWithInput("").isEmpty());
	}

	@AfterEach
	final void tearDown() throws IOException {
		Files.deleteIfExists(ApplicationTest.TARGET);
		Files.deleteIfExists(ApplicationTest.SOURCE);
	}

	@Test
	final void doesNotCreateTarget() {
		ApplicationTest.tryRun();
		assertFalse(Files.exists(ApplicationTest.TARGET));
	}

	@Test
	final void createTarget() throws IOException {
		Files.createFile(ApplicationTest.SOURCE);
		ApplicationTest.tryRun();
		assertTrue(Files.exists(ApplicationTest.TARGET));
	}
}
