package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
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
												.map(Main::compileRootSegment)
												.collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		return compileClass(input).orElseGet(() -> wrap(input));
	}

	private static Optional<String> compileClass(String input) {
		final int i = input.indexOf("class ");
		if (i < 0) return Optional.empty();
		final String modifiers = input.substring(0, i);
		final String afterKeyword = input.substring(i + "class ".length());

		final int i1 = afterKeyword.indexOf("{");
		if (i1 < 0) return Optional.empty();
		final String name = afterKeyword.substring(0, i1).strip();
		final String substring = afterKeyword.substring(i1).strip();

		if (!substring.endsWith("}")) return Optional.empty();
		final String content = substring.substring(0, substring.length() - 1);
		return Optional.of(wrap(modifiers) + "class " + name + " {" + wrap(content) + "}");
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
