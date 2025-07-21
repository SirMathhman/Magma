package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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


		final var input = Files.readString(source);
		final var segments = Main.divide(input);
		final var joined = segments.map(Main::generatePlaceholder).collect(Collectors.joining());

		final var target = targetParent.resolve(name + ".c");
		Files.writeString(target, joined);

		final var header = targetParent.resolve(name + ".h");
		Files.writeString(header, joined);
	}

	private static Stream<String> divide(final CharSequence input) {
		DivideState current = new MutableDivideState();
		final var length = input.length();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance().stream();
	}

	private static DivideState fold(final DivideState state, final char c) {
		final var appended = state.append(c);
		if (';' == c) return appended.advance();
		return appended;
	}

	private static String generatePlaceholder(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
