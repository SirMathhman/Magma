package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		final var sourceDirectory = Paths.get(".", "src");
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
		for (final var source : sources) Main.runWithSource(sourceDirectory, source);
	}

	private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
		final var relativeParent = sourceDirectory.relativize(source.getParent());
		final var fileName = source.getFileName().toString();
		final var separator = fileName.lastIndexOf('.');
		final var name = fileName.substring(0, separator);

		final var targetDirectory = Paths.get(".", "windows");
		final var targetParent = targetDirectory.resolve(relativeParent);
		if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

		final var target = targetParent.resolve(name + ".c");

		final var input = Files.readString(source);
		final var segments = Main.divide(input);

		final var joined = segments.stream().map(Main::generatePlaceholder).collect(Collectors.joining());
		Files.writeString(target, joined);
	}

	private static Collection<String> divide(final String input) {
		final var segments = new ArrayList<String>();
		final var buffer = new StringBuilder();
		final var length = input.length();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (';' == c) {
				segments.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}
		}
		segments.add(buffer.toString());
		return segments;
	}

	private static String generatePlaceholder(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
