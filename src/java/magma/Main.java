package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "windows", "magma", "Main.c"), Main.compile(input));
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(final CharSequence input) {
		return Main.compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
		final Collection<String> segments = new ArrayList<>();
		final var buffer = new StringBuilder();
		final var length = input.length();
		var depth = 0;
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (';' == c && 0 == depth) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				continue;
			}
			if ('}' == c && 1 == depth) {
				segments.add(buffer.toString());
				buffer.setLength(0);
				depth--;
				continue;
			}
			if ('{' == c) depth++;
			if ('}' == c) depth--;
		}
		segments.add(buffer.toString());

		return segments.stream().map(mapper).collect(Collectors.joining());
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		final var modifiers = Main.compileClass(strip);
		return modifiers.orElseGet(() -> Main.wrap(strip));
	}

	private static Optional<String> compileClass(final String input) {
		final var index = input.indexOf("class ");
		if (0 > index) return Optional.empty();
		final var modifiers = input.substring(0, index);
		final var withName = input.substring(index + "class ".length());

		final var contentStart = withName.indexOf('{');
		if (0 > contentStart) return Optional.empty();
		final var name = withName.substring(0, contentStart).strip();
		final var withEnd = withName.substring(contentStart + "{".length()).strip();

		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		return Optional.of(
				Main.wrap(modifiers) + "struct " + name + " {" + Main.compileStatements(content, Main::compileClassSegment) +
				"}");
	}

	private static String compileClassSegment(final String input) {
		return System.lineSeparator() + "\t" + Main.wrap(input.strip());
	}

	private static String wrap(final String input) {
		return "/*" + input + "*/";
	}
}