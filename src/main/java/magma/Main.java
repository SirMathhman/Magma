package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

		private Optional<Character> pop() {
			if (this.index < this.input.length()) {
				final var value = this.input.charAt(this.index);
				this.index++;
				return Optional.of(value);
			} else {
				return Optional.empty();
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

	private record Declaration(List<String> typeParameters, Optional<String> maybeBeforeType, String type, String name)
			implements MethodDeclaration {
		public Declaration(String type, String name) {
			this(Collections.emptyList(), Optional.empty(), type, name);
		}

		@Override
		public String generate() {
			var beforeDeclaration = generateTemplateString(this.typeParameters());
			return beforeDeclaration + this.type + " " + this.name;
		}

		public Declaration mapName(F1R<String, String> mapper) {
			return new Declaration(this.typeParameters, this.maybeBeforeType, this.type, mapper.apply(this.name));
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


	public final List<String> structures;
	public final List<String> functions;
	public final List<String> globals;

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
			templateString = "template " + typeParameters
					.stream()
					.map(typeParam -> "typename " + typeParam)
					.collect(Collectors.joining(", ", "<", ">")) + System.lineSeparator();
		}
		return templateString;
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}

	public static void main(String[] args) {
		new Main().run().ifPresent(Throwable::printStackTrace);
	}

	private Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var target = source.resolveSibling("Main.cpp");
		final var input = this.readString(source).mapValue(this::compile);

		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> this.writeString(target, v.value);
		};
	}

	private Optional<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

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
		return this.compileAll(input, mapper, this::foldStatement);
	}

	private String compileAll(String input, F1R<String, String> mapper, BiFunction<State, Character, State> folder) {
		return this.divide(input, folder).map(mapper::apply).collect(Collectors.joining(""));
	}

	private Stream<String> divide(String input, BiFunction<State, Character, State> folder) {
		var current = new State(input);
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			final var next = maybeNext.get();
			current = folder.apply(current, next);
		}

		return current.advance().stream();
	}

	private State foldStatement(State current, Character next) {
		final var appended = current.append(next);
		if (next == ';' && appended.isLevel()) {
			return appended.advance();
		}

		if (next == '}' && appended.isShallow()) {
			return appended.advance().exit();
		}

		if (next == '{') {
			return appended.enter();
		}

		if (next == '}') {
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

	private Optional<StructMember> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {return Optional.empty();}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {return Optional.empty();}
		var beforeContent = afterKeyword.substring(0, i1).strip();

		final var withEnd = afterKeyword.substring(i1 + 1).strip();
		if (!withEnd.endsWith("}")) {
			return Optional.empty();
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
					.divide(implementeesString, this::foldValue)
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
						.divide(substring.substring(i3 + 1), this::foldValue)
						.map(slice -> this.parseDeclaration(slice, Collections.emptyList()))
						.flatMap(Optional::stream)
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

		if (!this.isIdentifier(beforeContent)) {return Optional.empty();}

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
		for (var implementee : implementees) {
			final var identifier = implementee.toBaseName();

			final var variant = identifier + "Variant" + "." + name + "Variant";
			final var thisType = name + joinedTypeParameters;
			final var conversionFunctionContent = this.generateStatement(thisType + " this = *((" + thisType + "*) _this)") +
																						this.generateStatement(
																								identifier + "Data" + joinedTypeParameters + " data") +
																						this.generateStatement("data." + name + " = this") +
																						this.generateStatement("return { " + variant + ", data }");

			final var conversionFunction =
					templateString + implementee.generate() + " to" + identifier + "_" + name + "(void* _this){" +
					conversionFunctionContent + System.lineSeparator() + "}" + System.lineSeparator();

			this.functions.add(conversionFunction);
		}

		final var joinedRecordFields =
				recordFields.stream().map(Declaration::generate).map(this::generateStatement).collect(Collectors.joining());

		var finalTypeParameters = typeParameters;
		var finalVariants = variants;
		final var members = this
				.divide(inputContent, this::foldStatement)
				.map(slice -> this.compileClassSegment(slice, name, finalTypeParameters, finalVariants))
				.flatMap(Optional::stream)
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

		return Optional.of(new EmptyStructMember());
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
		for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
		}

		return true;
	}

	private Optional<StructMember> compileClassSegment(String input,
																										 String structName,
																										 List<String> typeParameters,
																										 List<String> variants) {
		final var stripped = input.strip();

		if (stripped.isEmpty()) {
			return Optional.empty();
		}

		final var maybeEnum = this.compileStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum;
		}

		final var maybeInterface = this.compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface;
		}

		final var maybeRecord = this.compileStructure("record", input);
		if (maybeRecord.isPresent()) {
			return maybeRecord;
		}

		final var maybeClass = this.compileStructure("class", input);
		if (maybeClass.isPresent()) {
			return maybeClass;
		}

		final var maybeEnumValues = this.compileEnumValues(input, structName);
		if (maybeEnumValues.isPresent()) {
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
						.divide(parametersString, this::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList()
						.stream()
						.map(param -> this.parseDeclaration(param, typeParameters))
						.flatMap(Optional::stream)
						.collect(Collectors.toCollection(ArrayList::new));

				final var methodDeclaration = this.parseMethodDeclaration(declarationString, structName, typeParameters);

				Optional<String> maybeCompiled = Optional.empty();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var inputContent = withBraces.substring(1, withBraces.length() - 1);
					maybeCompiled = Optional.of(this.compileStatements(inputContent, this::compileMethodSegment));
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
							structName + joinedTypeParameters + " this = *((" + structName + joinedTypeParameters + "*) _this)");

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
					case Constructor _ -> Optional.empty();
					case Declaration member -> Optional.of(new FunctionDeclaration(member.type, member.name, parameterTypes));
					case Placeholder placeholder -> Optional.of(placeholder);
				};
			}
		}

		return Optional.of(new Placeholder(stripped));
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

	private Optional<MethodDeclaration> parseConstructor(String declaration, String structName) {
		if (declaration.strip().equals(structName)) {
			return Optional.of(new Constructor(structName));
		} else {
			return Optional.empty();
		}
	}

	private Optional<StructMember> compileEnumValues(String input, String structName) {
		final var stripped = input.strip();
		if (!stripped.endsWith(";")) {
			return Optional.empty();
		}

		final var enumValues = this
				.divide(stripped.substring(0, stripped.length() - 1), this::foldValue)
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.toList();

		if (!enumValues.isEmpty()) {
			for (var enumValue : enumValues) {
				if (enumValue.endsWith(")")) {
					final var substring = enumValue.substring(0, enumValue.length() - 1);
					final var i = substring.indexOf("(");
					if (i >= 0) {
						final var name = substring.substring(0, i);
						if (!this.isIdentifier(name)) {
							return Optional.empty();
						}

						final var substring2 = substring.substring(i + 1);
						final var generated =
								structName + " " + structName + name + " = " + "new_" + structName + "(" + substring2 + ")" + ";" +
								System.lineSeparator();

						this.globals.add(generated);
					}
				}
			}
		}

		return Optional.of(new EmptyStructMember());
	}

	private State foldValue(State state, Character next) {
		if (next == ',' && state.isLevel()) {
			return state.advance();
		}

		final var appended = state.append(next);
		if (next == '<') {
			return appended.enter();
		}
		if (next == '>') {
			return appended.exit();
		}
		return appended;
	}

	private String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return System.lineSeparator() + "\t" + this.compileMethodStatement(substring) + ";";
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private String compileMethodStatement(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("return ")) {
			return "return " + this.compileExpression(stripped.substring("return ".length()));
		}

		final var i = stripped.indexOf("=");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			return this.compileExpression(substring) + " = " + this.compileExpression(substring1);
		}

		return wrap(stripped);
	}

	private String compileExpression(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var instance = stripped.substring(0, i);
			final var memberName = stripped.substring(i + 1).strip();
			if (this.isIdentifier(memberName)) {
				return this.compileExpression(instance) + "." + memberName;
			}
		}

		if (this.isIdentifier(stripped)) {
			return stripped;
		}

		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i1 = substring.indexOf("(");
			if (i1 >= 0) {
				final var caller = substring.substring(0, i1);
				final var arguments = substring.substring(i1 + 1);
				final var joinedArguments =
						this.divide(arguments, this::foldValue).map(this::compileExpression).collect(Collectors.joining(", "));

				return this.compileCaller(caller) + "(" + joinedArguments + ")";
			}
		}

		if (this.isNumber(stripped)) {
			return stripped;
		}

		final var i1 = stripped.indexOf("==");
		if (i1 >= 0) {
			final var left = stripped.substring(0, i1);
			final var right = stripped.substring(i1 + 2);
			return this.compileExpression(left) + " == " + this.compileExpression(right);
		}

		return wrap(stripped);
	}

	private boolean isNumber(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (Character.isDigit(c)) {
				continue;
			}
			return false;
		}

		return true;
	}

	private String compileCaller(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("new ")) {
			final var type = stripped.substring("new ".length());
			return "new_" + this.compileType(type);
		}

		return this.compileExpression(stripped);
	}

	private Optional<Declaration> parseDeclaration(String input, List<String> typeParameters) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator).strip();
			final var name = stripped.substring(nameSeparator + 1).strip();

			var typeSeparator = -1;
			var depth = 0;
			for (var i = 0; i < beforeName.length(); i++) {
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
			}

			if (typeSeparator < 0 && this.isIdentifier(name)) {
				final var type = this.compileType(beforeName);
				return Optional.of(new Declaration(type, name));
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

			if (this.isIdentifier(name)) {
				return Optional.of(new Declaration(copy,
																					 Optional.of(beforeType),
																					 this.compileType(beforeName.substring(typeSeparator + 1)),
																					 name));
			}
		}

		return Optional.empty();
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

				final var list = this.divide(parameters, this::foldValue).map(this::parseType).toList();

				return new TemplateType(base, list);
			}
		}

		if (this.isIdentifier(stripped)) {
			return new Identifier(stripped);
		}

		return new Placeholder(stripped);
	}
}
