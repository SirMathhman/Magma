package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	private sealed interface Definable extends JMethodHeader permits Definition, Placeholder {
		String generate();

		@Override
		default Definable toDefinable() {
			return this;
		}
	}

	private sealed interface JMethodHeader permits JConstructor, Definable {
		Definable toDefinable();
	}

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

	private record Definition(Optional<String> maybeBeforeType, String type, String name) implements Definable {
		public Definition(String type, String name) {
			this(Optional.empty(), type, name);
		}

		@Override
		public String generate() {
			return this.maybeBeforeType.map(Main::wrap).map(value -> value + " ").orElse("") + this.type() + " " +
						 this.name();
		}
	}

	private record Placeholder(String input) implements Definable {
		@Override
		public String generate() {
			return wrap(this.input);
		}
	}

	private record JConstructor(String name) implements JMethodHeader {
		@Override
		public Definable toDefinable() {
			return new Definition(this.name, "new_" + this.name);
		}
	}

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
		return compileAll(input, Main::foldStatement, mapper, "");
	}

	private static String compileAll(String input,
																	 BiFunction<State, Character, State> folder,
																	 Function<String, String> mapper,
																	 String delimiter) {
		return divide(input, folder).map(mapper).collect(Collectors.joining(delimiter));
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
		String beforeMaybeParams = beforeContent;
		String recordFields = "";
		if (beforeContent.endsWith(")")) {
			final String slice = beforeContent.substring(0, beforeContent.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				beforeMaybeParams = slice.substring(0, beforeParams).strip();
				final String substring = slice.substring(beforeParams + 1);
				recordFields = compileValues(substring, Main::compileParameter);
			}
		}

		String name = beforeMaybeParams;
		List<String> typeArguments = Collections.emptyList();
		if (beforeMaybeParams.endsWith(">")) {
			final String withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
			final int i1 = withoutEnd.indexOf("<");
			if (i1 >= 0) {
				name = withoutEnd.substring(0, i1);
				final String arguments = withoutEnd.substring(i1 + "<".length());
				typeArguments = divide(arguments, Main::foldValue).map(String::strip).toList();
			}
		}

		final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();

		if (!afterContent.endsWith("}")) return Optional.empty();
		final String content = afterContent.substring(0, afterContent.length() - "}".length());

		final List<String> segments = divide(content, Main::foldStatement).toList();

		StringBuilder inner = new StringBuilder();
		final StringBuilder outer = new StringBuilder();

		for (String segment : segments) {
			Tuple<String, String> compiled = compileClassSegment(segment, name);
			inner.append(compiled.left);
			outer.append(compiled.right);
		}

		String beforeStruct;
		if (typeArguments.isEmpty()) beforeStruct = "";
		else {
			final String templateValues =
					typeArguments.stream().map(slice -> "typeparam " + slice).collect(Collectors.joining(", ", "<", ">")) +
					System.lineSeparator();

			beforeStruct = "template " + templateValues;
		}

		return Optional.of(new Tuple<String, String>("",
																								 beforeStruct + "struct " + name + " {" + recordFields + inner +
																								 System.lineSeparator() + "};" + System.lineSeparator() + outer));
	}

	private static String compileValues(String input, Function<String, String> mapper) {
		return compileAll(input, Main::foldValue, mapper, ", ");
	}

	private static String compileParameter(String input1) {
		if (input1.isEmpty()) return "";
		return generateField(input1).orElseGet(() -> wrap(input1));
	}

	private static Optional<String> generateField(String input) {
		return compileDefinition(input).map(Definable::generate).map(content -> generateStatement(content, 1));
	}

	private static String generateStatement(String content, int depth) {
		return generateSegment(content + ";", depth);
	}

	private static String generateSegment(String content, int depth) {
		return generateIndent(depth) + content;
	}

	private static String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static State foldValue(State state, char next) {
		if (next == ',' && state.isLevel()) return state.advance();
		else return state.append(next);
	}

	private static Tuple<String, String> compileClassSegment(String input, String name) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) return new Tuple<String, String>("", "");
		return compileClassSegmentValue(stripped, name);
	}

	private static Tuple<String, String> compileClassSegmentValue(String input, String name) {
		if (input.isEmpty()) return new Tuple<>("", "");

		return compileStructure(input, "class").or(() -> compileStructure(input, "record"))
																					 .or(() -> compileStructure(input, "interface"))
																					 .or(() -> compileField(input))
																					 .or(() -> compileMethod(input, name))
																					 .orElseGet(() -> {
																						 final String generated = generateSegment(wrap(input), 1);
																						 return new Tuple<String, String>(generated, "");
																					 });
	}

	private static Optional<Tuple<String, String>> compileMethod(String input, String name) {
		final int paramStart = input.indexOf("(");
		if (paramStart < 0) return Optional.empty();

		final String beforeParams = input.substring(0, paramStart).strip();
		final String withParams = input.substring(paramStart + 1);

		final int paramEnd = withParams.indexOf(")");
		if (paramEnd < 0) return Optional.empty();

		final JMethodHeader methodHeader = compileMethodHeader(beforeParams);
		final String inputParams = withParams.substring(0, paramEnd);
		final String withBraces = withParams.substring(paramEnd + 1).strip();

		final String outputParams = compileParameters(inputParams);
		final String outputMethodHeader = transformMethodHeader(methodHeader, name).generate() + "(" + outputParams + ")";

		final String outputBodyWithBraces;
		if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String inputBody = withBraces.substring(1, withBraces.length() - 1);
			final String compiledBody = compileStatements(inputBody, input1 -> compileMethodSegment(input1, 1));

			String outputBody;
			if (Objects.requireNonNull(methodHeader) instanceof JConstructor)
				outputBody = generateStatement(name + " this", 1) + compiledBody + generateStatement("return this", 1);
			else outputBody = compiledBody;

			outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
		} else if (withBraces.equals(";")) outputBodyWithBraces = ";";
		else return Optional.empty();

		final String generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
		return Optional.of(new Tuple<String, String>("", generated));
	}

	private static Definable transformMethodHeader(JMethodHeader methodHeader, String name) {
		return switch (methodHeader) {
			case JConstructor constructor -> new Definition(constructor.name, "new_" + constructor.name);
			case Definition definition ->
					new Definition(definition.maybeBeforeType, definition.type, definition.name + "_" + name);
			case Placeholder placeholder -> placeholder;
		};
	}

	private static JMethodHeader compileMethodHeader(String beforeParams) {
		return compileDefinition(beforeParams).<JMethodHeader>map(definable -> definable)
																					.or(() -> compileConstructor(beforeParams))
																					.orElseGet(() -> new Placeholder(beforeParams));
	}

	private static String compileParameters(String input) {
		if (input.isEmpty()) return "";
		return compileValues(input, slice -> compileDefinition(slice).map(Definable::generate).orElse(""));
	}

	private static String compileMethodSegment(String input, int depth) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) return "";

		return generateSegment(compileMethodSegmentValue(stripped, depth), depth);
	}

	private static String compileMethodSegmentValue(String input, int depth) {
		final String stripped = input.strip();
		if (stripped.startsWith("{") && stripped.endsWith("}")) {
			final String substring = stripped.substring(1, stripped.length() - 1);
			final String compiled = compileStatements(substring, input1 -> compileMethodSegment(input1, depth + 1));
			return "{" + compiled + generateIndent(depth) + "}";
		}

		if (stripped.startsWith("if")) {
			final String withoutPrefix = stripped.substring(2);
			final int conditionEnd = findConditionEnd(withoutPrefix);
			if (conditionEnd >= 0) {
				final String substring1 = withoutPrefix.substring(0, conditionEnd).strip();
				final String body = withoutPrefix.substring(conditionEnd + 1);
				if (substring1.startsWith("(")) {
					final String expression = substring1.substring(1);
					final String condition = compileExpression(expression);
					final String compiledBody = compileMethodSegmentValue(body, depth);
					return "if (" + condition + ") " + compiledBody;
				}
			}
		}

		if (stripped.endsWith(";")) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			return compileMethodStatementValue(slice) + ";";
		}

		return wrap(stripped);
	}

	private static int findConditionEnd(String withoutPrefix) {
		int conditionEnd = -1;
		int depth0 = 0;
		for (int i = 0; i < withoutPrefix.length(); i++) {
			final char c = withoutPrefix.charAt(i);
			if (c == ')') {
				depth0--;
				if (depth0 == 0) {
					conditionEnd = i;
					break;
				}
			}
			if (c == '(') depth0++;
		}
		return conditionEnd;
	}

	private static String compileMethodStatementValue(String input) {
		if (input.startsWith("return ")) return "return " + compileExpression(input.substring("return ".length()));

		final int i = input.indexOf("=");
		if (i >= 0) {
			final String destinationString = input.substring(0, i);
			final String source = input.substring(i + 1);
			final String destination = compileDefinition(destinationString).map(Definition::generate)
																																		 .orElseGet(() -> compileExpression(
																																				 destinationString));

			return destination + " = " + compileExpression(source);
		}

		return wrap(input);
	}

	private static String compileExpression(String input) {
		final String stripped = input.strip();
		if (isString(stripped)) return stripped;

		final int i1 = stripped.indexOf("+");
		if (i1 >= 0) {
			final String left = stripped.substring(0, i1);
			final String right = stripped.substring(i1 + "+".length());
			return compileExpression(left) + " + " + compileExpression(right);
		}

		if (stripped.endsWith(")")) {
			final String slice = stripped.substring(0, stripped.length() - 1);

			int index = -1;
			int depth = 0;
			for (int i = 0; i < slice.length(); i++) {
				final char c = slice.charAt(i);
				if (c == '(') {
					depth++;
					if (depth == 1) index = i;
				}
				if (c == ')') depth--;
			}

			if (index >= 0) {
				final String caller = slice.substring(0, index);
				final String arguments = slice.substring(index + 1);
				return compileExpression(caller) + "(" + compileValues(arguments, Main::compileExpression) + ")";
			}
		}

		final int i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final String substring = stripped.substring(0, i);
			final String name = stripped.substring(i + 1).strip();
			if (isIdentifier(name)) return compileExpression(substring) + "." + name;
		}

		if (isIdentifier(stripped)) return stripped;
		if (isNumber(stripped)) return stripped;

		return wrap(stripped);
	}

	private static boolean isString(String stripped) {
		if (stripped.length() <= 2) return false;

		final boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
		if (!hasDoubleQuotes) return false;

		return !stripped.substring(1, stripped.length() - 1).contains("\"");
	}

	private static boolean isNumber(String input) {
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (!Character.isDigit(c)) return false;
		}

		return true;
	}

	private static boolean isIdentifier(String input) {
		for (int i = 0; i < input.length(); i++) if (!Character.isLetter(input.charAt(i))) return false;
		return true;
	}

	private static Optional<JMethodHeader> compileConstructor(String beforeParams) {
		final int separator = beforeParams.lastIndexOf(" ");
		if (separator < 0) return Optional.empty();

		final String name = beforeParams.substring(separator + " ".length());
		return Optional.of(new JConstructor(name));
	}

	private static Optional<Tuple<String, String>> compileField(String input) {
		if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			final Optional<String> s = generateField(substring);
			if (s.isPresent()) return Optional.of(new Tuple<String, String>(s.get(), ""));
		}

		return Optional.empty();
	}

	private static Optional<Definition> compileDefinition(String input) {
		final String stripped = input.strip();
		final int index = stripped.lastIndexOf(" ");
		if (index < 0) return Optional.empty();

		final String beforeName = stripped.substring(0, index).strip();
		final String name = stripped.substring(index + " ".length()).strip();
		if (!isIdentifier(name)) return Optional.empty();

		final int typeSeparator = beforeName.lastIndexOf(" ");
		if (typeSeparator < 0) return compileType(beforeName).map(type -> new Definition(type, name));

		final String beforeType = beforeName.substring(0, typeSeparator);
		final String typeString = beforeName.substring(typeSeparator + " ".length());
		return compileType(typeString).map(type -> new Definition(Optional.of(beforeType), type, name));
	}

	private static Optional<String> compileType(String input) {
		final String stripped = input.strip();
		if (stripped.equals("public")) return Optional.empty();

		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String arguments = withoutEnd.substring(argumentStart + "<".length());
				return Optional.of(base + "<" + compileType(arguments).orElse("") + ">");
			}
		}

		if (stripped.equals("String")) return Optional.of("char*");
		if (stripped.equals("int")) return Optional.of("int");
		return Optional.of(wrap(stripped));
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
