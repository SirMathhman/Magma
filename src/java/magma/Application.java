package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

record Application(Path source) {
	private static Optional<IOException> createFile(final Path target) {
		try {
			Files.createFile(target);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	Optional<IOException> run() {
		if (!Files.exists(this.source)) return Optional.empty();

		final var fileName = this.source.getFileName().toString();
		final var separator = fileName.indexOf('.');
		final var name = fileName.substring(0, separator);

		final var target = this.source.resolveSibling(name + ".c");
		return Application.createFile(target);
	}
}