package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
	private static class DivideState {
		private final Collection<String> segments = new ArrayList<>();
		private StringBuilder buffer = new StringBuilder();
		private int depth = 0;

		private DivideState advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		private DivideState append(final char c) {
			this.buffer.append(c);
			return this;
		}

		final boolean isLevel() {
			return 0 == this.depth;
		}

		final DivideState enter() {
			this.depth++;
			return this;
		}

		final DivideState exit() {
			this.depth--;
			return this;
		}
	}

	private Main() {}

	public static void main(final String[] args) {
		try {
			final var source = Paths.get(".", "src", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			final var output = Main.compile(input);
			Files.writeString(target, output);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(final CharSequence input) {
		return Main.divide(input).map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return Main.compileRootSegmentValue(strip) + System.lineSeparator();
	}

	private static String compileRootSegmentValue(final String input) {
		return Main.compileClass(input).orElseGet(() -> Main.generatePlaceholder(input));
	}

	private static Optional<String> compileClass(final String input) {
		final var classIndex = input.indexOf("class ");
		if (0 > classIndex) {return Optional.empty();}
		final var beforeKeyword = input.substring(0, classIndex);
		final var afterKeyword = input.substring(classIndex + "class ".length());

		final var contentStart = afterKeyword.indexOf('{');
		if (0 > contentStart) {return Optional.empty();}
		final var name = afterKeyword.substring(0, contentStart).strip();
		final var afterName = afterKeyword.substring(contentStart + "{".length()).strip();

		if (afterName.isEmpty() || '}' != afterName.charAt(afterName.length() - 1)) {return Optional.empty();}
		final var withoutEnd = afterName.substring(0, afterName.length() - "}".length());

		return Optional.of(Main.generatePlaceholder(beforeKeyword) + "struct " + name + " {};" + System.lineSeparator() +
											 Main.generatePlaceholder(withoutEnd));

	}

	private static Stream<String> divide(final CharSequence input) {
		var current = new DivideState();
		final var length = input.length();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance().stream();
	}

	private static DivideState fold(final DivideState state, final char c) {
		final var appended = state.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('{' == c) return appended.enter();
		if ('}' == c) return appended.exit();
		return appended;
	}

	private static String generatePlaceholder(final String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}