/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());

			Main.runWithSources(sources, sourceDirectory);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static void runWithSources(final Iterable<Path> sources, final Path sourceDirectory) throws IOException {
		for (final var source : sources) Main.runWithSource(source, sourceDirectory);
	}

	private static void runWithSource(final Path source, final Path sourceDirectory) throws IOException {
		final var relativeParent = sourceDirectory.relativize(source.getParent());
		final var fileName = source.getFileName().toString();
		final var separator = fileName.lastIndexOf('.');
		final var name = fileName.substring(0, separator);

		final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
		if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

		final var target = targetParent.resolve(name + ".ts");

		final var input = Files.readString(source);
		Files.writeString(target, "start" + input.replace("start", "start").replace("end", "end") + "end");
	}
}
*/