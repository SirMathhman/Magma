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
	private static class State {
		private final ArrayList<String> segments;
		private final String input;
		private StringBuilder buffer;
		private int depth;
		private int index;

		public State(String input) {
			this.input = input;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
			this.index = 0;
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

		private boolean isLevel() {
			return this.depth == 0;
		}

		private State append(char c) {
			this.buffer.append(c);
			return this;
		}

		private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		public Optional<Tuple<State, Character>> pop() {
			if (this.index >= this.input.length()) return Optional.empty();
			final char next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(new Tuple<State, Character>(this, next));
		}

		public Optional<Tuple<State, Character>> popAndAppendToTuple() {
			return this.pop().map(tuple -> new Tuple<State, Character>(tuple.left.append(tuple.right), tuple.right));
		}

		public Optional<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}
	}

	public record Tuple<A, B>(A left, B right) {}

	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);

			final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
			final Path targetParent = target.getParent();

			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);
			Files.writeString(target, "// File generated from '" + source + "'. This is not source code!\n" + compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		final String joined = compileStatements(input, Main::compileRootSegment);
		return joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" + System.lineSeparator() +
					 "\treturn 0;" + System.lineSeparator() + "}";
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
		State current = new State(input);
		while (true) {
			final Optional<Tuple<State, Character>> maybeNext = current.pop();
			if (maybeNext.isEmpty()) break;
			final Tuple<State, Character> tuple = maybeNext.get();
			current = foldEscaped(tuple.left, tuple.right, folder);
		}

		return current.advance().stream();
	}

	private static State foldEscaped(State state, char next, BiFunction<State, Character, State> folder) {
		return foldSingleQuotes(state, next).or(() -> foldDoubleQuotes(state, next))
																				.orElseGet(() -> folder.apply(state, next));
	}

	private static Optional<State> foldSingleQuotes(State state, char next) {
		if (next != '\'') return Optional.empty();

		final State appended = state.append(next);
		return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(State::popAndAppendToOption);
	}

	private static Optional<State> foldEscaped(Tuple<State, Character> tuple) {
		if (tuple.right == '\\') return tuple.left.popAndAppendToOption();
		else return Optional.of(tuple.left);
	}

	private static Optional<State> foldDoubleQuotes(State state, char next) {
		if (next != '\"') return Optional.empty();

		State appended = state.append(next);
		while (true) {
			final Optional<Tuple<State, Character>> maybeNext = appended.popAndAppendToTuple();
			if (maybeNext.isPresent()) {
				final Tuple<State, Character> tuple = maybeNext.get();
				appended = tuple.left;

				final char c = tuple.right;
				if (c == '\\') appended = appended.popAndAppendToOption().orElse(appended);
				if (c == '\"') break;
			} else break;
		}

		return Optional.of(appended);
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
		final String stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) return "";

		return compileStructure(stripped, "class").map(Tuple::right).orElseGet(() -> wrap(stripped));
	}

	private static Optional<Tuple<String, String>> compileStructure(String input, String type) {
		final int i = input.indexOf(type + " ");
		if (i < 0) return Optional.empty();

		final String afterKeyword = input.substring(i + (type + " ").length());
		final int contentStart = afterKeyword.indexOf("{");

		if (contentStart < 0) return Optional.empty();
		final String beforeContent = afterKeyword.substring(0, contentStart).strip();
		// if (!isIdentifier(beforeContent)) return Optional.empty();
		String name = beforeContent;
		String recordFields = "";
		if (beforeContent.endsWith(")")) {
			final String slice = beforeContent.substring(0, beforeContent.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				name = slice.substring(0, beforeParams);
				recordFields = compileAll(slice.substring(beforeParams + 1), Main::foldValue, Main::compileParameter);
			}
		}

		final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();

		if (!afterContent.endsWith("}")) return Optional.empty();
		final String content = afterContent.substring(0, afterContent.length() - "}".length());

		final List<String> segments = divide(content, Main::foldStatement).toList();

		StringBuilder inner = new StringBuilder();
		final StringBuilder outer = new StringBuilder();

		for (String segment : segments) {
			Tuple<String, String> compiled = compileClassSegment(segment);
			inner.append(compiled.left);
			outer.append(compiled.right);
		}

		return Optional.of(new Tuple<String, String>("",
																								 "struct " + name + " {" + recordFields + inner + "};" +
																								 System.lineSeparator() + outer));
	}

	private static String compileParameter(String input1) {
		if (input1.isEmpty()) return "";
		return generateField(input1);
	}

	private static String generateField(String input) {
		return System.lineSeparator() + "\t" + compileDefinition(input) + ";";
	}

	private static State foldValue(State state, char next) {
		if (next == ',' && state.isLevel()) return state.advance();
		else return state.append(next);
	}

	private static boolean isIdentifier(String input) {
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (!Character.isLetter(c)) return false;
		}

		return true;
	}

	private static Tuple<String, String> compileClassSegment(String input) {
		final String stripped = input.strip();
		return compileClassSegmentValue(stripped);
	}

	private static Tuple<String, String> compileClassSegmentValue(String input) {
		return compileStructure(input, "class").or(() -> compileStructure(input, "record"))
																					 .or(() -> compileField(input))
																					 .orElseGet(() -> new Tuple<String, String>(
																							 wrap(input) + System.lineSeparator(), ""));
	}

	private static Optional<Tuple<String, String>> compileField(String input) {
		if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			return Optional.of(new Tuple<String, String>(generateField(substring), ""));
		} else return Optional.empty();
	}

	private static String compileDefinition(String input) {
		final int index = input.lastIndexOf(" ");
		if (index >= 0) {
			final String beforeName = input.substring(0, index).strip();
			final String name = input.substring(index + " ".length()).strip();
			final int typeSeparator = beforeName.lastIndexOf(" ");
			if (typeSeparator >= 0) {
				final String beforeType = beforeName.substring(0, typeSeparator);
				final String type = beforeName.substring(typeSeparator + " ".length());
				return wrap(beforeType) + " " + compileType(type) + " " + name;
			}
		}

		return wrap(input);
	}

	private static String compileType(String input) {
		final String stripped = input.strip();
		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String arguments = withoutEnd.substring(argumentStart + "<".length());
				return base + "<" + compileType(arguments) + ">";
			}
		}

		if (stripped.equals("String")) return "char*";
		if (stripped.equals("int")) return "int";
		return wrap(stripped);
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
