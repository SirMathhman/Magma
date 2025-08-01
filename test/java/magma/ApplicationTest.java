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

	@AfterEach
	void tearDown() throws IOException {
		Files.deleteIfExists(ApplicationTest.TARGET); Files.deleteIfExists(ApplicationTest.SOURCE);
	}

	@Test
	void shouldNotCreateTargetFileWhenSourceDoesNotExist() {
		this.run(ApplicationTest.SOURCE); assertFalse(Files.exists(ApplicationTest.TARGET));
	}

	@Test
	void shouldCreateTargetFileWhenSourceExists() throws IOException {
		Files.createFile(ApplicationTest.SOURCE); this.run(ApplicationTest.SOURCE);
		assertTrue(Files.exists(ApplicationTest.TARGET));
	}

	private void run(final Path path) {
		try {
			if (Files.exists(path)) Files.createFile(ApplicationTest.TARGET);
		} catch (final IOException e) {
			Assertions.fail(e);
		}
	}
}
