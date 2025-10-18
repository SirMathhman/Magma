package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
	private sealed interface Definable extends JMethodHeader permits Definition, Placeholder {
		String generate();
	}

	private sealed interface JMethodHeader permits JConstructor, Definable {}

	sealed interface Result<T, X> permits Err, Ok {}

	@interface Actual {}

	private static final class ParseState {
		private final List<String> functions;
		private final List<String> structs;
		private final ArrayList<String> statements;
		private int counter;

		public ParseState() {
			this.functions = new ArrayList<String>();
			this.structs = new ArrayList<String>();
			this.counter = -1;
			this.statements = new ArrayList<>();
		}

		public ParseState addFunction(String func) {
			this.functions.add(func);
			return this;
		}

		public ParseState addStruct(String struct) {
			this.structs.add(struct);
			return this;
		}

		public String generateAnonymousFunctionName() {
			this.counter++;
			return "__lambda" + this.counter + "__";
		}

		public ParseState addStatement(String statement) {
			this.statements.add(statement);
			return this;
		}

		public ArrayList<String> popStatements() {
			final ArrayList<String> copy = new ArrayList<String>(this.statements);
			this.statements.clear();
			return copy;
		}
	}

	private static class DivideState {
		private final ArrayList<String> segments;
		private final String input;
		private StringBuilder buffer;
		private int depth;
		private int index;

		public DivideState(String input) {
			this.input = input;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
			this.index = 0;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		private DivideState enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private DivideState exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private DivideState append(char c) {
			this.buffer.append(c);
			return this;
		}

		private DivideState advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		public Optional<Tuple<DivideState, Character>> pop() {
			if (this.index >= this.input.length()) return Optional.empty();
			final char next = this.input.charAt(this.index);
			this.index++;
			return Optional.of(new Tuple<DivideState, Character>(this, next));
		}

		public Optional<Tuple<DivideState, Character>> popAndAppendToTuple() {
			return this.pop().map(tuple -> new Tuple<DivideState, Character>(tuple.left.append(tuple.right), tuple.right));
		}

		public Optional<DivideState> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}

		public Optional<Character> peek() {
			if (this.index < this.input.length()) return Optional.of(this.input.charAt(this.index));
			else return Optional.empty();
		}
	}

	public record Tuple<A, B>(A left, B right) {}

	private record Definition(List<String> annotations, String type, String name) implements Definable {
		@Override
		public String generate() {
			return this.type + " " + this.name;
		}
	}

	private record Placeholder(String input) implements Definable {
		@Override
		public String generate() {
			return wrap(this.input);
		}
	}

	private record JConstructor(String name) implements JMethodHeader {}

	record Ok<T, X>(T value) implements Result<T, X> {}

	record Err<T, X>(X error) implements Result<T, X> {}

	public static void main(String[] args) {
		run().ifPresent(Throwable::printStackTrace);
	}

	private static Optional<IOException> run() {
		final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");

		final Result<String, IOException> readResult = readString(source);
		if (readResult instanceof Ok<String, IOException>(String input)) {
			final Path targetParent = target.getParent();

			if (!Files.exists(targetParent)) return createDirectories(targetParent);
			final String output = "// File generated from '" + source + "'. This is not source code!\n" + compile(input);
			return writeString(target, output);
		}

		return Optional.empty();
	}

	@Actual
	private static Optional<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	@Actual
	private static Optional<IOException> createDirectories(Path targetParent) {
		try {
			Files.createDirectories(targetParent);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	@Actual
	private static Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private static String compile(String input) {
		StringJoiner joiner = new StringJoiner("");
		ParseState state = new ParseState();
		List<String> list = divide(input, Main::foldStatement).toList();
		int i = 0;
		while (i < list.size()) {
			String input1 = list.get(i);
			Tuple<String, ParseState> s = compileRootSegment(input1, state);
			joiner.add(s.left);
			state = s.right;
			i++;
		}

		final String joined = joiner.toString();
		final String joinedStructs = String.join("", state.structs);
		final String joinedFunctions = String.join("", state.functions);

		return joinedStructs + joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" +
					 System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";
	}

	private static Stream<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
		Tuple<DivideState, Boolean> current = new Tuple<DivideState, Boolean>(new DivideState(input), true);
		while (current.right) current = foldCycle(current.left, folder);
		return current.left.advance().stream();
	}

	private static Tuple<DivideState, Boolean> foldCycle(DivideState state,
																											 BiFunction<DivideState, Character, DivideState> folder) {
		final Optional<Tuple<DivideState, Character>> maybeNext = state.pop();
		if (maybeNext.isEmpty()) return new Tuple<DivideState, Boolean>(state, false);
		final Tuple<DivideState, Character> tuple = maybeNext.get();
		return new Tuple<DivideState, Boolean>(foldEscaped(tuple.left, tuple.right, folder), true);
	}

	private static DivideState foldEscaped(DivideState state,
																				 char next,
																				 BiFunction<DivideState, Character, DivideState> folder) {
		return foldSingleQuotes(state, next).or(() -> foldDoubleQuotes(state, next)).orElseGet(() -> folder.apply(state,
																																																							next));
	}

	private static Optional<DivideState> foldSingleQuotes(DivideState state, char next) {
		if (next != '\'') return Optional.empty();

		final DivideState appended = state.append(next);
		return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(DivideState::popAndAppendToOption);
	}

	private static Optional<DivideState> foldEscaped(Tuple<DivideState, Character> tuple) {
		if (tuple.right == '\\') return tuple.left.popAndAppendToOption();
		else return Optional.of(tuple.left);
	}

	private static Optional<DivideState> foldDoubleQuotes(DivideState state, char next) {
		if (next != '\"') return Optional.empty();

		Tuple<DivideState, Boolean> current = new Tuple<DivideState, Boolean>(state.append(next), true);
		while (current.right) current = foldUntilDoubleQuotes(current.left);
		return Optional.of(current.left);
	}

	private static Tuple<DivideState, Boolean> foldUntilDoubleQuotes(DivideState state) {
		final Optional<Tuple<DivideState, Character>> maybeNext = state.popAndAppendToTuple();
		if (maybeNext.isEmpty()) return new Tuple<DivideState, Boolean>(state, false);

		final Tuple<DivideState, Character> tuple = maybeNext.get();
		final DivideState nextState = tuple.left;
		final char nextChar = tuple.right;

		if (nextChar == '\\')
			return new Tuple<DivideState, Boolean>(nextState.popAndAppendToOption().orElse(nextState), true);
		if (nextChar == '\"') return new Tuple<DivideState, Boolean>(nextState, false);
		return new Tuple<DivideState, Boolean>(nextState, true);
	}

	private static DivideState foldStatement(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) {
			final Optional<Character> peeked = appended.peek();
			final DivideState withPeeked;
			if (peeked.isPresent() && peeked.get() == ';') withPeeked = appended.popAndAppendToOption().orElse(appended);
			else withPeeked = appended;
			return withPeeked.advance().exit();
		}
		if (c == '{' || c == '(') return appended.enter();
		if (c == '}' || c == ')') return appended.exit();
		return appended;
	}

	private static Tuple<String, ParseState> compileRootSegment(String input, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) return new Tuple<String, ParseState>("", state);
		if (stripped.startsWith("package ") || stripped.startsWith("import "))
			return new Tuple<String, ParseState>("", state);

		return compileStructure(stripped, "class", state).orElseGet(() -> new Tuple<String, ParseState>(wrap(stripped),
																																																		state));
	}

	private static Optional<Tuple<String, ParseState>> compileStructure(String input, String type, ParseState state) {
		final int i = input.indexOf(type + " ");
		if (i < 0) return Optional.empty();

		final String afterKeyword = input.substring(i + (type + " ").length());
		final int contentStart = afterKeyword.indexOf("{");

		if (contentStart < 0) return Optional.empty();

		final String beforeContent = afterKeyword.substring(0, contentStart).strip();
		String withoutPermits = beforeContent;
		List<String> variants = Collections.emptyList();

		final int permitsIndex = beforeContent.indexOf("permits");
		if (permitsIndex >= 0) {
			final String slice = beforeContent.substring(permitsIndex + "permits".length());
			variants = divide(slice, Main::foldValue).map(String::strip).filter(segment -> !segment.isEmpty()).toList();
			withoutPermits = beforeContent.substring(0, permitsIndex);
		}

		String maybeWithExtends = withoutPermits.strip();
		final int extendsIndex = maybeWithExtends.indexOf("extends");
		if (extendsIndex >= 0) maybeWithExtends = maybeWithExtends.substring(0, extendsIndex).strip();

		String maybeWithImplements = maybeWithExtends.strip();
		final int implementsIndex = maybeWithImplements.indexOf("implements");
		if (implementsIndex >= 0) maybeWithImplements = maybeWithImplements.substring(0, implementsIndex).strip();

		String beforeMaybeParams = maybeWithImplements.strip();
		String recordFields = "";
		if (maybeWithImplements.endsWith(")")) {
			final String slice = maybeWithImplements.substring(0, maybeWithImplements.length() - 1);
			final int beforeParams = slice.indexOf("(");
			if (beforeParams >= 0) {
				beforeMaybeParams = slice.substring(0, beforeParams).strip();
				final String substring = slice.substring(beforeParams + 1);
				recordFields = compileValues(substring, Main::compileParameter, "");
			}
		}

		String name = beforeMaybeParams.strip();
		List<String> typeParameters = Collections.emptyList();
		if (beforeMaybeParams.endsWith(">")) {
			final String withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
			final int i1 = withoutEnd.indexOf("<");
			if (i1 >= 0) {
				name = withoutEnd.substring(0, i1).strip();
				final String arguments = withoutEnd.substring(i1 + "<".length());
				typeParameters = divide(arguments, Main::foldValue).map(String::strip).toList();
			}
		}

		final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();

		if (!afterContent.endsWith("}")) return Optional.empty();
		final String content = afterContent.substring(0, afterContent.length() - "}".length());

		final List<String> segments = divide(content, Main::foldStatement).toList();

		StringBuilder inner = new StringBuilder();
		ParseState outer = state;
		int j = 0;
		while (j < segments.size()) {
			String segment = segments.get(j);
			Tuple<String, ParseState> compiled = compileClassSegment(segment, name, outer);
			inner.append(compiled.left);
			outer = compiled.right;
			j++;
		}

		String templateString;
		if (typeParameters.isEmpty()) templateString = "";
		else {
			final String collect =
					typeParameters.stream().map(slice -> "typeparam " + slice).collect(Collectors.joining(", ", "<", ">"));

			final String templateValues = collect + System.lineSeparator();
			templateString = "template " + templateValues;
		}

		String generatedSubStructs = "";
		if (!variants.isEmpty()) {
			final String enumFields =
					variants.stream().map(slice -> generateIndent(1) + slice).collect(Collectors.joining(","));

			final String joinedTypeParameters;
			if (typeParameters.isEmpty()) joinedTypeParameters = "";
			else joinedTypeParameters = "<" + String.join(", ", typeParameters) + ">";

			final String unionFields = variants.stream().map(slice -> slice + joinedTypeParameters + " " +
																																slice.toLowerCase()).map(content1 -> generateStatement(
					content1,
					1)).collect(Collectors.joining());

			generatedSubStructs =
					"enum " + name + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() +
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			recordFields += generateStatement(name + "Tag tag", 1);
			recordFields += generateStatement(name + "Data" + joinedTypeParameters + " data", 1);
		}

		final String generated =
				generatedSubStructs + templateString + "struct " + name + " {" + recordFields + inner + System.lineSeparator() +
				"};" + System.lineSeparator();
		return Optional.of(new Tuple<String, ParseState>("", outer.addStruct(generated)));
	}

	private static String compileValues(String input, Function<String, String> mapper) {
		return compileValues(input, mapper, ", ");
	}

	private static String compileValues(String input, Function<String, String> mapper, String delimiter) {
		return divide(input, Main::foldValue).map(mapper).collect(Collectors.joining(delimiter));
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

	private static DivideState foldValue(DivideState state, char next) {
		if (next == ',' && state.isLevel()) return state.advance();
		final DivideState appended = state.append(next);
		if (next == '-') {
			final Optional<Character> peeked = appended.peek();
			if (peeked.isPresent() && peeked.get().equals('>')) return appended.popAndAppendToOption().orElse(appended);
		}

		if (next == '(' || next == '<') return appended.enter();
		if (next == ')' || next == '>') return appended.exit();
		return appended;
	}

	private static Tuple<String, ParseState> compileClassSegment(String input, String name, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) return new Tuple<String, ParseState>("", state);
		return compileClassSegmentValue(stripped, name, state);
	}

	private static Tuple<String, ParseState> compileClassSegmentValue(String input, String name, ParseState state) {
		if (input.isEmpty()) return new Tuple<String, ParseState>("", state);

		return compileStructure(input, "class", state).or(() -> compileStructure(input,
																																						 "record",
																																						 state)).or(() -> compileStructure(input,
																																																							 "interface",
																																																							 state)).or(
				() -> compileField(input, state)).or(() -> compileMethod(input, name, state)).orElseGet(() -> {
			final String generated = generateSegment(wrap(input), 1);
			return new Tuple<String, ParseState>(generated, state);
		});
	}

	private static Optional<Tuple<String, ParseState>> compileMethod(String input, String name, ParseState state) {
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
		ParseState current = state;
		if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) outputBodyWithBraces = ";";
		else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String inputBody = withBraces.substring(1, withBraces.length() - 1);

			StringJoiner joiner = new StringJoiner("");
			List<String> list = divide(inputBody, Main::foldStatement).toList();
			int i = 0;
			while (i < list.size()) {
				String s = list.get(i);
				Tuple<String, ParseState> string = compileMethodSegment(s, 1, current);
				joiner.add(string.left);
				current = string.right;
				i++;
			}

			final String compiledBody = joiner.toString();

			String outputBody;
			if (Objects.requireNonNull(methodHeader) instanceof JConstructor)
				outputBody = generateStatement(name + " this", 1) + compiledBody + generateStatement("return this", 1);
			else outputBody = compiledBody;

			outputBodyWithBraces = "{" + outputBody + System.lineSeparator() + "}";
		} else return Optional.empty();

		final String generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
		return Optional.of(new Tuple<String, ParseState>("", current.addFunction(generated)));
	}

	private static boolean isPlatformDependentMethod(JMethodHeader methodHeader) {
		return methodHeader instanceof Definition definition && definition.annotations.contains("Actual");
	}

	private static Definable transformMethodHeader(JMethodHeader methodHeader, String name) {
		return switch (methodHeader) {
			case JConstructor constructor ->
					new Definition(Collections.emptyList(), constructor.name, "new_" + constructor.name);
			case Definition definition ->
					new Definition(Collections.emptyList(), definition.type, definition.name + "_" + name);
			case Placeholder placeholder -> placeholder;
		};
	}

	private static JMethodHeader compileMethodHeader(String beforeParams) {
		return compileDefinition(beforeParams).<JMethodHeader>map(definable -> definable).or(() -> compileConstructor(
				beforeParams)).orElseGet(() -> new Placeholder(beforeParams));
	}

	private static String compileParameters(String input) {
		if (input.isEmpty()) return "";
		return compileValues(input, slice -> compileDefinition(slice).map(Definable::generate).orElse(""));
	}

	private static Tuple<String, ParseState> compileMethodSegment(String input, int depth, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) return new Tuple<String, ParseState>("", state);

		final Tuple<String, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
		return new Tuple<String, ParseState>(generateSegment(tuple.left, depth), tuple.right);
	}

	private static Tuple<String, ParseState> compileMethodSegmentValue(String input, int depth, ParseState state) {
		final String stripped = input.strip();
		final Optional<Tuple<String, ParseState>> compiled = compileBlock(state, stripped, depth);
		if (compiled.isPresent()) return compiled.get();

		final Optional<Tuple<String, ParseState>> maybeIf = compileConditional("if", depth, state, stripped);
		if (maybeIf.isPresent()) return maybeIf.get();

		final Optional<Tuple<String, ParseState>> maybeWhile = compileConditional("while", depth, state, stripped);
		if (maybeWhile.isPresent()) return maybeWhile.get();

		if (stripped.startsWith("else")) {
			final String substring = stripped.substring("else".length());
			final Tuple<String, ParseState> result = compileMethodSegmentValue(substring, depth, state);
			return new Tuple<String, ParseState>("else " + result.left, result.right);
		}

		if (stripped.endsWith(";")) {
			final String slice = stripped.substring(0, stripped.length() - 1);
			final Tuple<String, ParseState> result = compileMethodStatementValue(slice, state);
			return new Tuple<String, ParseState>(result.left + ";", result.right);
		}

		return new Tuple<String, ParseState>(wrap(stripped), state);
	}

	private static Optional<Tuple<String, ParseState>> compileConditional(String type,
																																				int depth,
																																				ParseState state,
																																				String stripped) {
		if (!stripped.startsWith(type)) return Optional.empty();
		final String withoutPrefix = stripped.substring(type.length());

		final List<String> conditionEnd = divide(withoutPrefix, Main::foldConditionEnd).toList();
		if (conditionEnd.size() < 2) return Optional.empty();
		final String withConditionEnd = conditionEnd.getFirst();
		final String substring1 = withConditionEnd.substring(0, withConditionEnd.length() - 1).strip();
		final String body = String.join("", conditionEnd.subList(1, conditionEnd.size()));

		if (!substring1.startsWith("(")) return Optional.empty();
		final String expression = substring1.substring(1);

		final Tuple<String, ParseState> condition = compileExpression(expression, state);
		final Tuple<String, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
		return Optional.of(new Tuple<String, ParseState>(type + " (" + condition.left + ") " + compiledBody.left,
																										 compiledBody.right));
	}

	private static Optional<Tuple<String, ParseState>> compileBlock(ParseState state, String input, int depth) {
		if (!input.startsWith("{") || !input.endsWith("}")) return Optional.empty();
		final String substring = input.substring(1, input.length() - 1);

		StringJoiner joiner = new StringJoiner("");
		ParseState current = state;
		List<String> list = divide(substring, Main::foldStatement).toList();
		int i = 0;
		while (i < list.size()) {
			String s = list.get(i);
			Tuple<String, ParseState> string = compileMethodSegment(s, depth + 1, current);
			joiner.add(string.left);
			current = string.right;
			i++;
		}

		final ArrayList<String> removed = current.popStatements();
		final String compiled = String.join("", removed) + joiner;
		return Optional.of(new Tuple<String, ParseState>("{" + compiled + generateIndent(depth) + "}", current));

	}

	private static DivideState foldConditionEnd(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == ')') {
			final DivideState exited = appended.exit();
			if (exited.isLevel()) return exited.advance();
		}

		if (c == '(') return appended.enter();
		return appended;
	}

	private static Tuple<String, ParseState> compileMethodStatementValue(String input, ParseState state) {
		if (input.startsWith("return ")) {
			final String substring = input.substring("return ".length());
			final Tuple<String, ParseState> result = compileExpression(substring, state);
			return new Tuple<String, ParseState>("return " + result.left, result.right);
		}

		if (input.endsWith("++")) {
			final String slice = input.substring(0, input.length() - 2);
			final Optional<Tuple<String, ParseState>> temp = tryCompileExpression(slice, state);
			if (temp.isPresent()) {
				final Tuple<String, ParseState> result = temp.get();
				return new Tuple<String, ParseState>(result.left + "++", result.right);
			}
		}

		final Optional<Tuple<String, ParseState>> invokableResult = compileInvokable(state, input);
		if (invokableResult.isPresent()) return invokableResult.get();

		final int i = input.indexOf("=");
		if (i >= 0) {
			final String destinationString = input.substring(0, i);
			final String source = input.substring(i + 1);
			final Tuple<String, ParseState> destinationResult =
					compileDefinition(destinationString).map(Definition::generate).map(generated -> new Tuple<String, ParseState>(
							generated,
							state)).orElseGet(() -> compileExpression(destinationString, state));

			final Tuple<String, ParseState> sourceResult = compileExpression(source, destinationResult.right);
			return new Tuple<String, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
		}

		return compileDefinition(input).map(value -> new Tuple<String, ParseState>(value.generate(),
																																							 state)).orElseGet(() -> new Tuple<String, ParseState>(
				wrap(input),
				state));
	}

	private static Tuple<String, ParseState> compileExpression(String input, ParseState state) {
		return tryCompileExpression(input, state).orElseGet(() -> new Tuple<String, ParseState>(wrap(input), state));
	}

	private static Optional<Tuple<String, ParseState>> tryCompileExpression(String input, ParseState state) {
		final String stripped = input.strip();
		if (stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <= 4)
			return Optional.of(new Tuple<String, ParseState>(stripped, state));

		if (isString(stripped)) return Optional.of(new Tuple<String, ParseState>(stripped, state));

		if (stripped.startsWith("!")) {
			final String slice = stripped.substring(1);
			final Optional<Tuple<String, ParseState>> maybeResult = tryCompileExpression(slice, state);
			if (maybeResult.isPresent()) {
				final Tuple<String, ParseState> result = maybeResult.get();
				return Optional.of(new Tuple<String, ParseState>("!" + result.left, result.right));
			}
		}

		final Optional<Tuple<String, ParseState>> lambdaResult = compileLambda(state, stripped);
		if (lambdaResult.isPresent()) return lambdaResult;

		final int i1 = stripped.indexOf("instanceof");
		if (i1 >= 0) {
			final String substring = stripped.substring(0, i1).strip();
			String afterOperator = stripped.substring(i1 + "instanceof".length()).strip();
			final Optional<Tuple<String, ParseState>> maybeResult = tryCompileExpression(substring, state);
			if (maybeResult.isPresent()) {
				final Tuple<String, ParseState> result = maybeResult.get();

				final int typeArgumentsStart = afterOperator.indexOf("<");
				String variantName;
				if (typeArgumentsStart >= 0) variantName = afterOperator.substring(0, typeArgumentsStart);
				else variantName = afterOperator;

				String parameters = "";
				if (afterOperator.endsWith(")")) {
					final String slice = afterOperator.substring(0, afterOperator.length() - 1);
					final int paramStart = slice.indexOf("(");
					if (paramStart >= 0) {
						final String paramString = slice.substring(paramStart + 1);
						String result1 = "";
						if (!paramString.isEmpty())
							result1 = compileValues(paramString, slice1 -> compileDefinition(slice1).map(definition -> {
								final String generated = definition.generate();
								return generated + " = _cast." + definition.name;
							}).map(destructMember -> generateStatement(destructMember, 2)).orElse(""));

						parameters = result1;
						afterOperator = afterOperator.substring(0, paramStart);
					}
				}

				final String left = result.left;
				final String content = afterOperator + " _cast = " + left + ".data." + variantName.toLowerCase();
				final String statement = generateStatement(content, 2) + parameters;
				final ParseState parseState = result.right.addStatement(statement);
				return Optional.of(new Tuple<>(left + ".tag == " + variantName, parseState));
			}
		}

		final Optional<Tuple<String, ParseState>> left = compileInvokable(state, stripped);
		if (left.isPresent()) return left;

		final int separator = stripped.lastIndexOf("::");
		if (separator >= 0) {
			final String substring = stripped.substring(0, separator);
			final String name = stripped.substring(separator + 2).strip();
			if (isIdentifier(name)) {
				final Optional<String> maybeResult = compileType(substring);
				if (maybeResult.isPresent()) {
					final String result = maybeResult.get();
					return Optional.of(new Tuple<String, ParseState>(name + "_" + result, state));
				}
			}
		}

		final int i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final String substring = stripped.substring(0, i);
			final String name = stripped.substring(i + 1).strip();
			if (isIdentifier(name)) {
				final Optional<Tuple<String, ParseState>> maybeResult = tryCompileExpression(substring, state);
				if (maybeResult.isPresent()) {
					final Tuple<String, ParseState> result = maybeResult.get();
					return Optional.of(new Tuple<String, ParseState>(result.left + "." + name, result.right));
				}
			}
		}

		return compileOperator(stripped, "+", state).or(() -> compileOperator(stripped,
																																					"-",
																																					state)).or(() -> compileOperator(stripped,
																																																					 ">=",
																																																					 state)).or(() -> compileOperator(
				stripped,
				"<",
				state)).or(() -> compileOperator(stripped, "!=", state)).or(() -> compileOperator(stripped,
																																													"==",
																																													state)).or(() -> compileOperator(
				stripped,
				"&&",
				state)).or(() -> compileOperator(stripped, "||", state)).or(() -> compileIdentifier(stripped,
																																														state)).or(() -> compileNumber(
				stripped,
				state));
	}

	private static Optional<Tuple<String, ParseState>> compileInvokable(ParseState state, String stripped) {
		if (!stripped.endsWith(")")) return Optional.empty();
		final String slice = stripped.substring(0, stripped.length() - 1);

		final List<String> segments = findArgStart(slice).toList();
		if (segments.size() < 2) return Optional.empty();
		final String callerWithExt = String.join("", segments.subList(0, segments.size() - 1));

		if (!callerWithExt.endsWith("(")) return Optional.empty();
		final String caller = callerWithExt.substring(0, callerWithExt.length() - 1);
		final String arguments = segments.getLast();

		final Optional<Tuple<String, ParseState>> maybeCallerResult = compileCaller(state, caller);
		if (maybeCallerResult.isEmpty()) return Optional.empty();

		final Tuple<String, ParseState> callerResult = maybeCallerResult.get();
		final Tuple<StringJoiner, ParseState> reduce = divide(arguments,
																													Main::foldValue).toList().stream().reduce(new Tuple<StringJoiner, ParseState>(
				new StringJoiner(", "),
				callerResult.right), (tuple, s) -> mergeExpression(tuple.left, tuple.right, s), (_, next) -> next);
		final String collect = reduce.left.toString();
		return Optional.of(new Tuple<String, ParseState>(callerResult.left + "(" + collect + ")", reduce.right));
	}

	private static Tuple<StringJoiner, ParseState> mergeExpression(StringJoiner joiner,
																																 ParseState state,
																																 String segment) {
		Tuple<String, ParseState> result = compileExpression(segment, state);
		final StringJoiner add = joiner.add(result.left);
		return new Tuple<StringJoiner, ParseState>(add, result.right);
	}

	private static Stream<String> findArgStart(String input) {
		return divide(input, (state, c) -> {
			final DivideState appended = state.append(c);
			if (c == '(') {
				final DivideState entered = appended.enter();
				if (entered.isShallow()) return entered.advance();
				else return entered;
			}
			if (c == ')') return appended.exit();
			return appended;
		});
	}

	private static Optional<Tuple<String, ParseState>> compileLambda(ParseState state, String stripped) {
		final int i1 = stripped.indexOf("->");
		if (i1 < 0) return Optional.empty();

		final String beforeArrow = stripped.substring(0, i1).strip();

		final String outputParams;
		if (isIdentifier(beforeArrow)) outputParams = "auto " + beforeArrow;
		else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
			final String withoutParentheses = beforeArrow.substring(1, beforeArrow.length() - 1);
			outputParams =
					Arrays.stream(withoutParentheses.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).map(
							slice -> "auto " + slice).collect(Collectors.joining(", "));

		} else return Optional.empty();

		final String body = stripped.substring(i1 + 2).strip();
		final Tuple<String, ParseState> bodyResult = compileLambdaBody(state, body);

		final String generatedName = bodyResult.right.generateAnonymousFunctionName();
		final String s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left + System.lineSeparator();
		return Optional.of(new Tuple<String, ParseState>(generatedName, bodyResult.right.addFunction(s1)));
	}

	private static Tuple<String, ParseState> compileLambdaBody(ParseState state, String body) {
		final Optional<Tuple<String, ParseState>> maybeBlock = compileBlock(state, body, 0);
		if (maybeBlock.isPresent()) return maybeBlock.get();

		final Tuple<String, ParseState> result = compileExpression(body, state);
		final String s = generateStatement("return " + result.left, 1);
		final String s2 = "{" + s + generateIndent(0) + "}";
		return new Tuple<String, ParseState>(s2, result.right);
	}

	private static Optional<Tuple<String, ParseState>> compileCaller(ParseState state, String caller) {
		if (caller.startsWith("new ")) {
			final Optional<String> newType = compileType(caller.substring("new ".length()));
			if (newType.isPresent()) return Optional.of(new Tuple<String, ParseState>("new_" + newType.get(), state));
		}

		return tryCompileExpression(caller, state);
	}

	private static Optional<Tuple<String, ParseState>> compileIdentifier(String stripped, ParseState state) {
		if (isIdentifier(stripped)) return Optional.of(new Tuple<String, ParseState>(stripped, state));
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileNumber(String stripped, ParseState state) {
		if (isNumber(stripped)) return Optional.of(new Tuple<String, ParseState>(stripped, state));
		return Optional.empty();
	}

	private static Optional<Tuple<String, ParseState>> compileOperator(String input, String operator, ParseState state) {
		final List<String> segments = divide(input, (state1, next) -> foldOperator(operator, state1, next)).toList();

		if (segments.size() < 2) return Optional.empty();

		final String left = segments.getFirst();
		final String right = String.join(operator, segments.subList(1, segments.size()));

		final Optional<Tuple<String, ParseState>> maybeLeftResult = tryCompileExpression(left, state);

		if (maybeLeftResult.isEmpty()) return Optional.empty();
		final Tuple<String, ParseState> leftResult = maybeLeftResult.get();

		final Optional<Tuple<String, ParseState>> maybeRightResult = tryCompileExpression(right, leftResult.right);
		if (maybeRightResult.isEmpty()) return Optional.empty();
		final Tuple<String, ParseState> rightResult = maybeRightResult.get();

		final String generated = leftResult.left + " " + operator + " " + rightResult.left;
		return Optional.of(new Tuple<String, ParseState>(generated, rightResult.right));
	}

	private static DivideState foldOperator(String operator, DivideState state1, Character next) {
		if (next != operator.charAt(0)) return state1.append(next);

		final Optional<Character> peeked = state1.peek();
		if (operator.length() >= 2 && peeked.isPresent() && peeked.get() == operator.charAt(1))
			return state1.pop().map(inner -> inner.left).orElse(state1).advance();

		return state1.advance();
	}

	private static boolean isString(String stripped) {
		if (stripped.length() < 2) return false;

		final boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
		if (!hasDoubleQuotes) return false;

		final String content = stripped.substring(1, stripped.length() - 1);
		return areAllDoubleQuotesEscaped(content);
	}

	private static boolean areAllDoubleQuotesEscaped(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> {
			final char c = input.charAt(i);
			if (c != '\"') return true;
			if (i == 0) return false;
			char previous = input.charAt(i - 1);
			return previous == '\\';
		});
	}

	private static boolean isNumber(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> Character.isDigit(input.charAt(i)));
	}

	private static boolean isIdentifier(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> {
			final char next = input.charAt(i);
			final boolean isValidDigit = i != 0 && Character.isDigit(next);
			return Character.isLetter(next) || isValidDigit;
		});
	}

	private static Optional<JMethodHeader> compileConstructor(String beforeParams) {
		final int separator = beforeParams.lastIndexOf(" ");
		if (separator < 0) return Optional.empty();

		final String name = beforeParams.substring(separator + " ".length());
		return Optional.of(new JConstructor(name));
	}

	private static Optional<Tuple<String, ParseState>> compileField(String input, ParseState state) {
		if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			final Optional<String> s = generateField(substring);
			if (s.isPresent()) return Optional.of(new Tuple<String, ParseState>(s.get(), state));
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

		final List<String> segments = divide(beforeName, Main::foldTypeSeparator).toList();
		if (segments.size() < 2)
			return compileType(beforeName).map(type -> new Definition(Collections.emptyList(), type, name));

		final String withoutLast = String.join(" ", segments.subList(0, segments.size() - 1));
		final List<String> annotations = findAnnotations(withoutLast);

		final String typeString = segments.getLast();
		return compileType(typeString).map(type -> new Definition(annotations, type, name));
	}

	private static List<String> findAnnotations(String withoutLast) {
		final int i = withoutLast.lastIndexOf("\n");
		if (i < 0) return Collections.emptyList();

		final String[] slices = withoutLast.substring(0, i).strip().split(Pattern.quote("\n"));
		return Arrays.stream(slices).map(String::strip).filter(slice -> slice.startsWith("@")).map(slice -> slice.substring(
				1)).toList();
	}

	private static DivideState foldTypeSeparator(DivideState state, Character c) {
		if (c == ' ' && state.isLevel()) return state.advance();

		final DivideState appended = state.append(c);
		if (c == '<') return appended.enter();
		if (c == '>') return appended.exit();
		return appended;
	}

	private static Optional<String> compileType(String input) {
		final String stripped = input.strip();
		if (stripped.equals("public")) return Optional.empty();

		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String argumentsString = withoutEnd.substring(argumentStart + "<".length());

				final String arguments =
						compileValues(argumentsString, slice -> compileType(slice).orElseGet(() -> wrap(slice)));
				return Optional.of(base + "<" + arguments + ">");
			}
		}

		if (stripped.endsWith("[]")) {
			final String slice = stripped.substring(0, stripped.length() - 2);
			return compileType(slice).map(result -> result + "*");
		}

		if (stripped.equals("String")) return Optional.of("char*");
		if (stripped.equals("int")) return Optional.of("int");

		if (isIdentifier(stripped)) return Optional.of(stripped);
		return Optional.of(wrap(stripped));
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
