package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class Main {
	private interface Result<T, X> {
		<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
	}

	private @interface Actual {}

	private interface Definable {
		String generate();
	}

	private static final class State {
		private final StringBuilder buffer = new StringBuilder();
		private final Collection<String> segments = new ArrayList<>();
		private final CharSequence input;
		private int depth = 0;
		private int index = 0;

		private State(final CharSequence input) {
			this.input = input;
		}

		private boolean hasNextChar(final char c) {
			final var peek = this.peek();
			return peek.isPresent() && peek.get().equals(c);
		}

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

		Optional<Tuple<State, Character>> pop() {
			if (this.index >= this.input.length()) return Optional.empty();
			final var next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(new Tuple<>(this, next));
		}

		Optional<Tuple<State, Character>> popAndAppendToTuple() {
			return this.pop().map(tuple -> new Tuple<>(tuple.left.append(tuple.right), tuple.right));
		}

		Optional<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}

		Optional<Character> peek() {
			if (this.index < this.input.length()) return Optional.of(this.input.charAt(this.index));

			return Optional.empty();
		}
	}

	private record Tuple<A, B>(A left, B right) {}

	private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
			return whenOk.apply(this.value);
		}
	}

	private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
			return whenErr.apply(this.error);
		}
	}

	private record Definition(Optional<String> maybeTypeParameter, String type, String name) implements Definable {
		@Override
		public String generate() {
			return this.maybeTypeParameter.map(value -> "<" + value + "> ").orElse("") + this.type + " " + this.name;
		}
	}

	private record Placeholder(String value) implements Definable {
		@Override
		public String generate() {
			return Main.wrap(this.value);
		}
	}

	private static final SequencedCollection<List<String>> typeParams = new ArrayList<>();

	private Main() {}

	public static void main(final String[] args) {
		final var source = Paths.get(".", "src", "java", "magma", "Main.java");
		final var target = Paths.get(".", "src", "windows", "magma", "Main.c");

		Main.readString(source).match(input -> {
			final var output = Main.compile(input);
			return Main.writeString(target, output);
		}, Optional::of).ifPresent(Throwable::printStackTrace);
	}

	@Actual
	private static Optional<IOException> writeString(final Path target, final CharSequence output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (final IOException e) {
			return Optional.of(e);
		}
	}

	@Actual
	private static Result<String, IOException> readString(final Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (final IOException e) {
			return new Err<>(e);
		}
	}

	private static String compile(final CharSequence input) {
		return Main.compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
		return Main.compileAll(input, mapper, Main::foldStatement, "");
	}

	private static String compileAll(final CharSequence input,
																	 final Function<String, String> mapper,
																	 final BiFunction<State, Character, State> folder,
																	 final CharSequence delimiter) {
		return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
	}

	private static List<String> divide(final CharSequence input, final BiFunction<State, Character, State> folder) {
		var current = new State(input);
		while (true) {
			final var popped = current.pop();
			if (popped.isEmpty()) break;

			final var tuple = popped.get();
			current = Main.foldDecorated(folder, tuple.left, tuple.right);
		}

		return current.advance().stream().toList();
	}

	private static State foldDecorated(final BiFunction<State, Character, State> folder,
																		 final State state,
																		 final char next) {
		return Main.foldSingleQuotes(state, next)
							 .or(() -> Main.foldDoubleQuotes(state, next))
							 .orElseGet(() -> folder.apply(state, next));
	}

	private static Optional<State> foldDoubleQuotes(final State state, final char next) {
		if ('\"' != next) return Optional.empty();
		var current = state.append('\"');

		while (true) {
			final var maybeTuple = current.popAndAppendToTuple();
			if (maybeTuple.isEmpty()) break;

			final var tuple = maybeTuple.get();
			current = tuple.left;

			if ('\\' == tuple.right) current = current.popAndAppendToOption().orElse(current);
			if ('\"' == tuple.right) break;
		}

		return Optional.of(current);
	}

	private static Optional<State> foldSingleQuotes(final State state, final char next) {
		if ('\'' != next) return Optional.empty();

		return state.append('\'')
								.popAndAppendToTuple()
								.flatMap(tuple -> Main.foldEscapeChar(tuple.left, tuple.right))
								.flatMap(State::popAndAppendToOption);
	}

	private static Optional<State> foldEscapeChar(final State state, final Character next) {
		if ('\\' == next) return state.popAndAppendToOption();
		return Optional.of(state);
	}

	private static State foldStatement(final State current, final char c) {
		final var appended = current.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('}' == c && appended.isShallow()) return appended.advance().exit();
		if ('{' == c || '(' == c) return appended.enter();
		if ('}' == c || ')' == c) return appended.exit();
		return appended;
	}

	private static String compileRootSegment(final String input) {
		final var strip = input.strip();
		if (strip.isEmpty() || strip.startsWith("package ") || strip.startsWith("import ")) return "";
		final var modifiers = Main.compileClass("class", strip, 0);
		return modifiers.orElseGet(() -> Main.wrap(strip));
	}

	private static Optional<String> compileClass(final String type, final String input, final int depth) {
		final var index = input.indexOf(type + " ");
		if (0 > index) return Optional.empty();
		final var withName = input.substring(index + (type + " ").length());

		final var contentStart = withName.indexOf('{');
		if (0 > contentStart) return Optional.empty();
		final var name = withName.substring(0, contentStart).strip();
		final var withEnd = withName.substring(contentStart + "{".length()).strip();

		if (withEnd.isEmpty() || '}' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var content = withEnd.substring(0, withEnd.length() - 1);

		return Optional.of("struct " + name + " {" +
											 Main.compileStatements(content, input1 -> Main.compileClassSegment(input1, depth + 1)) +
											 Main.createIndent(depth) + "}");
	}

	private static String compileClassSegment(final String input, final int depth) {
		final var strip = input.strip();
		if (strip.isEmpty()) return "";
		return Main.createIndent(depth) + Main.compileClassSegmentValue(strip, depth);
	}

	private static String compileClassSegmentValue(final String input, final int depth) {
		return Main.compileClass("class", input, depth)
							 .or(() -> Main.compileClass("interface", input, depth))
							 .or(() -> Main.compileClass("record", input, depth))
							 .or(() -> Main.compileMethod(input, depth))
							 .or(() -> Main.compileField(input, depth))
							 .orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileField(final String input, final int depth) {
		final var strip = input.strip();
		if (strip.isEmpty() || ';' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final var withoutEnd = strip.substring(0, strip.length() - 1);
		return Main.compileInitialization(withoutEnd, depth)
							 .or(() -> Main.parseDefinition(withoutEnd).map(Definition::generate))
							 .map(result -> result + ";");
	}

	private static Optional<String> compileInitialization(final String input, final int depth) {
		final var valueSeparator = input.indexOf('=');
		if (0 > valueSeparator) return Optional.empty();

		final var definition = input.substring(0, valueSeparator);
		final var value = input.substring(valueSeparator + 1);

		final var destination = Main.parseDefinition(definition)
																.map(Definition::generate)
																.orElseGet(() -> Main.compileValueOrPlaceholder(definition, depth));
		return Main.compileValue(value, depth).map(s -> destination + " = " + s);
	}

	private static String compileValueOrPlaceholder(final String input, final int depth) {
		return Main.compileValue(input, depth).orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileValue(final String input, final int depth) {
		final var strip = input.strip();
		return Main.compileLambda(strip, depth)
							 .or(() -> Main.compileNumber(strip))
							 .or(() -> Main.compileInvokable(strip, depth))
							 .or(() -> Main.compileAccess(strip, ".", depth))
							 .or(() -> Main.compileAccess(strip, "::", depth))
							 .or(() -> Main.compileString(strip))
							 .or(() -> Main.compileChar(strip))
							 .or(() -> Main.compileOperator(strip, "==", depth))
							 .or(() -> Main.compileOperator(strip, "!=", depth))
							 .or(() -> Main.compileOperator(strip, "+", depth))
							 .or(() -> Main.compileOperator(strip, "-", depth))
							 .or(() -> Main.compileOperator(strip, "<", depth))
							 .or(() -> Main.compileOperator(strip, "&&", depth))
							 .or(() -> Main.compileOperator(strip, "||", depth))
							 .or(() -> Main.compileOperator(strip, ">=", depth))
							 .or(() -> Main.compileOperator(strip, ">", depth))
							 .or(() -> Main.compileIdentifier(strip))
							 .or(() -> Main.compileNot(depth, strip));
	}

	private static Optional<String> compileChar(final String input) {
		if (!input.isEmpty() && '\'' == input.charAt(0) && '\'' == input.charAt(input.length() - 1))
			return Optional.of(input);
		else return Optional.empty();
	}

	private static Optional<String> compileNot(final int depth, final String strip) {
		if (!strip.isEmpty() && '!' == strip.charAt(0))
			return Optional.of("!" + Main.compileValueOrPlaceholder(strip.substring(1), depth));
		return Optional.empty();
	}

	private static Optional<String> compileLambda(final String input, final int depth) {
		final var index = input.indexOf("->");
		if (0 > index) return Optional.empty();
		final var name = input.substring(0, index).strip();
		final var after = input.substring(index + "->".length()).strip();
		final String params;
		if (name.contentEquals("()")) params = "";
		else if (Main.isIdentifier(name)) params = "auto " + name;
		else
			return Optional.empty();

		if (after.isEmpty() || '{' != after.charAt(0) || '}' != after.charAt(after.length() - 1))
			return Main.compileValue(after, depth)
								 .map(value -> Main.assembleFunction(depth, params, "auto ?",
																										 Main.createIndent(depth + 1) + "return " + value));
		final var content = after.substring(1, after.length() - 1);
		return Optional.of(Main.assembleFunction(depth, params, "auto ?", Main.compileFunctionSegments(depth, content)));
	}

	private static Optional<String> compileString(final String input) {
		if (Main.isString(input)) return Optional.of(input);
		return Optional.empty();
	}

	private static boolean isString(final CharSequence input) {
		return !input.isEmpty() && '\"' == input.charAt(0) && '\"' == input.charAt(input.length() - 1);
	}

	private static Optional<String> compileOperator(final CharSequence input,
																									final CharSequence operator,
																									final int depth) {
		final var divisions = Main.divide(input, (state, next) -> Main.foldOperator(operator, state, next));
		if (2 > divisions.size()) return Optional.empty();

		final var left = divisions.getFirst();
		final var right = divisions.getLast();

		return Main.compileValue(left, depth)
							 .flatMap(leftResult -> Main.compileValue(right, depth)
																					.map(rightResult -> leftResult + " " + operator + " " + rightResult));
	}

	private static State foldOperator(final CharSequence operator, final State state, final Character next) {
		final var state1 = Main.tryAdvanceAtOperator(operator, state, next);
		if (state1.isPresent()) return state1.get();

		final var appended = state.append(next);
		if ('(' == next) return appended.enter();
		if (')' == next) return appended.exit();
		return appended;
	}

	private static Optional<State> tryAdvanceAtOperator(final CharSequence operator,
																											final State state,
																											final Character next) {
		if (!state.isLevel() || next != operator.charAt(0)) return Optional.empty();

		if (1 == operator.length()) return Optional.of(state.advance());
		if (2 != operator.length()) return Optional.empty();

		if (state.hasNextChar(operator.charAt(1)))
			return Optional.of(state.pop().map(tuple -> tuple.left).orElse(state).advance());
		return Optional.empty();

	}

	private static Optional<String> compileIdentifier(final String input) {
		if (Main.isIdentifier(input)) return Optional.of(input);
		else return Optional.empty();
	}

	private static boolean isIdentifier(final CharSequence input) {
		if(input.isEmpty()) return false;
		return IntStream.range(0, input.length()).allMatch(index -> Main.isIdentifierChar(input, index));
	}

	private static boolean isIdentifierChar(final CharSequence input, final int index) {
		final var next = input.charAt(index);
		if (0 == index) return Character.isLetter(next);
		return Character.isLetterOrDigit(next);
	}

	private static Optional<String> compileAccess(final String input, final String delimiter, final int depth) {
		final var index = input.lastIndexOf(delimiter);
		if (0 > index) return Optional.empty();

		final var before = input.substring(0, index);
		final var property = input.substring(index + delimiter.length()).strip();
		if (!Main.isIdentifier(property)) return Optional.empty();

		return Main.compileValue(before, depth).map(result -> result + "." + property);
	}

	private static Optional<String> compileNumber(final String input) {
		if (Main.isNumber(input)) return Optional.of(input);
		else return Optional.empty();
	}

	private static boolean isNumber(final CharSequence input) {
		final var length = input.length();
		return IntStream.range(0, length).mapToObj(input::charAt).allMatch(Character::isDigit);
	}

	private static Optional<String> compileInvokable(final String input, final int depth) {
		if (input.isEmpty() || ')' != input.charAt(input.length() - 1)) return Optional.empty();
		final var withoutEnd = input.substring(0, input.length() - 1);

		final var divisions = Main.divide(withoutEnd, Main::foldInvocationStart);
		if (2 > divisions.size()) return Optional.empty();

		final var withParamStart = String.join("", divisions.subList(0, divisions.size() - 1));
		final var arguments = divisions.getLast();

		if (withParamStart.isEmpty() || '(' != withParamStart.charAt(withParamStart.length() - 1)) return Optional.empty();
		final var caller = withParamStart.substring(0, withParamStart.length() - 1);

		final String outputArguments;
		if (arguments.isEmpty()) outputArguments = "";
		else
			outputArguments = Main.compileValues(arguments, input1 -> Main.compileValueOrPlaceholder(input1, depth));

		return Main.compileConstructor(caller)
							 .or(() -> Main.compileValue(caller, depth))
							 .map(result -> result + "(" + outputArguments + ")");
	}

	private static State foldInvocationStart(final State state, final Character next) {
		final var appended = state.append(next);
		if ('(' == next) {
			final var enter = appended.enter();
			if (enter.isShallow()) return enter.advance();
			else return enter;
		}
		if (')' == next) return appended.exit();
		return appended;
	}

	private static Optional<String> compileConstructor(final String input) {
		if (input.startsWith("new ")) {
			final var slice = input.substring("new ".length());
			final var output = Main.compileType(slice);
			return Optional.of(output);
		}

		return Optional.empty();
	}

	private static Optional<String> compileMethod(final String input, final int depth) {
		final var paramStart = input.indexOf('(');
		if (0 > paramStart) return Optional.empty();
		final var definitionString = input.substring(0, paramStart);
		final var withParams = input.substring(paramStart + 1);

		final var paramEnd = withParams.indexOf(')');
		if (0 > paramEnd) return Optional.empty();
		final var params = withParams.substring(0, paramEnd);
		final var withBraces = withParams.substring(paramEnd + 1).strip();

		final var definable = Main.parseDefinitionOrPlaceholder(definitionString);

		if (definable instanceof final Definition definition)
			Main.typeParams.add(definition.maybeTypeParameter.stream().toList());

		final String newParams = Main.compileValues(params, input1 -> Main.parseDefinitionOrPlaceholder(input1).generate());
		if (definable instanceof Definition) Main.typeParams.removeLast();

		if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1)) {
			final String definition1 = definable.generate();
			return Optional.of(Main.getString(newParams, definition1, ";"));
		}

		final var content = withBraces.substring(1, withBraces.length() - 1);
		return Optional.of(
				Main.assembleFunction(depth, newParams, definable.generate(), Main.compileFunctionSegments(depth, content)));
	}

	private static String assembleFunction(final int depth,
																				 final String params,
																				 final String definition,
																				 final String content) {
		return Main.getString(params, definition, " {" + content + Main.createIndent(depth) + "}");
	}

	private static String getString(final String params, final String definition, final String content) {
		return definition + "(" + params + ")" + content;
	}

	private static String compileFunctionSegments(final int depth, final CharSequence content) {
		return Main.compileStatements(content, input1 -> Main.compileFunctionSegment(input1, depth + 1));
	}

	private static String compileValues(final CharSequence input, final Function<String, String> mapper) {
		return Main.compileAll(input, mapper, Main::foldValue, ", ");
	}

	private static String createIndent(final int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private static String compileFunctionSegment(final String input, final int depth) {
		final var strip = input.strip();
		if (strip.isEmpty()) return "";
		return Main.createIndent(depth) + Main.compileFunctionSegmentValue(strip, depth);
	}

	private static String compileFunctionSegmentValue(final String input, final int depth) {
		return Main.compileConditional(input, depth, "while")
							 .or(() -> Main.compileConditional(input, depth, "if"))
							 .or(() -> Main.compileElse(input, depth))
							 .or(() -> Main.compileFunctionStatement(input, depth))
							 .orElseGet(() -> Main.wrap(input));
	}

	private static Optional<String> compileElse(final String input, final int depth) {
		if (input.startsWith("else")) {
			final var substring = input.substring("else".length());
			return Optional.of("else " + Main.compileFunctionStatement(substring, depth));
		} else return Optional.empty();
	}

	private static Optional<String> compileFunctionStatement(final String input, final int depth) {
		if (input.isEmpty() || ';' != input.charAt(input.length() - 1)) return Optional.empty();

		final var withoutEnd = input.substring(0, input.length() - 1);
		return Main.compileFunctionStatementValue(withoutEnd, depth).map(result -> result + ";");
	}

	private static Optional<String> compileBreak(final CharSequence input) {
		if ("break".contentEquals(input)) return Optional.of("break");
		else return Optional.empty();
	}

	private static Optional<String> compileConditional(final String input, final int depth, final String type) {
		if (!input.startsWith(type)) return Optional.empty();
		final var withoutStart = input.substring(type.length()).strip();

		if (withoutStart.isEmpty() || '(' != withoutStart.charAt(0)) return Optional.empty();
		final var withCondition = withoutStart.substring(1);

		final var divisions = Main.divide(withCondition, Main::foldConditionEnd);
		if (2 > divisions.size()) return Optional.empty();

		final var withEnd = String.join("", divisions.subList(0, divisions.size() - 1));
		final var maybeWithBraces = divisions.getLast();

		if (withEnd.isEmpty() || ')' != withEnd.charAt(withEnd.length() - 1)) return Optional.empty();
		final var condition = withEnd.substring(0, withEnd.length() - 1);

		final var before = type + " (" + Main.compileValueOrPlaceholder(condition, depth) + ")";
		return Optional.of(before + Main.compileWithBraces(depth, maybeWithBraces)
																		.orElseGet(() -> Main.compileFunctionSegment(maybeWithBraces, depth + 1)));
	}

	private static State foldConditionEnd(final State state, final Character next) {
		final var appended = state.append(next);
		if ('(' == next) return appended.enter();
		if (')' == next) if (appended.isLevel()) return appended.advance();
		else return appended.exit();
		return appended;
	}

	private static Optional<String> compileWithBraces(final int depth, final String input) {
		final var withBraces = input.strip();

		if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1))
			return Optional.empty();
		final var content = withBraces.substring(1, withBraces.length() - 1);

		return Optional.of("{ " + Main.compileFunctionSegments(depth, content) + Main.createIndent(depth) + "}");
	}

	private static Optional<String> compileFunctionStatementValue(final String input, final int depth) {
		if (input.startsWith("return ")) {
			final var value = input.substring("return ".length());
			return Optional.of("return " + Main.compileValueOrPlaceholder(value, depth));
		}

		return Main.compileInvokable(input, depth)
							 .or(() -> Main.compileInitialization(input, depth))
							 .or(() -> Main.parseDefinition(input).map(Definition::generate))
							 .or(() -> Main.compilePostFix(input, depth))
							 .or(() -> Main.compileBreak(input));
	}

	private static Optional<String> compilePostFix(final String input, final int depth) {
		if (!input.endsWith("++")) return Optional.empty();
		final var slice = input.substring(0, input.length() - "++".length());
		return Main.compileValue(slice, depth).map(result -> result + "++");
	}

	private static State foldValue(final State state, final char next) {
		if (',' == next && state.isLevel()) return state.advance();

		final var appended = state.append(next);
		if ('-' == next) {
			final var peeked = state.peek();
			if (peeked.isPresent() && '>' == peeked.get()) return appended.popAndAppendToOption().orElse(appended);
		}

		if ('(' == next || '<' == next) return appended.enter();
		if (')' == next || '>' == next) return appended.exit();
		return appended;
	}

	private static Definable parseDefinitionOrPlaceholder(final String input) {
		return Main.parseDefinition(input).<Definable>map(value -> value).orElseGet(() -> new Placeholder(input));
	}

	private static Optional<Definition> parseDefinition(final String input) {
		final var strip = input.strip();
		final var index = strip.lastIndexOf(' ');
		if (0 > index) return Optional.empty();
		final var beforeName = strip.substring(0, index);
		final var name = strip.substring(index + " ".length());

		final var divisions = Main.divide(beforeName, Main::foldTypeSeparator);
		if (2 > divisions.size()) return Optional.of(new Definition(Optional.empty(), Main.compileType(beforeName), name));

		final var joined = String.join(" ", divisions.subList(0, divisions.size() - 1)).strip();
		final var type = divisions.getLast();
		if (!joined.isEmpty() && '>' == joined.charAt(joined.length() - 1)) {
			final var withoutEnd = joined.substring(0, joined.length() - 1);
			final var typeParamStart = withoutEnd.lastIndexOf('<');
			if (0 <= typeParamStart) {
				final var typeParameterString = withoutEnd.substring(typeParamStart + 1).strip();
				Main.typeParams.add(List.of(typeParameterString));
				final var generated =
						Optional.of(new Definition(Optional.of(typeParameterString), Main.compileType(type), name));
				Main.typeParams.removeLast();
				return generated;
			}
		}

		return Optional.of(new Definition(Optional.empty(), Main.compileType(type), name));
	}

	private static State foldTypeSeparator(final State state, final Character next) {
		if (' ' == next && state.isLevel()) return state.advance();

		final var appended = state.append(next);
		if ('<' == next) return appended.enter();
		if ('>' == next) return appended.exit();
		return appended;
	}

	private static String compileType(final String input) {
		final var strip = input.strip();
		if ("int".contentEquals(strip) || "boolean".contentEquals(strip)) return "int";
		if ("var".contentEquals(strip)) return "auto";
		if ("void".contentEquals(strip)) return "void";
		if ("char".contentEquals(strip) || "Character".contentEquals(strip)) return "char";
		if ("String".contentEquals(strip)) return "struct String";

		if (Main.typeParams.stream().anyMatch(frame -> frame.contains(strip))) return "typeparam " + strip;
		return Main.compileGenericType(strip)
							 .or(() -> Main.compileArrayType(strip))
							 .or(() -> Main.compileStructureType(strip))
							 .orElseGet(() -> Main.wrap(strip));
	}

	private static Optional<String> compileStructureType(final CharSequence input) {
		if (Main.isIdentifier(input)) return Optional.of("struct ");
		return Optional.empty();
	}

	private static Optional<String> compileGenericType(final String strip) {
		if (strip.isEmpty() || '>' != strip.charAt(strip.length() - 1)) return Optional.empty();
		final var withoutEnd = strip.substring(0, strip.length() - 1);

		final var index = withoutEnd.indexOf('<');
		if (0 > index) return Optional.empty();
		final var base = withoutEnd.substring(0, index);
		final var inputArguments = withoutEnd.substring(index + "<".length());

		final var outputArgs = Main.beforeTypeArguments(inputArguments);
		if (base.contentEquals("Function"))
			return Optional.of(outputArgs.getLast() + " (*)(" + outputArgs.getFirst() + ")");
		if ("BiFunction".contentEquals(base))
			return Optional.of(outputArgs.getLast() + " (*)(" + outputArgs.getFirst() + ", " + outputArgs.get(1) + ")");

		final var outputArgsString = String.join(", ", outputArgs);
		return Optional.of("template " + base + "<" + outputArgsString + ">");
	}

	private static List<String> beforeTypeArguments(final CharSequence input) {
		if (input.isEmpty()) return Collections.emptyList();
		return Main.divide(input, Main::foldValue).stream().map(Main::compileType).toList();
	}

	private static Optional<String> compileArrayType(final String input) {
		if (!input.endsWith("[]")) return Optional.empty();
		final var withoutEnd = input.substring(0, input.length() - "[]".length());
		final var slice = Main.compileType(withoutEnd);

		return Optional.of("[" + slice + "]*");
	}

	private static String wrap(final String input) {
		return "/*" + input + "*/";
	}
}