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

	private static void tryRun() {
		try {
			ApplicationTest.run(ApplicationTest.SOURCE);
		} catch (final IOException e) {
			Assertions.fail(e);
		}
	}

	private static void run(final Path source) throws IOException {
		if (!Files.exists(source)) return;

		final var fileName = source.getFileName().toString();
		final var separator = fileName.indexOf('.');
		final var name = fileName.substring(0, separator);

		final var target = source.resolveSibling(name + ".c");
		Files.createFile(target);
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
	final void createsTarget() throws IOException {
		Files.createFile(ApplicationTest.SOURCE);
		ApplicationTest.tryRun();
		assertTrue(Files.exists(ApplicationTest.TARGET));
	}
}
