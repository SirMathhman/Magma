package magma;

import magma.Collections.ArrayList;
import magma.Collectors.Joiner;
import magma.Collectors.ListCollector;
import magma.Collectors.ResultCollector;
import magma.Functions.BiFunction;
import magma.Functions.Function;
import magma.IO.IOError;
import magma.IO.Path;
import magma.JavaImpl.JIOError;
import magma.JavaImpl.JavaPath;
import magma.JavaImpl.Paths;
import magma.Options.None;
import magma.Options.Option;
import magma.Options.Some;
import magma.Results.Err;
import magma.Results.Ok;
import magma.Results.Result;
import magma.Streams.Stream;

import java.io.IOException;
import java.util.Objects;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {

	private sealed interface Definable extends JMethodHeader permits Definition, Placeholder {
		String generate();
	}

	private sealed interface JMethodHeader permits JConstructor, Definable {}

	private sealed interface CExpression permits CIdentifier, Content {
		String generate();
	}

	private static class ParseState {
		private final Stack<ArrayList<String>> beforeStatements;
		public ArrayList<String> beforeStructs;
		private ArrayList<String> structFields;
		private ArrayList<String> afterStatements;
		private ArrayList<String> structs;
		private ArrayList<String> functions;
		private int counter;
		private ArrayList<String> includes;
		private ArrayList<String> functionDeclarations = new ArrayList<String>();

		public ParseState() {
			this.functions = new ArrayList<String>();
			this.structs = new ArrayList<String>();

			this.beforeStatements = new Stack<ArrayList<String>>();
			this.beforeStatements.add(new ArrayList<String>());

			this.afterStatements = new ArrayList<String>();
			this.counter = -1;
			this.includes = new ArrayList<String>();
			this.beforeStructs = new ArrayList<String>();
			this.structFields = new ArrayList<String>();
		}

		public ParseState addFunction(String func) {
			this.functions = this.functions.addLast(func);
			return this;
		}

		public ParseState addStruct(String struct) {
			this.structs = this.structs.addLast(struct);
			return this;
		}

		public String generateAnonymousFunctionName() {
			this.counter++;
			return "__lambda" + this.counter + "__";
		}

		public ParseState addAfterStatement(String statement) {
			this.afterStatements = this.afterStatements.addLast(statement);
			return this;
		}

		public ArrayList<String> popAfterStatements() {
			final ArrayList<String> copy = this.afterStatements.copy();
			this.afterStatements = this.afterStatements.clear();
			return copy;
		}

		public void addBeforeStatement(String beforeStatement) {
			final ArrayList<String> peek = this.beforeStatements.pop();
			final ArrayList<String> added = peek.addLast(beforeStatement);
			this.beforeStatements.push(added);
		}

		public ArrayList<String> popBeforeStatements() {
			return this.beforeStatements.pop();
		}

		public ParseState pushBeforeStatements() {
			this.beforeStatements.push(new ArrayList<String>());
			return this;
		}

		public ParseState addIncludes(String include) {
			if (!this.includes.contains(include)) {
				this.includes = this.includes.addLast(include);
			}
			return this;
		}

		public ParseState addBeforeStruct(String beforeStruct) {
			this.beforeStructs = this.beforeStructs.addLast(beforeStruct);
			return this;
		}

		public ParseState addStructField(String field) {
			this.structFields = this.structFields.addLast(field);
			return this;
		}

		public ArrayList<String> popStructFields() {
			final ArrayList<String> copy = this.structFields.copy();
			this.structFields = this.structFields.clear();
			return copy;
		}

		public ParseState addFunctionDeclaration(String functionDeclaration) {
			this.functionDeclarations = this.functionDeclarations.addLast(functionDeclaration);
			return this;
		}
	}

	private static class DivideState {
		private final String input;
		private ArrayList<String> segments;
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
			this.segments = this.segments.addLast(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		public Option<Tuple<DivideState, Character>> pop() {
			if (this.index >= this.input.length()) {
				return new None<Tuple<DivideState, Character>>();
			}
			final char next = this.input.charAt(this.index);
			this.index++;
			return new Some<Tuple<DivideState, Character>>(new Tuple<DivideState, Character>(this, next));
		}

		public Option<Tuple<DivideState, Character>> popAndAppendToTuple() {
			return this.pop().map(tuple -> new Tuple<DivideState, Character>(tuple.left.append(tuple.right), tuple.right));
		}

		public Option<DivideState> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}

		public Option<Character> peek() {
			if (this.index < this.input.length()) {
				return new Some<Character>(this.input.charAt(this.index));
			} else {
				return new None<Character>();
			}
		}
	}

	public record Tuple<A, B>(A left, B right) {}

	private record Definition(ArrayList<String> annotations, String type, String name) implements Definable {
		public Definition(String type, String name) {
			this(new ArrayList<String>(), type, name);
		}

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

	private record Content(String value) implements CExpression {
		@Override
		public String generate() {
			return this.value;
		}
	}

	private record CIdentifier(String value) implements CExpression {
		@Override
		public String generate() {
			return this.value;
		}
	}

	private record Location(ArrayList<String> namespace, String name) {}

	private record Struct(ArrayList<String> typeParameters, String name, Option<String> maybeFields) {
		private String generate() {
			final String s = this.maybeFields.map(fields -> " {" + fields + System.lineSeparator() + "}").orElse("");
			return generateTemplateString(this.typeParameters) + "struct " + this.name() + s + ";" + System.lineSeparator();
		}
	}

	public static void main(String[] args) {
		if (run() instanceof Some<IOError>(IOError value)) {
			System.out.println(value.display());
		}
	}

	private static Option<IOError> run() {
		final Path sourceDirectory = Paths.get(".", "src", "main", "java");
		final Path targetDirectory = Paths.get(".", "src", "main", "windows");

		final Result<ArrayList<Path>, IOError> walked = sourceDirectory.walk();
		return switch (walked) {
			case Err<ArrayList<Path>, IOError> v -> new Some<IOError>(v.error());
			case Ok<ArrayList<Path>, IOError> v -> {
				final Result<ArrayList<ArrayList<Path>>, IOError> result =
						runWithSources(v.value(), sourceDirectory, targetDirectory);
				yield switch (result) {
					case Err<ArrayList<ArrayList<Path>>, IOError> v1 -> new Some<IOError>(v1.error());
					case Ok<ArrayList<ArrayList<Path>>, IOError> v1 -> switch (writeBuildFile(v1, targetDirectory)) {
						case Err<Path, IOError> v2 -> new Some<IOError>(v2.error());
						case Ok<Path, IOError> v2 -> runBuildFile(targetDirectory, v2.value());
					};
				};
			}
		};
	}

	private static Option<IOError> runBuildFile(Path targetDirectory, Path path) {
		try {
			final ProcessBuilder builder =
					new ProcessBuilder("cmd.exe", "/c", targetDirectory.relativize(path).asString()).directory(JavaPath
																																																				 .unwrap(
																																																						 targetDirectory)
																																																				 .toFile());

			// Use the parent's console so the spawned process' stdout/stderr show up
			builder.inheritIO();
			final Process proc = builder.start();
			proc.waitFor();

			return new None<IOError>();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new Some<IOError>(new JIOError(new IOException(e)));
		} catch (IOException e) {
			return new Some<IOError>(new JIOError(e));
		}
	}

	private static Result<Path, IOError> writeBuildFile(Ok<ArrayList<ArrayList<Path>>, IOError> v1,
																											Path targetDirectory) {
		final ArrayList<Path> list = v1.value().stream().flatMap(ArrayList::stream).collect(new ListCollector<Path>());

		final Path path = targetDirectory.resolveByString("build.bat");
		final String joined = list
				.stream()
				.map(targetDirectory::relativize)
				.map(Path::asString)
				.map(slice -> slice + "^" + System.lineSeparator() + "\t")
				.collect(new Joiner(" "));
		return switch (path.writeString("clang " + joined + " -o magmac.exe")) {
			case None<IOError> _ -> new Ok<Path, IOError>(path);
			case Some<IOError> v -> new Err<Path, IOError>(v.value());
		};
	}

	private static Result<ArrayList<ArrayList<Path>>, IOError> runWithSources(ArrayList<Path> sources,
																																						Path sourceDirectory,
																																						Path targetDirectory) {
		return compileSources(sources, sourceDirectory, targetDirectory);
	}

	private static Result<ArrayList<ArrayList<Path>>, IOError> compileSources(ArrayList<Path> sources,
																																						Path sourceDirectory,
																																						Path targetDirectory) {
		return sources
				.stream()
				.filter(path -> path.asString().endsWith(".java"))
				.map(source -> compileSource(source, sourceDirectory, targetDirectory))
				.collect(new ResultCollector<ArrayList<Path>, IOError, ArrayList<ArrayList<Path>>>(new ListCollector<ArrayList<Path>>()));
	}

	private static Result<ArrayList<Path>, IOError> compileSource(Path source,
																																Path sourceDirectory,
																																Path targetDirectory) {
		final Path relativeParent = sourceDirectory.relativize(source.getParent());
		final Path targetParent = targetDirectory.resolveByPath(relativeParent);

		final ArrayList<String> namespace = relativeParent.stream().collect(new ListCollector<String>());

		return switch (source.readString()) {
			case Ok<String, IOError>(String input) -> compileInput(source, input, targetParent, namespace);
			case Err<String, IOError>(IOError error) -> new Err<ArrayList<Path>, IOError>(error);
		};
	}

	private static Result<ArrayList<Path>, IOError> compileInput(Path source,
																															 String input,
																															 Path targetParent,
																															 ArrayList<String> namespace) {
		if (!targetParent.exists()) {
			final Option<IOError> result = targetParent.createDirectories();
			if (result instanceof Some<IOError>(IOError error)) {
				return new Err<ArrayList<Path>, IOError>(error);
			}
		}

		final String fileName = source.getFileName().asString();
		final int separator = fileName.lastIndexOf(".");
		final String name = fileName.substring(0, separator);

		final Tuple<String, String> compiled = compile(input, new Location(namespace, name));
		final String prefix =
				"// File generated from '" + source.asString() + "'. This is not source code!" + System.lineSeparator();

		final String defined = name.toUpperCase() + "_H";
		final String headerOutput =
				prefix + "#ifndef " + defined + System.lineSeparator() + "#define " + defined + System.lineSeparator() +
				compiled.left + "#endif";

		final String targetOutput = prefix + "#include \"Main.h\"" + System.lineSeparator() + compiled.right;

		final Path header = targetParent.resolveByString(name + ".h");
		final Path target = targetParent.resolveByString(name + ".cpp");
		final Option<IOError> maybeError = header.writeString(headerOutput).or(() -> target.writeString(targetOutput));
		if (maybeError instanceof Some<IOError>(IOError error)) {
			return new Err<ArrayList<Path>, IOError>(error);
		}

		return new Ok<ArrayList<Path>, IOError>(new ArrayList<Path>().addLast(target));
	}

	private static Tuple<String, String> compile(String input, Location location) {
		StringJoiner joiner = new StringJoiner("");
		ParseState state = new ParseState();
		ArrayList<String> list = divide(input, Main::foldStatement).collect(new ListCollector<String>());
		int i = 0;
		while (i < list.size()) {
			String input1 = list.get(i).orElse(null);
			Tuple<String, ParseState> s = compileRootSegment(input1, state, location);
			joiner.add(s.left);
			state = s.right;
			i++;
		}

		final String joined = joiner.toString();

		final String joinedIncludes = state.includes.stream().collect(new Joiner(""));

		final String joinedBeforeStructs = state.beforeStructs.stream().collect(new Joiner(""));
		final String joinedStructs = state.structs.stream().collect(new Joiner(""));
		final String joinedFunctionDeclarations = state.functionDeclarations.stream().collect(new Joiner(""));

		final String joinedFunctions = state.functions.stream().collect(new Joiner(""));

		final String generatedHeaderContent =
				joinedIncludes + joinedBeforeStructs + joinedStructs + joinedFunctionDeclarations;
		final String generatedSourceContent =
				joinedFunctions + joined + "int main(){" + System.lineSeparator() + "\t" + "main_Main();" +
				System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}";

		return new Tuple<String, String>(generatedHeaderContent, generatedSourceContent);
	}

	private static Stream<String> divide(String input, BiFunction<DivideState, Character, DivideState> folder) {
		Tuple<DivideState, Boolean> current = new Tuple<DivideState, Boolean>(new DivideState(input), true);
		while (current.right) {
			current = foldCycle(current.left, folder);
		}
		return current.left.advance().stream();
	}

	private static Tuple<DivideState, Boolean> foldCycle(DivideState state,
																											 BiFunction<DivideState, Character, DivideState> folder) {
		final Option<Tuple<DivideState, Character>> maybeNext = state.pop();
		if (maybeNext instanceof Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> value)) {
			return new Tuple<DivideState, Boolean>(foldEscaped(value.left, value.right, folder), true);
		}

		return new Tuple<DivideState, Boolean>(state, false);
	}

	private static DivideState foldEscaped(DivideState state,
																				 char next,
																				 BiFunction<DivideState, Character, DivideState> folder) {
		return foldSingleQuotes(state, next)
				.or(() -> foldDoubleQuotes(state, next))
				.orElseGet(() -> folder.apply(state, next));
	}

	private static Option<DivideState> foldSingleQuotes(DivideState state, char next) {
		if (next != '\'') {
			return new None<DivideState>();
		}

		final DivideState appended = state.append(next);
		return appended.popAndAppendToTuple().flatMap(Main::foldEscaped).flatMap(DivideState::popAndAppendToOption);
	}

	private static Option<DivideState> foldEscaped(Tuple<DivideState, Character> tuple) {
		if (tuple.right == '\\') {
			return tuple.left.popAndAppendToOption();
		} else {
			return new Some<DivideState>(tuple.left);
		}
	}

	private static Option<DivideState> foldDoubleQuotes(DivideState state, char next) {
		if (next != '\"') {
			return new None<DivideState>();
		}

		Tuple<DivideState, Boolean> current = new Tuple<DivideState, Boolean>(state.append(next), true);
		while (current.right) {
			current = foldUntilDoubleQuotes(current.left);
		}
		return new Some<DivideState>(current.left);
	}

	private static Tuple<DivideState, Boolean> foldUntilDoubleQuotes(DivideState state) {
		final Option<Tuple<DivideState, Character>> maybeNext = state.popAndAppendToTuple();
		if (!(maybeNext instanceof Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> value))) {
			return new Tuple<DivideState, Boolean>(state, false);
		}

		final DivideState nextState = value.left;
		final char nextChar = value.right;

		if (nextChar == '\\') {
			return new Tuple<DivideState, Boolean>(nextState.popAndAppendToOption().orElse(nextState), true);
		}
		if (nextChar == '\"') {
			return new Tuple<DivideState, Boolean>(nextState, false);
		}
		return new Tuple<DivideState, Boolean>(nextState, true);
	}

	private static DivideState foldStatement(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == ';' && appended.isLevel()) {
			return appended.advance();
		}
		if (c == '}' && appended.isShallow()) {
			final Option<Character> peeked = appended.peek();
			final DivideState withPeeked;
			if (peeked instanceof Some<Character>(Character value) && value == ';') {
				withPeeked = appended.popAndAppendToOption().orElse(appended);
			} else {
				withPeeked = appended;
			}
			return withPeeked.advance().exit();
		}
		if (c == '{' || c == '(') {
			return appended.enter();
		}
		if (c == '}' || c == ')') {
			return appended.exit();
		}
		return appended;
	}

	private static Tuple<String, ParseState> compileRootSegment(String input, ParseState state, Location location) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}
		if (stripped.startsWith("package ")) {
			return new Tuple<String, ParseState>("", state);
		}

		if (stripped.startsWith("import ") && stripped.endsWith(";")) {
			final String slice = stripped.substring("import ".length(), stripped.length() - 1);
			final String[] divisionArray = slice.split(Pattern.quote("."));
			final ArrayList<String> divisions = Streams.fromRef(divisionArray).collect(new ListCollector<String>());

			final String joined = Streams.fromLength(location.namespace.size()).map(_ -> "..").collect(new Joiner("/"));
			final ArrayList<String> segments = divisions.subList(0, divisions.size() - 1).orElse(new ArrayList<String>());
			final ParseState newState;
			if (segments.getFirst() instanceof Some<String>(String first) && first.startsWith("java")) {
				newState = state;
			} else {
				final String folded = segments.stream().foldWithInitial(joined, (string, string2) -> string + "/" + string2);

				newState = state.addIncludes("#include \"" + folded + ".h\"" + System.lineSeparator());
			}

			return new Tuple<String, ParseState>("", newState);
		}

		return compileStructure(stripped, "class", state).orElseGet(() -> new Tuple<String, ParseState>(wrap(stripped),
																																																		state));
	}

	private static Option<Tuple<String, ParseState>> compileStructure(String input, String type, ParseState state) {
		final int keywordIndex = input.indexOf(type + " ");
		if (keywordIndex < 0) {
			return new None<Tuple<String, ParseState>>();
		}

		final ArrayList<String> annotations = findAnnotations(input.substring(0, keywordIndex));
		if (annotations.contains("Actual")) {
			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("", state));
		}

		final String afterKeyword = input.substring(keywordIndex + (type + " ").length());
		final int contentStart = afterKeyword.indexOf("{");

		if (contentStart < 0) {
			return new None<Tuple<String, ParseState>>();
		}

		final String beforeContent = afterKeyword.substring(0, contentStart).strip();
		String withoutPermits = beforeContent;
		ArrayList<String> variants = new ArrayList<String>();

		final int permitsIndex = beforeContent.indexOf("permits");
		if (permitsIndex >= 0) {
			final String slice = beforeContent.substring(permitsIndex + "permits".length());
			variants = divide(slice, Main::foldValue)
					.map(String::strip)
					.filter(segment -> !segment.isEmpty())
					.collect(new ListCollector<String>());
			withoutPermits = beforeContent.substring(0, permitsIndex);
		}

		String maybeWithExtends = withoutPermits.strip();
		final int extendsIndex = maybeWithExtends.indexOf("extends");
		if (extendsIndex >= 0) {
			maybeWithExtends = maybeWithExtends.substring(0, extendsIndex).strip();
		}

		String maybeWithImplements = maybeWithExtends.strip();
		final int implementsIndex = maybeWithImplements.indexOf("implements");
		if (implementsIndex >= 0) {
			maybeWithImplements = maybeWithImplements.substring(0, implementsIndex).strip();
		}

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
		ArrayList<String> typeParameters = new ArrayList<String>();
		if (beforeMaybeParams.endsWith(">")) {
			final String withoutEnd = beforeMaybeParams.substring(0, beforeMaybeParams.length() - 1);
			final int i1 = withoutEnd.indexOf("<");
			if (i1 >= 0) {
				name = withoutEnd.substring(0, i1).strip();
				final String arguments = withoutEnd.substring(i1 + "<".length());
				typeParameters = divide(arguments, Main::foldValue).map(String::strip).collect(new ListCollector<String>());
			}
		}

		if (!isIdentifier(name)) {
			return new None<Tuple<String, ParseState>>();
		}

		final String afterContent = afterKeyword.substring(contentStart + "{".length()).strip();
		if (!afterContent.endsWith("}")) {
			return new None<Tuple<String, ParseState>>();
		}
		final String content = afterContent.substring(0, afterContent.length() - "}".length());

		final ArrayList<String> segments = divide(content, Main::foldStatement).collect(new ListCollector<String>());

		StringBuilder inner = new StringBuilder();
		ParseState outer = state;
		int j = 0;
		while (j < segments.size()) {
			String segment = segments.get(j).orElse(null);
			Tuple<String, ParseState> compiled = compileClassSegment(segment, name, outer, typeParameters);
			inner.append(compiled.left);
			outer = compiled.right;
			j++;
		}
		recordFields += inner;

		final String joinedTypeParameters;
		if (typeParameters.isEmpty()) {
			joinedTypeParameters = "";
		} else {
			joinedTypeParameters = "<" + typeParameters.stream().collect(new Joiner(", ")) + ">";
		}

		String generatedSubStructs = "";
		if (!variants.isEmpty()) {
			final String enumFields = variants.stream().map(slice -> generateIndent(1) + slice).collect(new Joiner(","));

			final String unionFields = variants
					.stream()
					.map(slice -> slice + joinedTypeParameters + " " + slice.toLowerCase())
					.map(content1 -> generateStatement(content1, 1))
					.collect(new Joiner());

			generatedSubStructs =
					"enum " + name + "Tag {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator() +
					generateTemplateString(typeParameters) + "union " + name + "Data {" + unionFields + System.lineSeparator() +
					"};" + System.lineSeparator();

			recordFields += generateStatement(name + "Tag tag", 1);
			recordFields += generateStatement(name + "Data" + joinedTypeParameters + " data", 1);
		} else if (type.equals("interface")) {
			final String vTableName = name + "VTable";

			final String functionDeclarations = outer.popStructFields().stream().collect(new Joiner());
			generatedSubStructs = new Struct(typeParameters, vTableName, new Some<String>(functionDeclarations)).generate();
			recordFields += generateStatement("void* data", 1);
			recordFields += generateStatement(vTableName + joinedTypeParameters + " vtable", 1);
		}

		final String generated =
				generatedSubStructs + new Struct(typeParameters, name, new Some<String>(recordFields)).generate();

		final ParseState parseState =
				outer.addBeforeStruct(new Struct(typeParameters, name, new None<String>()).generate()).addStruct(generated);
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("", parseState));
	}

	private static String generateTemplateString(ArrayList<String> typeParameters) {
		String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			final String collect =
					"<" + typeParameters.stream().map(slice -> "typename " + slice).collect(new Joiner(", ")) + ">";

			final String templateValues = collect + System.lineSeparator();
			templateString = "template " + templateValues;
		}
		return templateString;
	}

	private static String compileValues(String input, Function<String, String> mapper) {
		return compileValues(input, mapper, ", ");
	}

	private static String compileValues(String input, Function<String, String> mapper, String delimiter) {
		return divide(input, Main::foldValue).map(mapper).collect(new Joiner(delimiter));
	}

	private static String compileParameter(String input1) {
		if (input1.isEmpty()) {
			return "";
		}
		return generateField(input1).orElseGet(() -> wrap(input1));
	}

	private static Option<String> generateField(String input) {
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
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}
		final DivideState appended = state.append(next);
		if (next == '-') {
			final Option<Character> peeked = appended.peek();
			if (peeked instanceof Some<Character>(Character value)) {
				if (value.equals('>')) {
					return appended.popAndAppendToOption().orElse(appended);
				}
			}
		}

		if (next == '(' || next == '<') {
			return appended.enter();
		}
		if (next == ')' || next == '>') {
			return appended.exit();
		}
		return appended;
	}

	private static Tuple<String, ParseState> compileClassSegment(String input,
																															 String name,
																															 ParseState state,
																															 ArrayList<String> typeParams) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}
		return compileClassSegmentValue(stripped, name, state, typeParams);
	}

	private static Tuple<String, ParseState> compileClassSegmentValue(String input,
																																		String name,
																																		ParseState state,
																																		ArrayList<String> typeParams) {
		if (input.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}

		return compileStructure(input, "class", state)
				.or(() -> compileStructure(input, "record", state))
				.or(() -> compileStructure(input, "interface", state))
				.or(() -> compileField(input, state))
				.or(() -> compileMethod(input, name, state, typeParams))
				.orElseGet(() -> {
					final String generated = generateSegment(wrap(input), 1);
					return new Tuple<String, ParseState>(generated, state);
				});
	}

	private static Option<Tuple<String, ParseState>> compileMethod(String input,
																																 String structName,
																																 ParseState state,
																																 ArrayList<String> typeParams) {
		final int paramStart = input.indexOf("(");
		if (paramStart < 0) {
			return new None<Tuple<String, ParseState>>();
		}

		final String beforeParams = input.substring(0, paramStart).strip();
		final String withParams = input.substring(paramStart + 1);

		final int paramEnd = withParams.indexOf(")");
		if (paramEnd < 0) {
			return new None<Tuple<String, ParseState>>();
		}

		final JMethodHeader methodHeader = compileMethodHeader(beforeParams);
		final String inputParamString = withParams.substring(0, paramEnd);
		final String withBraces = withParams.substring(paramEnd + 1).strip();

		final ArrayList<Definition> params;
		if (!inputParamString.isEmpty()) {
			params = divide(inputParamString, Main::foldValue)
					.map(Main::compileDefinition)
					.flatMap(Streams::fromOption)
					.collect(new ListCollector<Definition>());
		} else {
			params = new ArrayList<Definition>();
		}

		final String templateString = generateTemplateString(typeParams);
		final ArrayList<Definition> outputParams = params.addFirst(new Definition("void*", "_ref"));
		String joinedOutputParams = outputParams.stream().map(Definition::generate).collect(new Joiner(", "));

		String field = switch (methodHeader) {
			case Definable definable -> switch (definable) {
				case Definition definition -> definition.type + " (*" + definition.name + ")";
				case Placeholder placeholder -> placeholder.generate();
			};
			case JConstructor _ -> "/* Constructors not allowed as interface methods */";
		};

		final String outputParamsString = "(" + joinedOutputParams + ")";
		final String outputMethodHeader =
				templateString + transformMethodHeader(methodHeader, structName).generate() + outputParamsString;

		if (withBraces.equals(";") || isPlatformDependentMethod(methodHeader)) {
			final String joinedTypes = outputParams.stream().map(Definition::type).collect(new Joiner(", "));

			final String functionDeclaration = generateStatement(field + "(" + joinedTypes + ")", 1);

			final ArrayList<String> paramNames = params.stream().map(Definition::name).collect(new ListCollector<String>());
			final ArrayList<String> stringArrayList =
					paramNames.subList(1, paramNames.size()).orElse(new ArrayList<String>()).addFirst("this.data");

			final String joinedArgs = stringArrayList.stream().collect(new Joiner(", "));
			final ParseState withFunctionDeclaration = state
					.addStructField(functionDeclaration)
					.addFunctionDeclaration(outputMethodHeader + ";" + System.lineSeparator())
					.addFunction(
							outputMethodHeader + "{" + generateStatement(structName + " this = *((" + structName + "*) _ref)", 1) +
							generateStatement("return this.vtable.apply(" + joinedArgs + ")", 1) + System.lineSeparator() + "}" +
							System.lineSeparator());

			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("", withFunctionDeclaration));
		} else if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
			final String inputBody = withBraces.substring(1, withBraces.length() - 1);
			final Tuple<ArrayList<String>, ParseState> compiledBody = compileMethodStatements(state, 0, inputBody);

			ArrayList<String> statements = compiledBody.left;
			if (Objects.requireNonNull(methodHeader) instanceof JConstructor) {
				ArrayList<String> stringArrayList = statements.addFirst(generateStatement(structName + " this", 1));
				statements = stringArrayList.addLast(generateStatement("return this", 1));
			}

			final String joined = statements.stream().collect(new Joiner(""));
			final String outputBodyWithBraces = "{" + joined + System.lineSeparator() + "}";

			final String generated = outputMethodHeader + outputBodyWithBraces + System.lineSeparator();
			final ParseState parseState = state.addFunction(generated);
			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("", parseState));
		} else {
			return new None<Tuple<String, ParseState>>();
		}
	}

	private static boolean isPlatformDependentMethod(JMethodHeader methodHeader) {
		return methodHeader instanceof Definition definition && definition.annotations.contains("Actual");
	}

	private static Definable transformMethodHeader(JMethodHeader methodHeader, String name) {
		if (Objects.requireNonNull(methodHeader) instanceof JConstructor(String name1)) {
			return new Definition(new ArrayList<String>(), name1, "new_" + name1);
		} else if (methodHeader instanceof Definition definition) {
			return new Definition(new ArrayList<String>(), definition.type, definition.name + "_" + name);
		} else if (methodHeader instanceof Placeholder placeholder) {
			return placeholder;
		} else {
			return new Placeholder("?");
		}
	}

	private static JMethodHeader compileMethodHeader(String beforeParams) {
		return compileDefinition(beforeParams)
				.<JMethodHeader>map(definable -> definable)
				.or(() -> compileConstructor(beforeParams))
				.orElseGet(() -> new Placeholder(beforeParams));
	}

	private static Tuple<String, ParseState> compileMethodSegment(String input, int depth, ParseState state) {
		final String stripped = input.strip();
		if (stripped.isEmpty()) {
			return new Tuple<String, ParseState>("", state);
		}

		final Tuple<String, ParseState> tuple = compileMethodSegmentValue(stripped, depth, state);
		return new Tuple<String, ParseState>(generateSegment(tuple.left, depth), tuple.right);
	}

	private static Tuple<String, ParseState> compileMethodSegmentValue(String input, int depth, ParseState state) {
		final String stripped = input.strip();
		final Option<Tuple<String, ParseState>> compiled = compileBlock(state, stripped, depth);
		if (compiled instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final Option<Tuple<String, ParseState>> maybeIf = compileConditional("if", depth, state, stripped);
		if (maybeIf instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final Option<Tuple<String, ParseState>> maybeWhile = compileConditional("while", depth, state, stripped);
		if (maybeWhile instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

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

	private static Option<Tuple<String, ParseState>> compileConditional(String type,
																																			int depth,
																																			ParseState state,
																																			String stripped) {
		if (!stripped.startsWith(type)) {
			return new None<Tuple<String, ParseState>>();
		}
		final String withoutPrefix = stripped.substring(type.length());

		final ArrayList<String> conditionEnd =
				divide(withoutPrefix, Main::foldConditionEnd).collect(new ListCollector<String>());
		if (conditionEnd.size() < 2) {
			return new None<Tuple<String, ParseState>>();
		}
		final String withConditionEnd = conditionEnd.getFirst().orElse(null);
		final String substring1 = withConditionEnd.substring(0, withConditionEnd.length() - 1).strip();
		final String body =
				conditionEnd.subList(1, conditionEnd.size()).orElse(new ArrayList<String>()).stream().collect(new Joiner(""));

		if (!substring1.startsWith("(")) {
			return new None<Tuple<String, ParseState>>();
		}
		final String expression = substring1.substring(1);

		final Tuple<String, ParseState> condition = compileExpression(expression, state);
		final Tuple<String, ParseState> compiledBody = compileMethodSegmentValue(body, depth, condition.right);
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(
				type + " (" + condition.left + ") " + compiledBody.left, compiledBody.right));
	}

	private static Option<Tuple<String, ParseState>> compileBlock(ParseState state, String input, int depth) {
		if (!input.startsWith("{") || !input.endsWith("}")) {
			return new None<Tuple<String, ParseState>>();
		}
		final Tuple<ArrayList<String>, ParseState> result =
				compileMethodStatements(state, depth, input.substring(1, input.length() - 1));
		final String generated = "{" + result.left().stream().collect(new Joiner("")) + generateIndent(depth) + "}";
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(generated, result.right()));
	}

	private static Tuple<ArrayList<String>, ParseState> compileMethodStatements(ParseState state,
																																							int depth,
																																							String content) {
		ArrayList<String> compiled = new ArrayList<String>();
		ParseState current = state;
		ArrayList<String> list = divide(content, Main::foldStatement).collect(new ListCollector<String>());
		int i = 0;
		while (i < list.size()) {
			String s = list.get(i).orElse(null);

			Tuple<String, ParseState> string = compileMethodSegment(s, depth + 1, current.pushBeforeStatements());
			compiled = compiled.addAll(string.right.popBeforeStatements()).addLast(string.left);
			current = string.right;
			i++;
		}

		final ArrayList<String> removed = current.popAfterStatements();
		compiled = compiled.addAllAt(0, removed).orElse(new ArrayList<String>());

		return new Tuple<ArrayList<String>, ParseState>(compiled, current);
	}

	private static DivideState foldConditionEnd(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == ')') {
			final DivideState exited = appended.exit();
			if (exited.isLevel()) {
				return exited.advance();
			}
		}

		if (c == '(') {
			return appended.enter();
		}
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
			final Option<Tuple<String, ParseState>> temp =
					tryCompileExpression(slice, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																												tuple.right));
			if (temp instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
				return new Tuple<String, ParseState>(value.left + "++", value.right);
			}
		}

		final Option<Tuple<String, ParseState>> invokableResult = compileInvokable(state, input);
		if (invokableResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final int i = input.indexOf("=");
		if (i >= 0) {
			final String destinationString = input.substring(0, i);
			final String source = input.substring(i + 1);
			final Tuple<String, ParseState> destinationResult = compileDefinition(destinationString)
					.map(Definition::generate)
					.map(generated -> new Tuple<String, ParseState>(generated, state))
					.orElseGet(() -> compileExpression(destinationString, state));

			final Tuple<String, ParseState> sourceResult = compileExpression(source, destinationResult.right);
			return new Tuple<String, ParseState>(destinationResult.left + " = " + sourceResult.left, sourceResult.right);
		}

		return compileDefinition(input)
				.map(value -> new Tuple<String, ParseState>(value.generate(), state))
				.orElseGet(() -> new Tuple<String, ParseState>(wrap(input), state));
	}

	private static Tuple<String, ParseState> compileExpression(String input, ParseState state) {
		return tryCompileExpression(input, state)
				.map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(), tuple.right))
				.orElseGet(() -> new Tuple<String, ParseState>(wrap(input), state));
	}

	private static Option<Tuple<CExpression, ParseState>> tryCompileExpression(String input, ParseState state) {
		final String stripped = input.strip();
		final Option<Tuple<String, ParseState>> charResult = compileChar(stripped, state);
		if (charResult instanceof Some<Tuple<String, ParseState>>) {
			return charResult.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> stringResult = compileString(stripped, state);
		if (stringResult instanceof Some<Tuple<String, ParseState>>) {
			return stringResult.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> notResult = compileNot(state, stripped);
		if (notResult instanceof Some<Tuple<String, ParseState>>) {
			return notResult.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> lambdaResult = compileLambda(state, stripped);
		if (lambdaResult instanceof Some<Tuple<String, ParseState>>) {
			return lambdaResult.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> instanceOfResult = compileInstanceOf(state, stripped);
		if (instanceOfResult instanceof Some<Tuple<String, ParseState>>) {
			return instanceOfResult.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> left = compileInvokable(state, stripped);
		if (left instanceof Some<Tuple<String, ParseState>>) {
			return left.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> methodReferenceResult = compileMethodReference(state, stripped);
		if (methodReferenceResult instanceof Some<Tuple<String, ParseState>>) {
			return methodReferenceResult.map(Main::wrapInContent);
		}

		final Option<Tuple<String, ParseState>> fieldAccessResult = compileFieldAccess(state, stripped);
		if (fieldAccessResult instanceof Some<Tuple<String, ParseState>>) {
			return fieldAccessResult.map(Main::wrapInContent);
		}

		return getOr(state, stripped)
				.map(Main::wrapInContent)
				.or(() -> compileIdentifier(stripped, state))
				.or(() -> compileNumber(stripped, state).map(Main::wrapInContent));
	}

	private static Option<Tuple<String, ParseState>> getOr(ParseState state, String stripped) {
		return compileOperator(stripped, "+", state)
				.or(() -> compileOperator(stripped, "-", state))
				.or(() -> compileOperator(stripped, ">=", state))
				.or(() -> compileOperator(stripped, "<", state))
				.or(() -> compileOperator(stripped, "!=", state))
				.or(() -> compileOperator(stripped, "==", state))
				.or(() -> compileOperator(stripped, "&&", state))
				.or(() -> compileOperator(stripped, "||", state));
	}

	private static Tuple<CExpression, ParseState> wrapInContent(Tuple<String, ParseState> tuple) {
		return new Tuple<CExpression, ParseState>(new Content(tuple.left), tuple.right);
	}

	private static Option<Tuple<String, ParseState>> compileString(String stripped, ParseState state) {
		if (isString(stripped)) {
			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(stripped, state));
		}
		return new None<Tuple<String, ParseState>>();
	}

	private static Option<Tuple<String, ParseState>> compileFieldAccess(ParseState state, String stripped) {
		final int separator = stripped.lastIndexOf(".");
		if (separator < 0) {
			return new None<Tuple<String, ParseState>>();
		}

		final String substring = stripped.substring(0, separator);
		final String name = stripped.substring(separator + 1).strip();

		if (!isIdentifier(name)) {
			return new None<Tuple<String, ParseState>>();
		}
		final Option<Tuple<String, ParseState>> maybeResult =
				tryCompileExpression(substring, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																													tuple.right));
		if (!(maybeResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value))) {
			return new None<Tuple<String, ParseState>>();
		}
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(value.left + "." + name, value.right));
	}

	private static Option<Tuple<String, ParseState>> compileMethodReference(ParseState state, String stripped) {
		final int separator = stripped.lastIndexOf("::");
		if (separator >= 0) {
			final String substring = stripped.substring(0, separator);
			final String name = stripped.substring(separator + 2).strip();
			if (isIdentifier(name)) {
				final Option<String> maybeResult = compileType(substring);
				if (maybeResult instanceof Some<String>(String value)) {
					return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(name + "_" + value, state));
				}
			}
		}
		return new None<Tuple<String, ParseState>>();
	}

	private static Option<Tuple<String, ParseState>> compileInstanceOf(ParseState state, String stripped) {
		final int instanceOfIndex = stripped.indexOf("instanceof");
		if (instanceOfIndex < 0) {
			return new None<Tuple<String, ParseState>>();
		}
		final String beforeOperator = stripped.substring(0, instanceOfIndex).strip();
		String afterOperator = stripped.substring(instanceOfIndex + "instanceof".length()).strip();

		final Option<Tuple<CExpression, ParseState>> maybeResult = tryCompileExpression(beforeOperator, state);

		if (!(maybeResult instanceof Some<Tuple<CExpression, ParseState>>(
				Tuple<CExpression, ParseState> value
		))) {
			return new None<Tuple<String, ParseState>>();
		}

		final int typeArgumentsStart = afterOperator.indexOf("<");
		String variantName;
		if (typeArgumentsStart >= 0) {
			variantName = afterOperator.substring(0, typeArgumentsStart);
		} else {
			variantName = afterOperator;
		}

		String parameters = "";
		if (afterOperator.endsWith(")")) {
			final String slice = afterOperator.substring(0, afterOperator.length() - 1);
			final int paramStart = slice.indexOf("(");
			if (paramStart >= 0) {
				final String paramString = slice.substring(paramStart + 1);
				String result1 = "";
				if (!paramString.isEmpty()) {
					result1 = compileValues(paramString, slice1 -> compileDefinition(slice1).map(definition -> {
						final String generated = definition.generate();
						return generated + " = _cast." + definition.name;
					}).map(destructMember -> generateStatement(destructMember, 2)).orElse(""));
				}

				parameters = result1;
				afterOperator = afterOperator.substring(0, paramStart);
			}
		}

		final CExpression target = value.left;
		ParseState maybeWithBeforeStatement = value.right;
		final String targetAlias;

		if (!(target instanceof CIdentifier)) {
			final String alias = generateStatement("??? _temp = " + target.generate(), 1);
			maybeWithBeforeStatement.addBeforeStatement(alias);
			targetAlias = "_temp";
		} else {
			targetAlias = target.generate();
		}


		final String content = afterOperator + " _cast = " + targetAlias + ".data." + variantName.toLowerCase();
		final String statement = generateStatement(content, 2) + parameters;
		final ParseState parseState = maybeWithBeforeStatement.addAfterStatement(statement);
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(targetAlias + ".tag == " + variantName,
																																						 parseState));
	}

	private static Option<Tuple<String, ParseState>> compileChar(String stripped, ParseState state) {
		if (isABoolean(stripped)) {
			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(stripped, state));
		}
		return new None<Tuple<String, ParseState>>();
	}

	private static boolean isABoolean(String stripped) {
		return stripped.startsWith("'") && stripped.endsWith("'") && stripped.length() <= 4;
	}

	private static Option<Tuple<String, ParseState>> compileNot(ParseState state, String stripped) {
		if (stripped.startsWith("!")) {
			final String slice = stripped.substring(1);
			final Option<Tuple<String, ParseState>> maybeResult =
					tryCompileExpression(slice, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																												tuple.right));
			if (maybeResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
				return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("!" + value.left, value.right));
			}
		}
		return new None<Tuple<String, ParseState>>();
	}

	private static Option<Tuple<String, ParseState>> compileInvokable(ParseState state, String stripped) {
		if (!stripped.endsWith(")")) {
			return new None<Tuple<String, ParseState>>();
		}
		final String slice = stripped.substring(0, stripped.length() - 1);

		final ArrayList<String> segments = findArgStart(slice).collect(new ListCollector<String>());
		if (segments.size() < 2) {
			return new None<Tuple<String, ParseState>>();
		}

		final String callerWithExt =
				segments.subList(0, segments.size() - 1).orElse(new ArrayList<String>()).stream().collect(new Joiner(""));
		if (!callerWithExt.endsWith("(")) {
			return new None<Tuple<String, ParseState>>();
		}
		final String caller = callerWithExt.substring(0, callerWithExt.length() - 1);
		final String arguments = segments.getLast().orElse(null);

		final Option<Tuple<String, ParseState>> maybeCallerResult = compileCaller(state, caller);
		if (!(maybeCallerResult instanceof Some<Tuple<String, ParseState>>(
				Tuple<String, ParseState> value
		))) {
			return new None<Tuple<String, ParseState>>();
		}

		final Tuple<StringJoiner, ParseState> reduce = divide(arguments, Main::foldValue)
				.collect(new ListCollector<String>())
				.stream()
				.foldWithInitial(new Tuple<StringJoiner, ParseState>(new StringJoiner(", "), value.right),
												 (tuple, s) -> mergeExpression(tuple.left, tuple.right, s));
		final String collect = reduce.left.toString();
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(value.left + "(" + collect + ")",
																																						 reduce.right));
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
				if (entered.isShallow()) {
					return entered.advance();
				} else {
					return entered;
				}
			}
			if (c == ')') {
				return appended.exit();
			}
			return appended;
		});
	}

	private static Option<Tuple<String, ParseState>> compileLambda(ParseState state, String stripped) {
		final int i1 = stripped.indexOf("->");
		if (i1 < 0) {
			return new None<Tuple<String, ParseState>>();
		}

		final String beforeArrow = stripped.substring(0, i1).strip();

		final String outputParams;
		if (isIdentifier(beforeArrow)) {
			outputParams = "auto " + beforeArrow;
		} else if (beforeArrow.startsWith("(") && beforeArrow.endsWith(")")) {
			final String withoutParentheses = beforeArrow.substring(1, beforeArrow.length() - 1);
			final String[] array = withoutParentheses.split(Pattern.quote(","));
			outputParams = Streams
					.fromRef(array)
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(slice -> "auto " + slice)
					.collect(new Joiner(", "));

		} else {
			return new None<Tuple<String, ParseState>>();
		}

		final String body = stripped.substring(i1 + 2).strip();
		final Tuple<String, ParseState> bodyResult = compileLambdaBody(state, body);

		final String generatedName = bodyResult.right.generateAnonymousFunctionName();
		final String s1 = "auto " + generatedName + "(" + outputParams + ") " + bodyResult.left + System.lineSeparator();
		return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(generatedName,
																																						 bodyResult.right.addFunction(s1)));
	}

	private static Tuple<String, ParseState> compileLambdaBody(ParseState state, String body) {
		final Option<Tuple<String, ParseState>> maybeBlock = compileBlock(state, body, 0);
		if (maybeBlock instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value)) {
			return value;
		}

		final Tuple<String, ParseState> result = compileExpression(body, state);
		final String s = generateStatement("return " + result.left, 1);
		final String s2 = "{" + s + generateIndent(0) + "}";
		return new Tuple<String, ParseState>(s2, result.right);
	}

	private static Option<Tuple<String, ParseState>> compileCaller(ParseState state, String caller) {
		if (caller.startsWith("new ")) {
			final Option<String> newType = compileType(caller.substring("new ".length()));
			if (newType instanceof Some<String>(String value)) {
				return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>("new_" + value, state));
			}
		}

		return tryCompileExpression(caller, state).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																													tuple.right));
	}

	private static Option<Tuple<CExpression, ParseState>> compileIdentifier(String input, ParseState state) {
		if (isIdentifier(input)) {
			return new Some<Tuple<CExpression, ParseState>>(new Tuple<CExpression, ParseState>(new CIdentifier(input),
																																												 state));
		}
		return new None<Tuple<CExpression, ParseState>>();
	}

	private static Option<Tuple<String, ParseState>> compileNumber(String stripped, ParseState state) {
		if (isNumber(stripped)) {
			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(stripped, state));
		}
		return new None<Tuple<String, ParseState>>();
	}

	private static Option<Tuple<String, ParseState>> compileOperator(String input, String operator, ParseState state) {
		final ArrayList<String> segments =
				divide(input, (state1, next) -> foldOperator(operator, state1, next)).collect(new ListCollector<String>());

		if (segments.size() < 2) {
			return new None<Tuple<String, ParseState>>();
		}

		final String left = segments.getFirst().orElse(null);
		final String right =
				segments.subList(1, segments.size()).orElse(new ArrayList<String>()).stream().collect(new Joiner(operator));

		final Option<Tuple<String, ParseState>> maybeLeftResult =
				tryCompileExpression(left, state).map(tuple1 -> new Tuple<String, ParseState>(tuple1.left.generate(),
																																											tuple1.right));

		if (!(maybeLeftResult instanceof Some<Tuple<String, ParseState>>(Tuple<String, ParseState> value))) {
			return new None<Tuple<String, ParseState>>();
		}

		final Option<Tuple<String, ParseState>> maybeRightResult =
				tryCompileExpression(right, value.right).map(tuple -> new Tuple<String, ParseState>(tuple.left.generate(),
																																														tuple.right));
		if (maybeRightResult instanceof Some<Tuple<String, ParseState>>(
				Tuple<String, ParseState> rightResult
		)) {
			final String generated = value.left + " " + operator + " " + rightResult.left;
			return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(generated, rightResult.right));
		}

		return new None<Tuple<String, ParseState>>();
	}

	private static DivideState foldOperator(String operator, DivideState state1, Character next) {
		if (next != operator.charAt(0)) {
			return state1.append(next);
		}

		final Option<Character> peeked = state1.peek();
		if (operator.length() >= 2 && peeked instanceof Some<Character>(Character value)) {
			if (value == operator.charAt(1)) {
				return state1.pop().map(inner -> inner.left).orElse(state1).advance();
			}
		}

		return state1.advance();
	}

	private static boolean isString(String stripped) {
		if (stripped.length() < 2) {
			return false;
		}

		final boolean hasDoubleQuotes = stripped.startsWith("\"") && stripped.endsWith("\"");
		if (!hasDoubleQuotes) {
			return false;
		}

		final String content = stripped.substring(1, stripped.length() - 1);
		return areAllDoubleQuotesEscaped(content);
	}

	private static boolean areAllDoubleQuotesEscaped(String input) {
		return IntStream.range(0, input.length()).allMatch(i -> {
			final char c = input.charAt(i);
			if (c != '\"') {
				return true;
			}
			if (i == 0) {
				return false;
			}
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

	private static Option<JMethodHeader> compileConstructor(String beforeParams) {
		final int separator = beforeParams.lastIndexOf(" ");
		if (separator < 0) {
			return new None<JMethodHeader>();
		}

		final String name = beforeParams.substring(separator + " ".length());
		return new Some<JMethodHeader>(new JConstructor(name));
	}

	private static Option<Tuple<String, ParseState>> compileField(String input, ParseState state) {
		if (input.endsWith(";")) {
			final String substring = input.substring(0, input.length() - ";".length()).strip();
			final Option<String> s = generateField(substring);
			if (s instanceof Some<String>(String value)) {
				return new Some<Tuple<String, ParseState>>(new Tuple<String, ParseState>(value, state));
			}
		}

		return new None<Tuple<String, ParseState>>();
	}

	private static Option<Definition> compileDefinition(String input) {
		final String stripped = input.strip();
		final int index = stripped.lastIndexOf(" ");
		if (index < 0) {
			return new None<Definition>();
		}

		final String beforeName = stripped.substring(0, index).strip();
		final String name = stripped.substring(index + " ".length()).strip();
		if (!isIdentifier(name)) {
			return new None<Definition>();
		}

		final ArrayList<String> segments =
				divide(beforeName, Main::foldTypeSeparator).collect(new ListCollector<String>());
		if (segments.size() < 2) {
			return compileType(beforeName).map(type -> new Definition(new ArrayList<String>(), type, name));
		}

		final String beforeType =
				segments.subList(0, segments.size() - 1).orElse(new ArrayList<String>()).stream().collect(new Joiner(" "));

		final ArrayList<String> annotations = findAnnotations(beforeType);

		final String typeString = segments.getLast().orElse(null);
		return compileType(typeString).map(type -> new Definition(annotations, type, name));
	}

	private static ArrayList<String> findAnnotations(String withoutLast) {
		final int i = withoutLast.lastIndexOf("\n");
		if (i < 0) {
			return new ArrayList<String>();
		}

		final String[] slices = withoutLast.substring(0, i).strip().split(Pattern.quote("\n"));
		return Streams
				.fromRef(slices)
				.map(String::strip)
				.filter(slice -> slice.startsWith("@"))
				.map(slice -> slice.substring(1))
				.collect(new ListCollector<String>());
	}

	private static DivideState foldTypeSeparator(DivideState state, Character c) {
		if (c == ' ' && state.isLevel()) {
			return state.advance();
		}

		final DivideState appended = state.append(c);
		if (c == '<') {
			return appended.enter();
		}
		if (c == '>') {
			return appended.exit();
		}
		return appended;
	}

	private static Option<String> compileType(String input) {
		final String stripped = input.strip();
		if (stripped.equals("public")) {
			return new None<String>();
		}

		if (stripped.endsWith(">")) {
			final String withoutEnd = stripped.substring(0, stripped.length() - 1);
			final int argumentStart = withoutEnd.indexOf("<");
			if (argumentStart >= 0) {
				final String base = withoutEnd.substring(0, argumentStart);
				final String argumentsString = withoutEnd.substring(argumentStart + "<".length());

				final ArrayList<String> arguments = divide(argumentsString, Main::foldValue)
						.map(Main::compileTypeOrPlaceholder)
						.collect(new ListCollector<String>());

				final String outputArguments = arguments.stream().collect(new Joiner(", "));
				return new Some<String>(base + "<" + outputArguments + ">");
			}
		}

		if (stripped.endsWith("[]")) {
			final String slice = stripped.substring(0, stripped.length() - 2);
			return compileType(slice).map(result -> result + "*");
		}

		if (stripped.equals("String")) {
			return new Some<String>("char*");
		}
		if (stripped.equals("int")) {
			return new Some<String>("int");
		}

		if (isIdentifier(stripped)) {
			return new Some<String>(stripped);
		}
		return new Some<String>(wrap(stripped));
	}

	private static String compileTypeOrPlaceholder(String slice) {
		return compileType(slice).orElseGet(() -> wrap(slice));
	}

	private static String wrap(String input) {
		final String replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
