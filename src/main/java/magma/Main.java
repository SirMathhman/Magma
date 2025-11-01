package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
	private enum CPrimitiveType implements CType {
		Char("char"), Void("void");

		private final String content;

		CPrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}
	}

	private sealed interface Result<T, X> permits Err, Ok {}

	private sealed interface CType permits CIdentifier, CPlaceholder, CPointerType, CPrimitiveType, CTemplateType {
		String generate();
	}

	private sealed interface CDefinable permits CDefinition, CPlaceholder {
		String generate();
	}

	private sealed interface JMethodHeader permits JConstructor, JDefinition, JPlaceholder {
		CDefinable toCDefinition();
	}

	private interface CFunctionHeader {}

	private record Err<T, X>(X error) implements Result<T, X> {}

	private record Ok<T, X>(T value) implements Result<T, X> {}

	private record CPointerType(CType child) implements CType {
		@Override
		public String generate() {
			return this.child.generate() + "*";
		}
	}

	private record CTemplateType(String base, List<CType> typeArguments) implements CType {
		@Override
		public String generate() {
			final var typeArguments1 = this.typeArguments;
			final var stream = typeArguments1.stream();
			final var stringStream = stream.map(CType::generate);
			final var joined = stringStream.collect(Collectors.joining(", "));
			return this.base + "<" + joined + ">";
		}
	}

	private record CIdentifier(String input) implements CType {
		@Override
		public String generate() {
			return this.input;
		}
	}

	private record CPlaceholder(String input) implements CType, CDefinable {
		private static String wrap(String input) {
			final var replaced = input.replace("/*", "start").replace("*/", "end");
			return "/*" + replaced + "*/";
		}

		@Override
		public String generate() {
			return wrap(this.input);
		}
	}

	private record CDefinition(CType type, String name) implements CDefinable {
		public String generate() {
			return this.type.generate() + " " + this.name;
		}
	}

	private record JDefinition(Optional<String> beforeType, CType type, String name) implements JMethodHeader {
		@Override
		public CDefinable toCDefinition() {
			return new CDefinition(this.type, this.name);
		}
	}

	private record JConstructor(String input) implements JMethodHeader {
		@Override
		public CDefinable toCDefinition() {
			final var type = new CIdentifier(this.input);
			return new CDefinition(type, "new_" + this.input);
		}
	}

	private record JPlaceholder(String input) implements JMethodHeader {
		@Override
		public CDefinable toCDefinition() {
			return new CPlaceholder(this.input);
		}
	}

	public static final List<String> functions = new ArrayList<String>();
	public static final List<String> structures = new ArrayList<String>();
	private static final List<String> globals = new ArrayList<String>();
	private static final Stack<String> structureNames = new Stack<String>();
	private static final Stack<List<JDefinition>> scope = new Stack<List<JDefinition>>();

	public static void main(String[] args) {
		run().ifPresent(Throwable::printStackTrace);
	}

	private static Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		return switch (readString(source)) {
			case Ok(var input) -> {
				final var target = Paths.get(".", "src", "main", "windows", "magma", "Main.cpp");
				final var output = compile(input);
				yield writeString(target, output);
			}
			case Err<String, IOException> v -> Optional.of(v.error);
		};
	}

	private static Optional<IOException> writeString(Path target, String output) {
		try {
			Files.writeString(target, output);
			return Optional.empty();
		} catch (IOException e) {
			return Optional.of(e);
		}
	}

	private static Result<String, IOException> readString(Path source) {
		try {
			return new Ok<String, IOException>(Files.readString(source));
		} catch (IOException e) {
			return new Err<String, IOException>(e);
		}
	}

	private static String compile(String input) {
		final var compiled = compileStatements(input, Main::compileRootSegment);
		final var joinedGlobals = String.join("", globals);
		final var joinedStructures = String.join("", structures);
		final var joinedFunctions = String.join("", functions);
		return joinedGlobals + joinedStructures + joinedFunctions + compiled;
	}

	private static String compileStatements(String input, Function<String, String> mapper) {
		final var segments = new ArrayList<String>();
		var buffer = new StringBuilder();
		var depth = 0;
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
			} else if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
			}
		}
		segments.add(buffer.toString());

		return segments.stream().map(mapper).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure(stripped, "class").orElseGet(() -> CPlaceholder.wrap(stripped));
	}

	private static Optional<String> compileStructure(String stripped, String type) {
		final var i = stripped.indexOf(type + " ");
		if (i >= 0) {
			final var substring = stripped.substring(i + (type + " ").length()).strip();
			if (substring.endsWith("}")) {
				final var substring1 = substring.substring(0, substring.length() - 1);
				final var i1 = substring1.indexOf("{");
				if (i1 >= 0) {
					var beforeContent = substring1.substring(0, i1).strip();
					final var content = substring1.substring(i1 + 1).strip();

					final var i2 = beforeContent.indexOf("permits");
					List<String> variants = new ArrayList<String>();
					if (i2 >= 0) {
						final var stripped1 = beforeContent.substring(i2 + "permits".length()).strip().split(Pattern.quote(","));

						variants = Arrays.stream(stripped1).map(String::strip).filter(segment -> !segment.isEmpty()).toList();

						beforeContent = beforeContent.substring(0, i2).strip();
					}

					Optional<String> maybeImplements = Optional.empty();
					final var i4 = beforeContent.indexOf("implements ");
					if (i4 >= 0) {
						final var substring2 = beforeContent.substring(i4 + "implements ".length());
						maybeImplements = Optional.of(compileTypeToString(substring2.strip()));

						beforeContent = beforeContent.substring(0, i4).strip();
					}

					var structureFields = "";
					if (beforeContent.endsWith(")")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("(");
						if (i3 >= 0) {
							final var substring4 = substring2.substring(i3 + 1);
							structureFields += Arrays
									.stream(substring4.split(Pattern.quote(",")))
									.map(String::strip)
									.filter(slice -> !slice.isEmpty())
									.map(Main::compileDefinition)
									.flatMap(Optional::stream)
									.map(Main::generateStatement)
									.collect(Collectors.joining());

							beforeContent = substring2.substring(0, i3);
						}
					}

					List<String> typeParameters = new ArrayList<String>();
					if (beforeContent.endsWith(">")) {
						final var substring2 = beforeContent.substring(0, beforeContent.length() - 1);
						final var i3 = substring2.indexOf("<");
						if (i3 >= 0) {
							final var substring3 = substring2.substring(i3 + 1).strip().split(Pattern.quote(","));
							typeParameters = Arrays.stream(substring3).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

							beforeContent = beforeContent.substring(0, i3).strip();
						}
					}

					var templateString = "";
					if (!typeParameters.isEmpty()) {
						final var joined =
								typeParameters.stream().map(slice -> "typename " + slice).collect(Collectors.joining(", "));

						templateString = "template <" + joined + ">" + System.lineSeparator();
					}

					final String typeArguments;
					if (typeParameters.isEmpty()) {
						typeArguments = "";
					} else {
						typeArguments = "<" + String.join(", ", typeParameters) + ">";
					}

					var beforeStruct = "";
					if (maybeImplements.isPresent()) {
						final var superType = maybeImplements.get();
						final var thisType = beforeContent + typeArguments;
						functions.add(generateFunction(thisType,
																					 superType,
																					 "to" + superType + "_" + beforeContent,
																					 "void* _ref",
																					 generateStatement(superType + "Data data") +
																					 generateStatement("data.err = this") + generateStatement(
																							 "return " + superType + " { " + beforeContent + "Type, data }")));
					}

					if (!variants.isEmpty()) {
						final var enumFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + "Type")
								.collect(Collectors.joining(","));

						final var tagType = beforeContent + "Tag";
						final var generatedEnum =
								"enum " + tagType + " {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

						final var unionFields = variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + typeArguments + " " + segment.toLowerCase() +
																";")
								.collect(Collectors.joining());

						final var unionType = beforeContent + "Data";
						final var generatedUnion =
								templateString + "union " + unionType + " {" + unionFields + System.lineSeparator() + "}" +
								System.lineSeparator();

						beforeStruct += generatedEnum + generatedUnion;
						structureFields +=
								generateStatement(tagType + " _tag") + generateStatement(unionType + typeArguments + " _data");
					}

					structureNames.push(beforeContent);
					final var generated = beforeStruct + templateString + "struct " + beforeContent + " {" + structureFields +
																compileStatements(content, Main::compileClassSegment) + System.lineSeparator() + "};" +
																System.lineSeparator();
					structureNames.pop();

					structures.add(generated);
					return Optional.of("");
				}
			}
		}

		return Optional.empty();
	}

	private static String generateFunction(String thisType,
																				 String returnType,
																				 String name,
																				 String params,
																				 String content) {
		return returnType + " " + name + "(" + params + "){" + generateDereferenceThis(thisType) + content +
					 System.lineSeparator() + "}" + System.lineSeparator();
	}

	private static String generateDereferenceThis(String thisType) {
		return generateStatement(thisType + " _this = *((" + thisType + "*) _ref)");
	}

	private static String generateStatement(String content) {
		return System.lineSeparator() + "\t" + content + ";";
	}

	private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (Character.isLetter(c) || (i != 0 && Character.isDigit(c))) {continue;}
			return false;
		}

		return true;
	}

	private static String compileClassSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		final var maybeInterface = compileStructure(stripped, "interface");
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = compileStructure(stripped, "record");
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final var maybeEnum = compileStructure(stripped, "enum");
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var maybeClassStatement = compileClassStatement(substring);
			if (maybeClassStatement.isPresent()) {
				return maybeClassStatement.get();
			}
		}

		return compileMethod(stripped).orElseGet(() -> CPlaceholder.wrap(stripped));
	}

	private static Optional<String> compileMethod(String stripped) {
		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var substring = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var paramString = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				final var header = parseDefinition(substring)
						.<JMethodHeader>map(definition -> definition)
						.or(() -> parseConstructor(substring))
						.orElseGet(() -> new JPlaceholder(substring));

				final var jParameters = Arrays
						.stream(paramString.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::parseDefinition)
						.flatMap(Optional::stream)
						.toList();

				final var cParameters =
						new ArrayList<CDefinable>(jParameters.stream().map(JDefinition::toCDefinition).toList());

				CDefinable outputDefinition;
				switch (header) {
					case JDefinition jDefinition:
						cParameters.addFirst(new CDefinition(new CPointerType(CPrimitiveType.Void), "_ref"));
						outputDefinition = new CDefinition(jDefinition.type, jDefinition.name + "_" + structureNames.peek());
						break;
					default:
						outputDefinition = header.toCDefinition();
						break;
				}

				final var joinedParameters = cParameters.stream().map(CDefinable::generate).collect(Collectors.joining(", "));
				final var headerWithString = outputDefinition.generate() + "(" + joinedParameters + ")";

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					scope.push(jParameters);
					final var compiledContent = compileStatements(content, Main::compileMethodSegment);
					scope.pop();

					final String outputContent;
					if (header instanceof JConstructor(var name)) {
						outputContent = generateStatement(name + " this") + compiledContent + generateStatement("return this");
					} else if (header instanceof JDefinition) {
						outputContent = generateDereferenceThis(structureNames.peek()) + compiledContent;
					} else {
						outputContent = compiledContent;
					}

					final var generated =
							headerWithString + " {" + outputContent + System.lineSeparator() + "}" + System.lineSeparator();

					functions.add(generated);
					return Optional.of("");
				} else {
					final var generated = headerWithString + ";" + System.lineSeparator();
					functions.add(generated);
					return Optional.of("");
				}
			}
		}

		return Optional.empty();
	}

	private static Optional<JMethodHeader> parseConstructor(String input) {
		final var stripped = input.strip();
		return Optional.of(new JConstructor(stripped));
	}

	private static Optional<String> compileClassStatement(String input) {
		return compileDefinition(input).map(Main::generateStatement).or(() -> {
			final var list =
					Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

			final var name = structureNames.peek();
			for (var segment : list) {
				if (!compileEnumValue(segment, name)) {
					return Optional.empty();
				}
			}

			return Optional.of("");
		});
	}

	private static boolean compileEnumValue(String segment, String enumName) {
		final var stripped = segment.strip();
		if (stripped.endsWith(")")) {
			final var substring = stripped.substring(0, stripped.length() - 1);

			final var i = substring.indexOf("(");
			if (i >= 0) {
				final var memberName = substring.substring(0, i);
				final var substring2 = substring.substring(i + 1);

				if (isIdentifier(memberName)) {
					globals.add(enumName + " " + enumName + "_" + memberName + " = new_" + enumName + "(" +
											compileExpression(substring2) + ");" + System.lineSeparator());
					return true;
				}
			}
		}

		return false;
	}

	private static String compileExpression(String input) {
		final var stripped = input.strip();
		if (isIdentifier(stripped)) {
			if (isDefined(stripped)) {
				return stripped;
			} else {
				return CPlaceholder.wrap("Undefined identifier: " + stripped);
			}
		}

		final var i = stripped.lastIndexOf(".");
		if (i >= 0) {
			final var substring = stripped.substring(0, i).strip();
			final var substring1 = stripped.substring(i + 1).strip();
			return compileExpression(substring) + "." + substring1;
		}

		return CPlaceholder.wrap(stripped);
	}

	private static boolean isDefined(String input) {
		if (input.equals("this")) {
			return true;
		}

		return scope
				.stream()
				.map(frame -> frame.stream().filter(definition -> definition.name.equals(input)).findFirst())
				.flatMap(Optional::stream)
				.findFirst()
				.isPresent();
	}

	private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			return generateStatement(compileMethodSegmentValue(substring));
		}

		return CPlaceholder.wrap(stripped);
	}

	private static String compileMethodSegmentValue(String input) {
		if (input.startsWith("return ")) {
			final var slice = input.substring("return ".length());
			return "return " + compileExpression(slice);
		}

		final var i = input.indexOf("=");
		if (i >= 0) {
			final var destination = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			final var s = compileDefinition(destination).orElseGet(() -> compileExpression(destination));

			return s + " = " + compileExpression(substring1);
		}

		return CPlaceholder.wrap(input);
	}

	private static String compileDefinitionOrPlaceholder(String input) {
		return compileDefinition(input).orElseGet(() -> CPlaceholder.wrap(input));
	}

	private static Optional<String> compileDefinition(String input) {
		return parseDefinition(input).map(JDefinition::toCDefinition).map(CDefinable::generate);
	}

	private static Optional<JDefinition> parseDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if (i < 0) {return Optional.empty();}
		final var beforeName = stripped.substring(0, i).strip();
		final var name = stripped.substring(i + 1).strip();

		if (!isIdentifier(name)) {
			return Optional.empty();
		}

		final var i1 = beforeName.lastIndexOf(" ");
		if (i1 >= 0) {
			final var beforeType = beforeName.substring(0, i1);
			final var type = beforeName.substring(i1 + 1);
			return Optional.of(new JDefinition(Optional.of(beforeType), compileType(type), name));
		} else {
			return Optional.of(new JDefinition(Optional.empty(), compileType(beforeName), name));
		}
	}

	private static String compileTypeToString(String input) {
		return compileType(input).generate();
	}

	private static CType compileType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return CPrimitiveType.Void;
		}

		if (stripped.endsWith("[]")) {
			final var cType = compileType(stripped.substring(0, stripped.length() - 2));
			return new CPointerType(cType);
		}

		if (stripped.equals("String")) {
			return new CPointerType(CPrimitiveType.Char);
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var typeArgumentsArray = substring.substring(i + 1).split(Pattern.quote(","));

				final var typeArguments = Arrays
						.stream(typeArgumentsArray)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::compileType)
						.toList();

				return new CTemplateType(base, typeArguments);
			}
		}

		if (isIdentifier(stripped)) {
			return new CIdentifier(stripped);
		}

		return new CPlaceholder(stripped);
	}
}
