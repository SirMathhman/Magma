package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static class DivideState {
		private final Collection<String> segments = new ArrayList<>();
		private StringBuilder buffer = new StringBuilder();

		private DivideState advance() {
			segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private Stream<String> stream() {
			return segments.stream();
		}

		private DivideState append(char c) {
			buffer.append(c);
			return this;
		}
	}

	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			final var output = compile(input);
			Files.writeString(target, output);
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return divide(input).map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return generatePlaceholder(strip) + System.lineSeparator();
	}

	private static Stream<String> divide(String input) {
		var current = new DivideState();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			current = fold(current, c);
		}

		return current.advance().stream();
	}

	private static DivideState fold(DivideState state, char c) {
		final var appended = state.append(c);
		if (c == ';') {
			return appended.advance();
		}

		return appended;
	}

	private static String generatePlaceholder(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}