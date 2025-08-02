package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Main {
	private static final class State {
		private final StringBuilder buffer = new StringBuilder();
		private final Collection<String> segments = new ArrayList<>();
		private int depth = 0;

		private Stream<String> stream() {
			return this.segments.stream();
		}

		private State append(final char c) {
			this.buffer.append(c);
			return this;
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private boolean isLevel() {
			return 0 == this.depth;
		}

		private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private boolean isShallow() {
			return 1 == this.depth;
		}
	}

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
		final var length = input.length();
		var current = new State();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance().stream().map(mapper).collect(Collectors.joining());
	}

	private static State fold(final State current, final char c) {
		final var appended = current.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('}' == c && appended.isShallow()) return appended.advance().exit();
		if ('{' == c) return appended.enter();
		if ('}' == c) return appended.exit();
		return appended;
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		final var modifiers = Main.compileClass(strip, 1);
		return modifiers.orElseGet(() -> Main.wrap(strip));
	}

	private static Optional<String> compileClass(final String input, final int depth) {
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

		return Optional.of(Main.wrap(modifiers) + "struct " + name + " {" +
											 Main.compileStatements(content, input1 -> Main.compileClassSegment(input1, depth)) + "}");
	}

	private static String compileClassSegment(final String input, final int depth) {
		return System.lineSeparator() + "\t".repeat(depth) + Main.compileClassSegmentValue(input.strip(), depth + 1);
	}

	private static String compileClassSegmentValue(final String input, final int depth) {
		return Main.compileClass(input, depth)
							 .or(() -> Main.compileField(input))
							 .or(() -> Main.compileMethod(input))
							 .orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileField(final String input) {
		final var strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final var input1 = strip.substring(0, strip.length() - 1);

		final var valueSeparator = input1.lastIndexOf('=');
		if (0 > valueSeparator) return Optional.empty();
		final var definition = input1.substring(0, valueSeparator);
		final var value = input1.substring(valueSeparator + 1);

		return Optional.of(Main.compileDefinition(definition) + " = " + Main.compileValue(value) + ";");
	}

	private static String compileValue(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("new ")) {
			final var slice = strip.substring("new ".length());
			return Main.compileValue(slice);
		}

		return Main.wrap(strip);
	}

	private static Optional<String> compileMethod(final String input) {
		final var paramStart = input.indexOf('(');
		if (0 > paramStart) return Optional.empty();
		final var definition = input.substring(0, paramStart);
		final var withParams = input.substring(paramStart + 1);

		final var paramEnd = withParams.indexOf(')');
		if (0 > paramEnd) return Optional.empty();
		final var params = withParams.substring(0, paramEnd);
		final var withBraces = withParams.substring(paramEnd + 1);

		return Optional.of(Main.compileDefinition(definition) + "(" + Main.wrap(params) + ")" + Main.wrap(withBraces));
	}

	private static String compileDefinition(final String input) {
		final var strip = input.strip();
		final var index = strip.lastIndexOf(' ');
		if (0 <= index) {
			final var beforeName = strip.substring(0, index);
			final var name = strip.substring(index + " ".length());
			final var i = beforeName.lastIndexOf(' ');
			if (0 <= i) {
				final var beforeType = beforeName.substring(0, i);
				final var type = beforeName.substring(i + " ".length());
				return Main.wrap(beforeType) + " " + Main.compileType(type) + " " + name;
			}
		}

		return Main.wrap(strip);
	}

	private static String compileType(final String input) {
		final var strip = input.strip();
		if ("void".contentEquals(strip)) return "void";
		return "struct " + input;
	}

	private static String wrap(final String input) {
		return "/*" + input + "*/";
	}
}