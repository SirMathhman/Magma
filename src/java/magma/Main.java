package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
	private static class State {
		private final Collection<String> segments = new ArrayList<>();
		private StringBuilder buffer = new StringBuilder();
		private int depth = 0;

		private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private State append(final char c) {
			this.buffer.append(c);
			return this;
		}

		private boolean isLevel() {
			return 0 == this.depth;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}
	}

	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

			final var targetParent = Paths.get(".", "src", "windows", "magma");
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

			final var target = targetParent.resolve("Main.c");

			final var segments = Main.divide(input);
			final var joined = segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());
			Files.writeString(target, joined);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return Main.generatePlaceholder(strip) + System.lineSeparator();
	}

	private static List<String> divide(final CharSequence input) {
		final var length = input.length();
		var current = new State();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = Main.fold(current, c);
		}

		return current.advance().stream().toList();
	}

	private static State fold(final State state, final char c) {
		final var current = state.append(c);
		if (';' == c && current.isLevel()) return current.advance();
		if ('{' == c) return current.enter();
		if ('}' == c) return current.exit();
		return current;
	}

	private static String generatePlaceholder(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
