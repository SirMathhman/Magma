package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public record StringBuffer(List<Character> chars) {
		public StringBuffer() {
			this(new ArrayList<Character>());
		}

		public StringBuffer clear() {
			this.chars.clear();
			return this;
		}

		public StringBuffer append(char c) {
			this.chars.add(c);
			return this;
		}

		public String intoString() {
			final char[] array = new char[this.chars.size()];
			for (int i = 0; i < this.chars.size(); i++) array[i] = this.chars.get(i);
			return new String(array);
		}
	}

	public static class State {
		private final List<String> segments;
		private StringBuffer buffer;
		private int depth;

		public State() {
			this.buffer = new StringBuffer();
			this.segments = new ArrayList<String>();
			this.depth = 0;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private State advance() {
			this.segments.add(this.buffer.intoString());
			this.buffer = this.buffer.clear();
			return this;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private State append(char c) {
			this.buffer = this.buffer.append(c);
			return this;
		}
	}

	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			Files.writeString(source.resolveSibling("main.c"), compile(input));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String compile(String input) {
		return compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(String input, Function<String, String> mapper) {
		return compileAll(input, Main::foldStatement, mapper);
	}

	private static String compileAll(String input,
																	 BiFunction<State, Character, State> folder,
																	 Function<String, String> mapper) {
		return divide(input, folder).map(mapper).collect(Collectors.joining());
	}

	private static Stream<String> divide(String input, BiFunction<State, Character, State> folder) {
		State current = new State();
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			current = folder.apply(current, c);
		}

		return current.advance().stream();
	}

	private static State foldStatement(State state, char c) {
		final State appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) return appended.advance().exit();
		if (c == '{') return appended.enter();
		if (c == '}') return appended.exit();
		return appended;
	}

	private static String compileRootSegment(String input) {
		final String strip = input.strip();
		if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
		return compileStructure(strip).orElseGet(() -> wrap(strip));
	}

	private static Optional<String> compileStructure(String input) {
		if (!input.endsWith("}")) return Optional.empty();

		final String withoutEnd = input.substring(0, input.length() - "}".length());
		final int index = withoutEnd.indexOf("{");
		if (index < 0) return Optional.empty();

		final String header = withoutEnd.substring(0, index).strip();
		final String body = withoutEnd.substring(index + "{".length());
		return Optional.of(compileStructureHeader(header) + " {};" + System.lineSeparator() +
											 compileStatements(body, Main::compileClassSegment));
	}

	private static String compileClassSegment(String input) {
		final String stripped = input.strip();
		return compileClassSegmentValue(stripped) + System.lineSeparator();
	}

	private static String compileClassSegmentValue(String input) {
		return compileStructure(input).or(() -> compileMethod(input))
																	.or(() -> compileField(input))
																	.orElseGet(() -> wrap(input));
	}

	private static Optional<? extends String> compileField(String input) {
		final String stripped = input.strip();
		if (stripped.endsWith(";")) {
			final String slice = stripped.substring(0, stripped.length() - ";".length());
			return Optional.of(compileDefinition(slice) + ";");
		} else return Optional.empty();
	}

	private static Optional<String> compileMethod(String input) {
		if (!input.endsWith("}")) return Optional.empty();
		final String withoutEnd = input.substring(0, input.length() - "}".length());

		final int index = withoutEnd.indexOf("{");
		if (index < 0) return Optional.empty();
		final String header = withoutEnd.substring(0, index).strip();
		final String body = withoutEnd.substring(index + "{".length());

		if (!header.endsWith(")")) return Optional.empty();
		final String headerWithoutEnd = header.substring(0, header.length() - ")".length());

		final int paramStart = headerWithoutEnd.indexOf("(");
		if (paramStart < 0) return Optional.empty();
		final String definition = headerWithoutEnd.substring(0, paramStart);
		final String params = headerWithoutEnd.substring(paramStart + "(".length());

		return Optional.of(compileDefinition(definition) + "(" + compileParameters(params) + "){" + wrap(body) + "}");
	}

	private static String compileParameters(String params) {
		return compileAll(params, Main::foldValue, Main::compileDefinition);
	}

	private static State foldValue(State state, char c) {
		if (c == ',') return state.advance();
		else return state.append(c);
	}

	private static String compileDefinition(String input) {
		final String stripped = input.strip();
		final int index = stripped.lastIndexOf(" ");
		if (index >= 0) {
			final String beforeName = stripped.substring(0, index).strip();
			final String name = stripped.substring(index + " ".length());
			final int typeSeparator = beforeName.lastIndexOf(" ");
			if (typeSeparator >= 0) {
				final String beforeType = beforeName.substring(0, typeSeparator);
				final String type = beforeName.substring(typeSeparator + " ".length());
				return wrap(beforeType) + " " + compileType(type) + " " + name;
			} else return compileType(beforeName) + " " + name;
		}

		return wrap(stripped);
	}

	private static String compileType(String input) {
		final String stripped = input.strip();
		if (stripped.equals("void")) return "void";

		return wrap(stripped);
	}

	private static String compileStructureHeader(String input) {
		final int index = input.indexOf("class ");
		if (index >= 0) {
			final String name = input.substring(index + "class ".length());
			return "struct " + name;
		}

		if (input.endsWith(")")) {
			final String withoutEnd = input.substring(0, input.length() - ")".length());
			final int paramStart = withoutEnd.indexOf("(");
			if (paramStart >= 0) {
				final String beforeParams = withoutEnd.substring(0, paramStart);
				final String params = withoutEnd.substring(paramStart + "(".length());
				final int keywordIndex = beforeParams.indexOf("record ");
				if (keywordIndex >= 0) {
					// compileParameters(params);
					final String modifiers = beforeParams.substring(0, keywordIndex);
					final String name = beforeParams.substring(keywordIndex + "record ".length());
					return "struct " + name;
				}
			}
		}

		return wrap(input);
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
