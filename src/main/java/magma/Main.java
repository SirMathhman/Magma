package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
	private enum PrimitiveType implements Type {
		Void("void"), Char("char");

		private final String content;

		PrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String toBaseName() {
			return this.content;
		}
	}

	private interface FR<T> {
		T apply();
	}

	private sealed interface Option<T> permits None, Some {
		static <T> Option<T> of(T value) {
			return new Some<T>(value);
		}

		static <T> Option<T> empty() {
			return new None<T>();
		}

		<R> Option<R> map(F1R<T, R> mapper);

		T orElse(T other);

		<R> Option<R> flatMap(F1R<T, Option<R>> mapper);

		T orElseGet(FR<T> other);

		Stream<T> stream();

		Option<T> or(FR<Option<T>> other);
	}

	private interface F1R<T0, R> {
		R apply(T0 value);
	}

	private sealed interface Result<T, X> permits Err, Ok {
		<R> Result<R, X> mapValue(F1R<T, R> mapper);
	}

	private sealed interface Type permits Identifier, Placeholder, PointerType, PrimitiveType, TemplateType {
		String generate();

		String toBaseName();
	}

	private sealed interface MethodDeclaration permits Constructor, Declaration, Placeholder {
		String generate();
	}

	private sealed interface StructMember permits EmptyStructMember, FunctionDeclaration, Placeholder {
		String generate();
	}

	private interface Folder {
		State apply(State state, Character character);
	}

	private @interface Actual {}

	private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(F1R<T, R> mapper) {
			return new Err<R, X>(this.error);
		}
	}

	private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(F1R<T, R> mapper) {
			return new Ok<R, X>(mapper.apply(this.value));
		}
	}

	private record Tuple<A, B>(A left, B right) {}

	private static class State {
		private final String input;
		private final ArrayList<String> segments;
		private final StringBuilder buffer;
		private int index;
		private int depth;

		public State(String input) {
			this.input = input;
			this.index = 0;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
		}

		private boolean isShallow() {
			return this.depth == 1;
		}

		private boolean isLevel() {
			return this.depth == 0;
		}

		private State append(Character next) {
			this.buffer.append(next);
			return this;
		}

		private Option<Character> pop() {
			if (this.index < this.input.length()) {
				final var value = this.input.charAt(this.index);
				this.index++;
				return Option.of(value);
			} else {
				return Option.empty();
			}
		}

		private State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer.setLength(0);
			return this;
		}

		private State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		private State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		private Stream<String> stream() {
			return this.segments.stream();
		}

		public Option<Tuple<State, Character>> popAndAppendToTuple() {
			return this.pop().map(popped -> {
				final var appended = this.append(popped);
				return new Tuple<State, Character>(appended, popped);
			});
		}

		public Option<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(tuple -> tuple.left);
		}

		public Option<Character> peek() {
			if (this.index < this.input.length()) {
				return Option.of(this.input.charAt(this.index));
			}

			return Option.empty();
		}
	}

	private record PointerType(Type type) implements Type {
		@Override
		public String generate() {
			return this.type.generate() + "*";
		}

		@Override
		public String toBaseName() {
			return this.type.toBaseName() + "_ptr";
		}
	}

	private record TemplateType(String base, List<Type> list) implements Type {

		@Override
		public String generate() {
			final var typeArguments = this.list.stream().map(Type::generate).collect(Collectors.joining(", "));

			return this.base + "<" + typeArguments + ">";
		}

		@Override
		public String toBaseName() {
			return this.base;
		}
	}

	private record Identifier(String value) implements Type {
		@Override
		public String generate() {
			return this.value;
		}

		@Override
		public String toBaseName() {
			return this.value;
		}
	}

	private record Placeholder(String input) implements Type, MethodDeclaration, StructMember {
		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String toBaseName() {
			return wrap(this.input);
		}
	}

	private record Constructor(String structName) implements MethodDeclaration {
		@Override
		public String generate() {
			return this.structName + " new_" + this.structName;
		}
	}

	private record Declaration(List<String> annotations, List<String> typeParameters, Option<String> maybeBeforeType,
														 String type, String name) implements MethodDeclaration {
		public Declaration(String type, String name) {
			this(Collections.emptyList(), Collections.emptyList(), Option.empty(), type, name);
		}

		@Override
		public String generate() {
			var beforeDeclaration = generateTemplateString(this.typeParameters());
			return beforeDeclaration + this.type + " " + this.name;
		}

		public Declaration mapName(F1R<String, String> mapper) {
			return new Declaration(this.annotations,
														 this.typeParameters,
														 this.maybeBeforeType,
														 this.type,
														 mapper.apply(this.name));
		}
	}

	private record FunctionDeclaration(String type, String name, List<String> parameterTypes) implements StructMember {
		@Override
		public String generate() {
			final var joinedParameterTypes = this.parameterTypes.stream().collect(Collectors.joining(", ", "(", ")"));
			return this.type + " (*" + this.name + ")" + joinedParameterTypes;
		}
	}

	private static final class EmptyStructMember implements StructMember {
		@Override
		public String generate() {
			return "";
		}
	}

	private record EscapedFolder(Folder folder) implements Folder {
		@Override
		public State apply(State state, Character next) {
			if (next == '\'') {
				final var appended = state.append(next);
				return appended.popAndAppendToTuple().map(tuple -> {
					if (tuple.right == '\\') {
						return tuple.left.popAndAppendToOption().orElse(tuple.left);
					}
					return tuple.left;
				}).flatMap(State::popAndAppendToOption).orElse(appended);
			}

			if (next == '\"') {
				var current = state.append(next);
				while (true) {
					final var maybeTuple = current.popAndAppendToTuple();
					if (!(maybeTuple instanceof Some<Tuple<State, Character>>(var value))) {
						break;
					}

					current = value.left;

					final var right = value.right;
					if (right == '\\') {
						current = current.popAndAppendToOption().orElse(current);
					}

					if (right == '\"') {
						break;
					}
				}

				return current;
			}

			return this.folder.apply(state, next);
		}
	}

	private static class ValueFolder implements Folder {
		@Override
		public State apply(State state, Character next) {
			if (next == ',' && state.isLevel()) {
				return state.advance();
			}

			final var appended = state.append(next);
			if (next == '-') {
				final var peeked = appended.peek();
				if (peeked instanceof Some<Character>(var value) && value == '>') {
					return appended.popAndAppendToOption().orElse(appended);
				} else {
					return appended;
				}
			}

			if (next == '<' || next == '(') {
				return appended.enter();
			}

			if (next == '>' || next == ')') {
				return appended.exit();
			}
			return appended;
		}
	}

	private record Some<T>(T value) implements Option<T> {
		@Override
		public <R> Option<R> map(F1R<T, R> mapper) {
			return new Some<R>(mapper.apply(this.value));
		}

		@Override
		public T orElse(T other) {
			return this.value;
		}

		@Override
		public <R> Option<R> flatMap(F1R<T, Option<R>> mapper) {
			return mapper.apply(this.value);
		}

		@Override
		public T orElseGet(FR<T> other) {
			return this.value;
		}

		@Override
		public Stream<T> stream() {
			return Stream.of(this.value);
		}

		@Override
		public Option<T> or(FR<Option<T>> other) {
			return this;
		}
	}

	private static final class None<T> implements Option<T> {
		@Override
		public <R> Option<R> map(F1R<T, R> mapper) {
			return new None<R>();
		}

		@Override
		public T orElse(T other) {
			return other;
		}

		@Override
		public <R> Option<R> flatMap(F1R<T, Option<R>> mapper) {
			return new None<R>();
		}

		@Override
		public T orElseGet(FR<T> other) {
			return other.apply();
		}

		@Override
		public Stream<T> stream() {
			return Stream.empty();
		}

		@Override
		public Option<T> or(FR<Option<T>> other) {
			return other.apply();
		}
	}

	private static class ConditionEndLocator implements Folder {
		@Override
		public State apply(State state, Character c) {
			final var appended = state.append(c);
			if (c == '(') {
				return appended.enter();
			}

			if (c == ')') {
				if (appended.isLevel()) {
					return appended.advance();
				}

				return appended.exit();
			}

			return appended;
		}
	}

	public final List<String> structures;
	public final List<String> functions;
	public final List<String> globals;
	private int counter = 0;

	public Main() {
		this.structures = new ArrayList<String>();
		this.functions = new ArrayList<String>();
		this.globals = new ArrayList<String>();
	}

	private static String generateTemplateString(List<String> typeParameters) {
		final String templateString;
		if (typeParameters.isEmpty()) {
			templateString = "";
		} else {
			final var typeNames =
					typeParameters.stream().map(typeParam -> "typename " + typeParam).collect(Collectors.joining(", ", "<",
																																																			 ">"));

			templateString = "template " + typeNames + System.lineSeparator();
		}
		return templateString;
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}

	public static void main(String[] args) {
		var ioExceptionOption = new Main().run();
		if (ioExceptionOption instanceof Some<IOException>(
				var value
		)) {
			//noinspection CallToPrintStackTrace
			value.printStackTrace();
		}
	}

	private Option<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var target = source.resolveSibling("Main.cpp");
		final var input = this.readString(source).mapValue(this::compile);

		return switch (input) {
			case Err<String, IOException> v -> Option.of(v.error);
			case Ok<String, IOException> v -> this.writeString(target, v.value);
		};
	}

	@Actual
	private Option<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Option.empty();
		} catch (IOException e) {
			return Option.of(e);
		}
	}

	@Actual
	private Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private String compile(String input) {
		final var all = this.compileStatements(input, this::compileRootSegment);

		final var joinedStructures = String.join("", this.structures);
		final var joinedGlobals = String.join("", this.globals);
		final var joinedFunctions = String.join("", this.functions);
		return joinedStructures + joinedGlobals + joinedFunctions + all;
	}

	private String compileStatements(String input, F1R<String, String> mapper) {
		return this.compileAll(input, mapper, new EscapedFolder(this::foldStatement));
	}

	private String compileAll(String input, F1R<String, String> mapper, Folder folder) {
		return this.divide(input, folder).map(mapper::apply).collect(Collectors.joining(""));
	}

	private Stream<String> divide(String input, Folder folder) {
		var current = new State(input);
		while (true) {
			final var maybeNext = current.pop();
			if (!(maybeNext instanceof Some<Character>(var value))) {
				break;
			}

			final Character next;
			next = value;
			current = folder.apply(current, next);
		}

		return current.advance().stream();
	}

	private State foldStatement(State current, Character next) {
		if (next == '/' && current.isLevel()) {
			final var maybePeeked = current.peek();
			if (maybePeeked instanceof Some<Character>(var peek) && peek == '/') {
				var withoutLineCommentPrefix = current.append('/').popAndAppendToOption().orElse(current);
				while (true) {
					final var maybeTuple = withoutLineCommentPrefix.popAndAppendToTuple();
					if (maybeTuple instanceof Some<Tuple<State, Character>>(var tuple)) {
						withoutLineCommentPrefix = tuple.left;

						final var right = tuple.right;
						if (right == '\r' || right == '\n') {
							withoutLineCommentPrefix = withoutLineCommentPrefix.advance();
						}
					} else {
						return withoutLineCommentPrefix;
					}
				}
			}
		}

		final var appended = current.append(next);
		if (next == ';' && appended.isLevel()) {
			return appended.advance();
		}

		if (next == '}' && appended.isShallow()) {
			final State appended1;
			if (appended.peek() instanceof Some<Character>(var peek) && peek == ';') {
				appended1 = appended.popAndAppendToOption().orElse(appended);
			} else {
				appended1 = appended;
			}

			return appended1.advance().exit();
		}

		if (next == '{' || next == '(') {
			return appended.enter();
		}

		if (next == '}' || next == ')') {
			return appended.exit();
		}

		return appended;
	}

	private String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return this.compileStructure("class", stripped).map(StructMember::generate).orElseGet(() -> wrap(stripped));
	}

	private Option<StructMember> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {return Option.empty();}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {return Option.empty();}
		var beforeContent = afterKeyword.substring(0, i1).strip();

		final var withEnd = afterKeyword.substring(i1 + 1).strip();
		if (!withEnd.endsWith("}")) {
			return Option.empty();
		}
		final var inputContent = withEnd.substring(0, withEnd.length() - 1);

		List<String> variants = new ArrayList<String>();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = this.splitValues(substring1);
		}

		List<Type> implementees = new ArrayList<Type>();
		final var i4 = beforeContent.indexOf("implements ");
		if (i4 >= 0) {
			final var implementeesString = beforeContent.substring(i4 + "implements ".length());
			beforeContent = beforeContent.substring(0, i4).strip();
			implementees = this
					.divide(implementeesString, (state, character) -> new ValueFolder().apply(state, character))
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.map(this::parseType)
					.toList();
		}

		List<Declaration> recordFields = Collections.emptyList();
		if (beforeContent.endsWith(")")) {
			final var substring = beforeContent.substring(0, beforeContent.length() - 1);
			final var i3 = substring.indexOf("(");
			if (i3 >= 0) {
				beforeContent = substring.substring(0, i3);
				recordFields = this
						.divide(substring.substring(i3 + 1), (state, character) -> new ValueFolder().apply(state, character))
						.map(slice -> this.parseDeclaration(slice, Collections.emptyList()))
						.flatMap(Option::stream)
						.toList();
			}
		}

		List<String> typeParameters = new ArrayList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			if (substring1.endsWith(">")) {
				beforeContent = beforeContent.substring(0, i3);
				final var substring = substring1.substring(0, substring1.length() - 1);
				typeParameters = this.splitValues(substring);
			}
		}

		if (!this.isIdentifier(beforeContent)) {return Option.empty();}

		final var modifiersList = Arrays
				.stream(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));

		var name = beforeContent.strip();

		final var templateString = generateTemplateString(typeParameters);
		final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

		var fields = "";
		var dependencies = new StringBuilder();
		implementees.stream().map(implementee -> {
			final var identifier = implementee.toBaseName();
			final var variant = identifier + "Variant" + "." + name + "Variant";
			final var thisType = name + joinedTypeParameters;
			final var conversionFunctionContent = this.generateStatement(thisType + " this = *((" + thisType + "*) _this)") +
																						this.generateStatement(
																								identifier + "Data" + joinedTypeParameters + " data") +
																						this.generateStatement("data." + name + " = this") +
																						this.generateStatement("return { " + variant + ", data }");
			return templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" +
						 conversionFunctionContent + System.lineSeparator() + "}" + System.lineSeparator();
		}).forEach(this.functions::add);

		final var joinedRecordFields =
				recordFields.stream().map(Declaration::generate).map(this::generateStatement).collect(Collectors.joining());

		var finalTypeParameters = typeParameters;
		var finalVariants = variants;
		final var members = this
				.divide(inputContent, new EscapedFolder(this::foldStatement))
				.map(slice -> this.compileClassSegment(slice, name, finalTypeParameters, finalVariants))
				.flatMap(Option::stream)
				.toList();

		if (modifiersList.contains("sealed")) {
			modifiersList.remove("sealed");

			final var enumFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Variant")
					.collect(Collectors.joining(","));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final var unionFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Data" + joinedTypeParameters + " " + variant +
													";")
					.collect(Collectors.joining());

			final var generatedUnion =
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			fields += System.lineSeparator() + "\t" + name + "Variant variant;" + System.lineSeparator() + "\t" + name +
								"Data data;";

			dependencies.append(generatedEnum).append(generatedUnion);
		} else if (type.equals("interface")) {
			final var table = this.generateStatement(name + "Table" + joinedTypeParameters + " table");
			final var data = this.generateStatement("void* data");

			final var tableMembers =
					members.stream().map(StructMember::generate).map(this::generateStatement).collect(Collectors.joining(""));
			final var vTable = templateString + "struct " + name + "Table" + joinedTypeParameters + " {" + tableMembers +
												 System.lineSeparator() + "};" + System.lineSeparator();

			dependencies.append(vTable);
			fields += table + data;
		} else {
			final var joinedMembers = members
					.stream()
					.filter(member -> !(member instanceof FunctionDeclaration))
					.map(StructMember::generate)
					.collect(Collectors.joining());

			fields += joinedMembers;
		}

		final var generated =
				dependencies + templateString + "struct " + name + " {" + joinedRecordFields + fields + System.lineSeparator() +
				"};" + System.lineSeparator();
		this.structures.add(generated);

		return Option.of(new EmptyStructMember());
	}

	private String joinTypeParameters(List<String> typeParameters) {
		final String joinedTypeParameters;
		if (typeParameters.isEmpty()) {
			joinedTypeParameters = "";
		} else {
			joinedTypeParameters = typeParameters.stream().collect(Collectors.joining(", ", "<", ">"));
		}
		return joinedTypeParameters;
	}

	private String generateStatement(String content) {return this.generateStatement(1, content);}

	private String generateStatement(int depth, String content) {
		return this.generateIndent(depth) + content + ";";
	}

	private String generateIndent(int depth) {
		return System.lineSeparator() + "\t".repeat(depth);
	}

	private List<String> splitValues(String input) {
		return Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
	}

	private boolean isIdentifier(String input) {
		final var stripped = input.strip();
		return IntStream.range(0, stripped.length()).allMatch(i -> {
			final var c = stripped.charAt(i);
			return Character.isLetter(c) || (i != 0 && Character.isDigit(c));
		});
	}

	private Option<StructMember> compileClassSegment(String input,
																									 String structName,
																									 List<String> typeParameters,
																									 List<String> variants) {
		final var stripped = input.strip();

		if (stripped.isEmpty()) {
			return Option.empty();
		}

		final var maybeEnum = this.compileStructure("enum", input);
		if (maybeEnum instanceof Some<StructMember>) {
			return maybeEnum;
		}

		final var maybeInterface = this.compileStructure("interface", input);
		if (maybeInterface instanceof Some<StructMember>) {
			return maybeInterface;
		}

		final var maybeRecord = this.compileStructure("record", input);
		if (maybeRecord instanceof Some<StructMember>) {
			return maybeRecord;
		}

		final var maybeClass = this.compileStructure("class", input);
		if (maybeClass instanceof Some<StructMember>) {
			return maybeClass;
		}

		final var maybeEnumValues = this.compileEnumValues(input, structName);
		if (maybeEnumValues instanceof Some<StructMember>) {
			return maybeEnumValues;
		}

		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var declarationString = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var parametersString = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();

				final var parameters = this
						.divide(parametersString, (state, character) -> new ValueFolder().apply(state, character))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList()
						.stream()
						.map(param -> this.parseDeclaration(param, typeParameters))
						.flatMap(Option::stream)
						.collect(Collectors.toCollection(ArrayList::new));

				final var methodDeclaration = this.parseMethodDeclaration(declarationString, structName, typeParameters);

				Option<String> maybeCompiled = Option.empty();
				if (methodDeclaration instanceof Declaration declaration && declaration.annotations.contains("Actual")) {
					final var compiledParameters =
							parameters.stream().map(Declaration::generate).collect(Collectors.joining(", "));

					final var modifiedMethodDeclaration = declaration.mapName(name -> name + "_" + structName);
					this.functions.add(
							modifiedMethodDeclaration.generate() + "(" + compiledParameters + ");" + System.lineSeparator());
					return new Some<StructMember>(new EmptyStructMember());
				}

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var inputContent = withBraces.substring(1, withBraces.length() - 1);
					maybeCompiled = Option.of(this.compileMethodsSegments(inputContent, 1));
				}

				String outputContent;
				if (methodDeclaration instanceof Constructor) {
					final var compiled = maybeCompiled.orElse("?");
					outputContent =
							this.generateStatement(structName + " this") + compiled + this.generateStatement("return this");
				} else if (methodDeclaration instanceof Declaration declaration) {
					parameters.addFirst(new Declaration("void*", "_this"));

					final var joinedTypeParameters = this.joinTypeParameters(typeParameters);

					final var thisInitialization = this.generateStatement(
							structName + joinedTypeParameters + "* this = (" + structName + joinedTypeParameters + "*) _this");

					outputContent = thisInitialization + maybeCompiled.orElseGet(() -> {
						final var returnValueDefinition = this.generateStatement(declaration.type + " _ret");

						final var cases = variants
								.stream()
								.map(variant -> this.generateCase(structName, declaration, variant))
								.collect(Collectors.joining());

						return returnValueDefinition + this.generateIndent(1) + "switch (" + "this.variant" + ") {" + cases +
									 this.generateIndent(1) + "}" + this.generateStatement("return _ret");
					});
				} else {
					outputContent = "?";
				}

				final var compiledParameters = parameters.stream().map(Declaration::generate).collect(Collectors.joining(", "
				));

				final var modifiedMethodDeclaration = switch (methodDeclaration) {
					case Constructor constructor -> constructor;
					case Declaration declaration -> declaration.mapName(name -> name + "_" + structName);
					case Placeholder placeholder -> placeholder;
				};

				final var header = modifiedMethodDeclaration.generate() + "(" + compiledParameters + ")";
				final var generated = header + "{" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();
				this.functions.add(generated);

				final var parameterTypes = parameters.stream().map(Declaration::type).toList();

				return switch (methodDeclaration) {
					case Constructor _ -> Option.empty();
					case Declaration member -> Option.of(new FunctionDeclaration(member.type, member.name, parameterTypes));
					case Placeholder placeholder -> Option.of(placeholder);
				};
			}
		}

		return Option.of(new Placeholder(stripped));
	}

	private String compileMethodsSegments(String inputContent, int indent) {
		return this.compileStatements(inputContent, input -> this.compileMethodSegment(input, indent));
	}

	private String generateCase(String structName, Declaration declaration, String variant) {
		return this.generateIndent(2) + "case " + structName + "Variant." + variant + "Variant:" +
					 this.generateStatement(3, "_ret = " + declaration.name + "_" + variant + "(&this.data." + variant + ")") +
					 this.generateStatement(3, "break");
	}

	private MethodDeclaration parseMethodDeclaration(String declaration, String structName,
																									 List<String> typeParameters) {
		return this
				.parseDeclaration(declaration, typeParameters)
				.<MethodDeclaration>map(value -> value)
				.or(() -> this.parseConstructor(declaration, structName))
				.orElseGet(() -> new Placeholder(declaration));
	}

	private Option<MethodDeclaration> parseConstructor(String declaration, String structName) {
		if (declaration.strip().equals(structName)) {
			return Option.of(new Constructor(structName));
		} else {
			return Option.empty();
		}
	}

	private Option<StructMember> compileEnumValues(String input, String structName) {
		final var stripped = input.strip();
		if (!stripped.endsWith(";")) {
			return Option.empty();
		}

		final var enumValues = this
				.divide(stripped.substring(0, stripped.length() - 1),
								(state, character) -> new ValueFolder().apply(state, character))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		if (!enumValues.isEmpty()) {
			final var areAnyInvalid = enumValues
					.stream()
					.map(enumValue -> this.compileEnumValue(structName, enumValue))
					.anyMatch(option -> option instanceof None<StructMember>);

			if (areAnyInvalid) {
				return new None<StructMember>();
			}
		}

		return Option.of(new EmptyStructMember());
	}

	private Option<StructMember> compileEnumValue(String structName, String enumValue) {
		if (enumValue.endsWith(")")) {
			final var substring = enumValue.substring(0, enumValue.length() - 1);
			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var name = substring.substring(0, i);
				if (!this.isIdentifier(name)) {
					return Option.empty();
				}

				final var substring2 = substring.substring(i + 1);
				final var generated =
						structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
						System.lineSeparator();

				this.globals.add(generated);
				return Option.of(new EmptyStructMember());
			}
		}

		return new None<StructMember>();
	}

	private String compileMethodSegment(String input, int indent) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return this.generateIndent(indent) + this.compileMethodStatement(substring) + ";";
		}

		final var maybeIf = this.compileConditional("if", indent, stripped);
		if (maybeIf instanceof Some<String>(var result)) {
			return result;
		}

		final var maybeWhile = this.compileConditional("while", indent, stripped);
		if (maybeWhile instanceof Some<String>(var result)) {
			return result;
		}

		if (stripped.startsWith("else ")) {
			final var substring = stripped.substring("else ".length()).strip();
			if (substring.startsWith("{") && substring.endsWith("}")) {
				final var substring1 = substring.substring(1, substring.length() - 1);
				return this.generateIndent(indent) + "else {" + this.compileMethodsSegments(substring1, indent + 1) +
							 this.generateIndent(indent) + "}";
			}
		}

		if (stripped.startsWith("//")) {
			return this.generateIndent(indent) + stripped;
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private Option<String> compileConditional(String type, int indent, String input) {
		if (input.startsWith(type)) {
			final var substring = input.substring(type.length()).strip();
			if (substring.startsWith("(")) {
				final var afterConditionStart = substring.substring(1).strip();

				final var divisions = this
						.divide(afterConditionStart, new EscapedFolder(new ConditionEndLocator()))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList();

				if (divisions.size() < 2) {
					return Option.empty();
				}

				final var first = divisions.getFirst();
				final var last = String.join("", divisions.subList(1, divisions.size()));

				if (!first.endsWith(")")) {
					return new None<String>();
				}
				final var condition = first.substring(0, first.length() - 1);

				if (last.startsWith("{") && last.endsWith("}")) {
					final var content = last.substring(1, last.length() - 1);
					return Option.of(
							this.generateIndent(indent) + type + " (" + this.compileExpressionOrPlaceholder(condition) + ") {" +
							this.compileMethodsSegments(content, indent + 1) + this.generateIndent(indent) + "}");
				}
			}
		}

		return Option.empty();
	}

	private String compileMethodStatement(String input) {
		final var stripped = input.strip();
		if (stripped.equals("break")) {
			return "break";
		}

		if (stripped.startsWith("return ")) {
			return "return " + this.compileExpressionOrPlaceholder(stripped.substring("return ".length()));
		}

		final var i = stripped.indexOf("=");
		if (i >= 0) {
			final var destination = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			return this
								 .compileExpression(destination)
								 .or(() -> this.parseDeclaration(destination, Collections.emptyList()).map(Declaration::generate))
								 .orElseGet(() -> wrap(destination)) + " = " + this.compileExpressionOrPlaceholder(substring1);
		}

		final var maybeInvokable = this.compileInvokable(stripped);
		if (maybeInvokable instanceof Some<String>(var value)) {
			return value;
		}

		if (stripped.endsWith("++")) {
			final var instance = stripped.substring(0, stripped.length() - 2);
			return this.compileExpressionOrPlaceholder(instance) + "++";
		}

		final var maybeDeclaration = this.parseDeclaration(input, Collections.emptyList());
		if (maybeDeclaration instanceof Some<Declaration>(var declaration)) {
			return declaration.generate();
		}

		return wrap(stripped);
	}

	private String compileExpressionOrPlaceholder(String input) {
		return this.compileExpression(input).orElseGet(() -> wrap(input));
	}

	private Option<String> compileExpression(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("switch ")) {
			return new Some<String>("_switch");
		}

		final var i2 = stripped.lastIndexOf("::");
		if (i2 >= 0) {
			final var substring = stripped.substring(0, i2);
			final var name = stripped.substring(i2 + 2).strip();
			if (this.isIdentifier(name)) {
				final var compiled = this.compileExpressionOrPlaceholder(substring);
				final var functionalInterfaceName = "F?";
				return Option.of(
						functionalInterfaceName + " { alloc(" + compiled + "), " + functionalInterfaceName + "Table { " + name +
						" }}");
			}
		}

		if (stripped.startsWith("'") && stripped.endsWith("'")) {
			return Option.of(stripped);
		}

		final var maybeLambda = this.compileLambda(stripped);
		if (maybeLambda instanceof Some<String>) {
			return maybeLambda;
		}

		final var i3 = stripped.indexOf("instanceof");
		if (i3 >= 0) {
			final var substring = stripped.substring(0, i3);
			final var substring1 = stripped.substring(i3 + "instanceof".length()).strip();
			final var maybeInstance = this.compileExpression(substring);
			if (maybeInstance instanceof Some<String>(var instance)) {
				final var i4 = substring1.indexOf("<");
				final String substring2;
				if (i4 >= 0) {
					substring2 = substring1.substring(0, i4);
				} else {
					substring2 = substring1;
				}

				return new Some<String>(instance + ".variant = ?." + substring2 + "Variant");
			}
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var instanceString = stripped.substring(0, i);
			final var memberName = stripped.substring(i + 1).strip();
			if (this.isIdentifier(memberName)) {
				final var maybeInstance = this.compileExpression(instanceString);
				if (maybeInstance instanceof Some<String>(var value)) {
					final String instance;
					instance = value;
					final String generated;
					if (instance.equals("this")) {
						generated = "this->" + memberName;
					} else {
						generated = instance + "." + memberName;
					}

					return Option.of(generated);
				}
			}
		}

		final var maybeInvokable = this.compileInvokable(stripped);
		if (maybeInvokable instanceof Some<String>) {
			return maybeInvokable;
		}

		final var maybeOperator = this
				.compileOperator(stripped, "==")
				.or(() -> this.compileOperator(stripped, "<"))
				.or(() -> this.compileOperator(stripped, "+"))
				.or(() -> this.compileOperator(stripped, "-"))
				.or(() -> this.compileOperator(stripped, "&&"))
				.or(() -> this.compileOperator(stripped, "||"))
				.or(() -> this.compileOperator(stripped, ">="));

		if (maybeOperator instanceof Some<String>) {
			return maybeOperator;
		}

		if (this.isIdentifier(stripped)) {
			return Option.of(stripped);
		}

		if (stripped.startsWith("!")) {
			final var substring = stripped.substring(1);
			final var maybeInstance = this.compileExpression(substring);
			if (maybeInstance instanceof Some<String>(var instance)) {
				return new Some<String>("!" + instance);
			}
		}

		if (this.isNumber(stripped)) {
			return Option.of(stripped);
		}

		if (stripped.startsWith("\"") && stripped.endsWith("\"")) {
			return Option.of(stripped);
		}

		return Option.empty();
	}

	private Option<String> compileLambda(String stripped) {
		final var i1 = stripped.indexOf("->");
		if (i1 >= 0) {
			final var beforeContent = stripped.substring(0, i1).strip();
			final var maybeWithBraces = stripped.substring(i1 + 2).strip();

			List<String> params;
			if (this.isIdentifier(beforeContent)) {
				params = Collections.singletonList(beforeContent);
			} else if (beforeContent.startsWith("(") && beforeContent.endsWith(")")) {
				final var substring = beforeContent.substring(1, beforeContent.length() - 1);
				params =
						this.divide(substring, new ValueFolder()).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
			} else {
				return new None<String>();
			}

			if (maybeWithBraces.startsWith("{") && maybeWithBraces.endsWith("}")) {
				final var content = maybeWithBraces.substring(1, maybeWithBraces.length() - 1);
				final var compiled = this.compileMethodsSegments(content, 1);

				final var generatedName = this.generateName();

				final var paramList =
						params.stream().map(param -> "auto " + param).collect(Collectors.toCollection(ArrayList::new));

				paramList.addFirst("void* _this");

				final var joined = String.join(", ", paramList);

				this.functions.add("auto " + generatedName + "(" + joined + "){" + compiled + System.lineSeparator() + "}" +
													 System.lineSeparator());
				return Option.of(generatedName);
			} else {
				final var generatedName = this.generateName();

				this.functions.add("auto " + generatedName + "(void* _this, auto " + beforeContent + "){" +
													 this.generateStatement("return " + this.compileExpressionOrPlaceholder(maybeWithBraces)) +
													 System.lineSeparator() + "}" + System.lineSeparator());
				return Option.of(generatedName);
			}
		}

		return Option.empty();
	}

	private String generateName() {
		final var generatedName = "lambda" + this.counter;
		this.counter++;
		return generatedName;
	}

	private Option<String> compileOperator(String input, String operator) {
		final var i1 = input.indexOf(operator);
		if (i1 >= 0) {
			final var leftString = input.substring(0, i1);
			final var right = input.substring(i1 + operator.length());
			if (this.compileExpression(leftString) instanceof Some<String>(var leftCompiled)) {
				if (this.compileExpression(right) instanceof Some<String>(var rightCompiled)) {
					return Option.of(leftCompiled + " " + operator + " " + rightCompiled);
				}
			}
		}

		return Option.empty();
	}

	private Option<String> compileInvokable(String stripped) {
		if (stripped.endsWith(")")) {
			final var withoutEnd = stripped.substring(0, stripped.length() - 1);

			final var callerStart = this.findCallerStart(withoutEnd);

			if (callerStart >= 0) {
				final var callerString = withoutEnd.substring(0, callerStart);
				final var arguments = withoutEnd.substring(callerStart + 1);
				final var joinedArguments = this
						.divide(arguments, new EscapedFolder(new ValueFolder()))
						.map(this::compileExpressionOrPlaceholder)
						.collect(Collectors.joining(", "));

				final var maybeCaller = this.compileCaller(callerString);
				if (maybeCaller instanceof Some<String>(var value)) {
					return Option.of(value + "(" + joinedArguments + ")");
				}
			}
		}

		return Option.empty();
	}

	private int findCallerStart(String withoutEnd) {
		var callerStart = -1;
		var depth = 0;
		var i = 0;
		while (i < withoutEnd.length()) {
			final var c = withoutEnd.charAt(i);
			if (c == '(') {
				if (depth == 0) {
					callerStart = i;
				}

				depth++;
			}
			if (c == ')') {
				depth--;
			}
			i++;
		}
		return callerStart;
	}

	private boolean isNumber(String input) {
		return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isDigit);
	}

	private Option<String> compileCaller(String input) {
		final var stripped = input.strip();
		final var maybeExpression = this.compileExpression(stripped);
		if (maybeExpression instanceof Some<String>) {
			return maybeExpression;
		}

		if (stripped.startsWith("new ")) {
			final var type = stripped.substring("new ".length());
			return Option.of("new_" + this.compileType(type));
		}

		return new None<String>();
	}

	private Option<Declaration> parseDeclaration(String input, List<String> typeParameters) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			final var typeSeparator = this.findTypeSeparator(beforeName);

			if (!this.isIdentifier(name)) {
				return Option.empty();
			}

			if (typeSeparator < 0) {
				final var type = this.compileType(beforeName);
				return Option.of(new Declaration(type, name));
			}

			var beforeType = beforeName.substring(0, typeSeparator).strip();

			final var copy = new ArrayList<String>(typeParameters);
			if (beforeType.endsWith(">")) {
				final var substring = beforeType.substring(0, beforeType.length() - 1);
				final var i = substring.indexOf("<");
				if (i >= 0) {
					final var substring2 = substring.substring(i + 1);
					copy.addAll(this.splitValues(substring2));

					beforeType = substring.substring(0, i);
				}
			}

			List<String> annotations = new ArrayList<String>();
			final var i = beforeType.lastIndexOf("\n");
			if (i >= 0) {
				annotations = Arrays
						.stream(beforeType.substring(0, i).split(Pattern.quote("\n")))
						.filter(slice -> !slice.isEmpty())
						.map(slice -> slice.substring(1))
						.map(String::strip)
						.toList();

				beforeType = beforeType.substring(i + 1).strip();
			}

			if (this.isIdentifier(name)) {
				return Option.of(new Declaration(annotations,
																				 copy,
																				 Option.of(beforeType),
																				 this.compileType(beforeName.substring(typeSeparator + 1)),
																				 name));
			}
		}

		return Option.empty();
	}

	private int findTypeSeparator(String beforeName) {
		var typeSeparator = -1;
		var depth = 0;
		var i = 0;
		while (i < beforeName.length()) {
			final var c = beforeName.charAt(i);
			if (c == ' ' && depth == 0) {
				typeSeparator = i;
			}
			if (c == '<') {
				depth++;
			}
			if (c == '>') {
				depth--;
			}
			i++;
		}
		return typeSeparator;
	}

	private String compileType(String input) {
		return this.parseType(input).generate();
	}

	private Type parseType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return PrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			final var type = this.parseType(slice);
			return new PointerType(type);
		}

		if (stripped.equals("String")) {
			return new PointerType(PrimitiveType.Char);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var parameters = substring.substring(i + 1);

				final var list = this.divide(parameters, new ValueFolder()).map(this::parseType).toList();

				return new TemplateType(base, list);
			}
		}

		if (this.isIdentifier(stripped)) {
			return new Identifier(stripped);
		}

		return new Placeholder(stripped);
	}
}
