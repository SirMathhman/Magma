package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	private static class State {
		private final Collection<String> segments = new ArrayList<>();
		private StringBuilder buffer = new StringBuilder();
		private int depth = 0;

		private Stream<String> stream() {
			return segments.stream();
		}

		private State exit() {
			this.depth = depth - 1;
			return this;
		}

		private State enter() {
			this.depth = depth + 1;
			return this;
		}

		private State advance() {
			segments.add(buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		private State append(char c) {
			buffer.append(c);
			return this;
		}
	}

	public static void main(String[] args) {
		try {
			final String input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "node", "magma", "Main.ts"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return divide(input).map(String::strip)
												.filter(segment -> !segment.startsWith("package ") && !segment.startsWith("import "))
												.map(Main::wrap)
												.collect(Collectors.joining());
	}

	private static Stream<String> divide(String input) {
		State current = new State();
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			current = fold(current, c);
		}

		return current.advance().stream();
	}

	private static State fold(State state, char c) {
		final State appended = state.append(c);
		if (c == ';' && appended.depth == 0) return appended.advance();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
