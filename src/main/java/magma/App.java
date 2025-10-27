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
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
	private enum CPPPrimitiveType implements CPPType {
		Void("void"), Char("char");

		private final String content;

		CPPPrimitiveType(String content) {this.content = content;}

		@Override
		public String generate() {
			return this.content;
		}

		@Override
		public String getSimpleName() {
			return this.content;
		}
	}

	private sealed interface Result<T, X> permits Err, Ok {}

	private sealed interface CPPType permits CIdentifier, CPPPrimitiveType, CPointerType, CTemplateType, Placeholder {
		String generate();

		String getSimpleName();
	}

	private record Err<T, X>(X error) implements Result<T, X> {}

	private record Ok<T, X>(T value) implements Result<T, X> {}

	private record CPointerType(CPPType type) implements CPPType {
		@Override
		public String generate() {
			return this.type.generate() + "*";
		}

		@Override
		public String getSimpleName() {
			return this.type.getSimpleName() + "_ref";
		}
	}

	public record CTemplateType(String base, List<CPPType> list) implements CPPType {
		@Override
		public String generate() {
			final var joined = this.list.stream().map(CPPType::generate).collect(Collectors.joining(", "));

			return this.base + "<" + joined + ">";
		}

		@Override
		public String getSimpleName() {
			return this.base;
		}
	}

	private record CIdentifier(String input) implements CPPType {
		@Override
		public String generate() {
			return this.input;
		}

		@Override
		public String getSimpleName() {
			return this.input;
		}
	}

	private record Placeholder(String input) implements CPPType {
		private static String wrap(String input) {
			return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
		}

		@Override
		public String generate() {
			return wrap(this.input);
		}

		@Override
		public String getSimpleName() {
			return this.generate();
		}
	}

	private record Tuple<A, B>(A left, B right) {}

	private class State {
		public final String input;
		public final ArrayList<String> segments;
		private StringBuilder buffer;
		private int depth;
		private int index = 0;

		public State(String input) {
			this.input = input;
			this.buffer = new StringBuilder();
			this.depth = 0;
			this.segments = new ArrayList<String>();
		}

		State enter() {
			this.depth = this.depth + 1;
			return this;
		}

		State exit() {
			this.depth = this.depth - 1;
			return this;
		}

		State advance() {
			this.segments.add(this.buffer.toString());
			this.buffer = new StringBuilder();
			return this;
		}

		boolean isShallow() {
			return this.depth == 1;
		}

		State append(char c) {
			this.buffer.append(c);
			return this;
		}

		boolean isLevel() {
			return this.depth == 0;
		}

		public Optional<Character> pop() {
			if (this.index < this.input.length()) {
				var counter = this.index;
				this.index++;
				final var element = this.input.charAt(counter);
				return Optional.of(element);
			} else {
				return Optional.empty();
			}
		}

		public Stream<String> stream() {
			return this.segments.stream();
		}

		public Optional<Tuple<Character, State>> popAndAppendToTuple() {
			return this.pop().map(next -> {
				final var appended = this.append(next);
				return new Tuple<Character, State>(next, appended);
			});
		}

		public Optional<State> popAndAppendToOption() {
			return this.popAndAppendToTuple().map(Tuple::right);
		}
	}

	private final List<String> globals;
	private final List<String> forwardDeclarations;
	private final List<String> functions;
	private final List<String> structures;
	private final List<String> sealedStructures;
	private final Stack<String> structureNames;

	public App() {
		this.globals = new ArrayList<String>();
		this.structureNames = new Stack<String>();
		this.functions = new ArrayList<String>();
		this.forwardDeclarations = new ArrayList<String>();
		this.structures = new ArrayList<String>();
		this.sealedStructures = new ArrayList<String>();
	}

	public static void main(String[] args) {
		new App().run().ifPresent(Throwable::printStackTrace);
	}

	private Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "App.java");
		final var input = this.readString(source);
		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> this.compilePath(source, v.value);
		};
	}

	private Optional<IOException> compilePath(Path source, String input) {
		final var target = source.resolveSibling("App.cpp");
		final var output = this.compile(input);
		return this.writeString(target, output).or(() -> this.compileNative(target));
	}

	private Optional<? extends IOException> compileNative(Path target) {
		final var clang = this.startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
		return switch (clang) {
			case Err<Process, IOException> v1 -> Optional.of(v1.error);
			case Ok<Process, IOException> v1 -> this.waitForProcess(v1.value);
		};
	}

	private Optional<IOException> waitForProcess(Process process) {
		return switch (this.waitFor(process)) {
			case Err<Integer, IOException> v2 -> Optional.of(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield Optional.empty();
			}
		};
	}

	private Result<Integer, IOException> waitFor(Process process) {
		try {
			return new Ok<Integer, IOException>(process.waitFor());
		} catch (InterruptedException e) {
			return new Err<Integer, IOException>(new IOException(e));
		}
	}

	private Result<Process, IOException> startCommand(List<String> command) {
		try {
			return new Ok<Process, IOException>(new ProcessBuilder(command).inheritIO().start());
		} catch (IOException e) {
			return new Err<Process, IOException>(e);
		}
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
		final var compiled = this.compileStatements(input, this::compileRootSegment);

		final var joinedForwardDeclarations = String.join("", this.forwardDeclarations);
		final var joinedFunctions = String.join("", this.functions);

		final var joinedStructures = String.join("", this.structures);
		final var joinedSealedStructures = String.join("", this.sealedStructures);
		final var joinedGlobals = String.join("", this.globals);

		return joinedForwardDeclarations + compiled + joinedStructures + joinedSealedStructures + joinedGlobals +
					 joinedFunctions + "int main(){" + System.lineSeparator() + "\treturn " + "0;" + System.lineSeparator() +
					 "}";
	}

	private String compileStatements(String input, Function<String, String> mapper) {
		return this.divide(new State(input)).map(mapper).collect(Collectors.joining());
	}

	private Stream<String> divide(State state) {
		var current = state;
		while (true) {
			final var maybeNext = current.pop();
			if (maybeNext.isEmpty()) {
				break;
			}

			current = this.foldEscaped(current, maybeNext.get());
		}

		return current.advance().stream();
	}

	private State foldEscaped(State current, char next) {
		if (next == '\'') {
			return current
					.append(next)
					.popAndAppendToTuple()
					.map(this::foldSingleEscapeChar)
					.flatMap(State::popAndAppendToOption)
					.orElse(current);
		}

		if (next == '\"') {
			var current0 = current.append(next);
			while (true) {
				final var maybeTuple = current0.popAndAppendToTuple();
				if (maybeTuple.isEmpty()) {
					break;
				}

				final var tuple = maybeTuple.get();
				current0 = tuple.right;

				final var nextInQuotes = tuple.left;
				if (nextInQuotes == '\\') {
					current0 = current0.popAndAppendToOption().orElse(current0);
					continue;
				}

				if (nextInQuotes == '\"') {
					break;
				}
			}

			return current0;
		}

		return this.fold(current, next);
	}

	private State foldSingleEscapeChar(Tuple<Character, State> tuple) {
		if (tuple.left == '\\') {
			return tuple.right.popAndAppendToOption().orElse(tuple.right);
		}
		return tuple.right;
	}

	private State fold(State state, Character c) {
		final var appended = state.append(c);
		if (c == ';' && appended.isLevel()) {
			return appended.advance();
		} else if (c == '}' && appended.isShallow()) {
			return appended.advance().exit();
		}
		if (c == '{') {
			return appended.enter();
		}
		if (c == '}') {
			return appended.exit();
		}
		return appended;
	}

	private String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return this.compileStructure("class", stripped).orElseGet(() -> Placeholder.wrap(input));
	}

	private Optional<String> compileStructure(String type, String input) {
		final var classIndex = input.indexOf(type);
		if (classIndex >= 0) {
			final var afterKeyword = input.substring(classIndex + type.length());
			final var contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				var beforeContent = afterKeyword.substring(0, contentStart).strip();
				final var withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
				if (withEnd.endsWith("}")) {
					final var content = withEnd.substring(0, withEnd.length() - 1);

					final var permitsIndex = beforeContent.indexOf("permits");
					List<String> variants = Collections.emptyList();
					if (permitsIndex >= 0) {
						final var variantsArray =
								beforeContent.substring(permitsIndex + "permits".length()).split(Pattern.quote(","));
						beforeContent = beforeContent.substring(0, permitsIndex).strip();
						variants = Arrays.stream(variantsArray).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
					}

					final var implementsIndex = beforeContent.indexOf("implements");
					Optional<CPPType> maybeInterfaceType = Optional.empty();
					if (implementsIndex >= 0) {
						final var slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
						maybeInterfaceType = this.compileType(slice);
						beforeContent = beforeContent.substring(0, implementsIndex).strip();
					}

					if (beforeContent.endsWith(")")) {
						final var slice = beforeContent.substring(0, beforeContent.length() - 1);
						final var i = slice.indexOf("(");
						if (i >= 0) {
							final var params = slice.substring(i + 1);
							beforeContent = slice.substring(0, i).strip();
						}
					}

					List<String> typeParameters = new ArrayList<String>();
					if (beforeContent.endsWith(">")) {
						final var withoutEnd = beforeContent.substring(0, beforeContent.length() - 1);
						final var typeParamStart = withoutEnd.indexOf("<");
						if (typeParamStart >= 0) {
							beforeContent = withoutEnd.substring(0, typeParamStart);
							final var typeParamsArray = withoutEnd.substring(typeParamStart + 1).split(Pattern.quote(","));
							typeParameters =
									Arrays.stream(typeParamsArray).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
						}
					}

					if (!this.isIdentifier(beforeContent)) {
						return Optional.empty();
					}

					String templateString;
					if (typeParameters.isEmpty()) {
						templateString = "";
					} else {
						final var collect =
								typeParameters.stream().map(slice -> "typename " + slice).collect(Collectors.joining(", "));
						templateString = "template <" + collect + ">" + System.lineSeparator();
					}

					String dependencies;
					if (variants.isEmpty()) {
						dependencies = "";
					} else {
						final var enumFields = variants
								.stream()
								.map(slice -> slice + "Tag")
								.map(this::generateWithIndent)
								.collect(Collectors.joining(","));

						final var typeArguments = this.joinTypeArguments(typeParameters);
						final var unionFields = variants
								.stream()
								.map(slice -> System.lineSeparator() + "\t" + slice + typeArguments + " " + slice.toLowerCase() + ";")
								.collect(Collectors.joining());

						dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" +
													 System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields +
													 System.lineSeparator() + "};" + System.lineSeparator();
					}

					final String fields;
					if (variants.isEmpty()) {
						fields = "";
					} else {
						fields = this.generateStatement(beforeContent + "Tag tag") +
										 this.generateStatement(beforeContent + "Data " + "data");
					}

					if (maybeInterfaceType.isPresent()) {
						final var interfaceType = maybeInterfaceType.get();
						final var joinedTypeArguments = this.joinTypeArguments(typeParameters);

						final var thisType = beforeContent + joinedTypeArguments;
						this.functions.add(templateString + interfaceType.generate() + " to" + interfaceType.getSimpleName() +
															 "_" +
															 beforeContent + "(void* _ref" + "){" +
															 this.generateStatement(thisType + " _this = *((" + thisType + "*) _ref)") +
															 this.generateStatement(
																	 interfaceType.getSimpleName() + "Data" + joinedTypeArguments + " data") +
															 this.generateStatement("data." + beforeContent.toLowerCase() + " = _this") +
															 this.generateStatement(
																	 "return " + interfaceType.generate() + " { " + beforeContent + "Tag, " + "data }") +
															 System.lineSeparator() + "}" + System.lineSeparator());
					}

					this.forwardDeclarations.add(templateString + "struct " + beforeContent + ";" + System.lineSeparator());

					this.structureNames.push(beforeContent);
					final var generated =
							dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() +
							this.compileStatements(content, this::compileClassSegment) + "};" + System.lineSeparator();
					this.structureNames.pop();

					if (variants.isEmpty()) {
						this.structures.add(generated);
					} else {
						this.sealedStructures.add(generated);
					}

					return Optional.of("");
				}
			}
		}

		return Optional.empty();
	}

	private String joinTypeArguments(List<String> typeParameters) {
		String joinedTypeArguments;
		if (typeParameters.isEmpty()) {
			joinedTypeArguments = "";
		} else {
			joinedTypeArguments = "<" + String.join(", ", typeParameters) + ">";
		}
		return joinedTypeArguments;
	}

	private String generateStatement(String content) {
		return this.generateWithIndent(content) + ";";
	}

	private String generateWithIndent(String content) {
		return System.lineSeparator() + "\t" + content;
	}

	private boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			if (!Character.isLetter(input.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private String compileClassSegment(String input) {
		if (input.isEmpty()) {
			return "";
		}

		final var maybeInterface = this.compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = this.compileStructure("record", input);
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
		}

		final var maybeEnum = this.compileStructure("enum", input);
		if (maybeEnum.isPresent()) {
			return maybeEnum.get();
		}

		final var paramStart = input.indexOf("(");
		if (paramStart >= 0) {
			final var definition = input.substring(0, paramStart).strip();
			final var withParams = input.substring(paramStart + 1);
			final var paramEnd = withParams.indexOf(")");
			if (paramEnd >= 0) {
				final var params = withParams.substring(0, paramEnd).strip();
				final var withBraces = withParams.substring(paramEnd + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					final var header = this
							.compileDefinition(definition)
							.or(() -> this.compileConstructor(definition))
							.orElseGet(() -> Placeholder.wrap(definition));

					final var generated = header + "(" + this.compileParameters(params) + ") {" +
																this.compileStatements(content, this::compileMethodSegment) + "}" +
																System.lineSeparator();

					this.functions.add(generated);
					return "";
				}
			}
		}

		if (input.endsWith(";")) {
			final var slice = input.substring(0, input.length() - 1);
			return this
					.compileEnumValues(slice)
					.orElseGet(() -> this.generateStatement(this
																											.compileDefinition(slice)
																											.orElseGet(() -> Placeholder.wrap(slice))));

		}

		return Placeholder.wrap(input);
	}

	private Optional<String> compileConstructor(String input) {
		final var i = input.lastIndexOf(" ");
		if (i >= 0) {
			final var name = input.substring(i + 1).strip();
			if (this.isIdentifier(name)) {
				final var structName = this.structureNames.peek();
				return Optional.of(structName + " new_" + structName);
			}
		} else {
			if (this.isIdentifier(input)) {
				final var structName = this.structureNames.peek();
				return Optional.of(structName + " new_" + structName);
			}
		}

		return Optional.empty();
	}

	private Optional<String> compileEnumValues(String input) {
		final var segments =
				Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();

		for (var segment : segments) {
			final var stripped = segment.strip();
			final var maybeEnumValue = this.compileEnumValue(stripped);
			if (maybeEnumValue.isPresent()) {
				this.globals.add(maybeEnumValue.get());
			} else {
				return Optional.empty();
			}
		}

		return Optional.of("");
	}

	private Optional<String> compileEnumValue(String stripped) {
		if (stripped.endsWith(")")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var name = slice.substring(0, i).strip();
				final var arguments = slice.substring(i + 1);
				if (this.isIdentifier(name)) {
					final var structureName = this.structureNames.peek();
					return Optional.of(structureName + " " + name + "Value = " + structureName + " { " + arguments + " };" +
														 System.lineSeparator());
				}
			}
		}

		return Optional.empty();
	}

	private String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		if (stripped.endsWith(";")) {
			final var slice = stripped.substring(0, stripped.length() - 1);
			return this.generateStatement(this.compileMethodStatement(slice));
		}

		return Placeholder.wrap(input);
	}

	private String compileMethodStatement(String input) {
		if (input.startsWith("return ")) {
			final var slice = input.substring("return ".length()).strip();
			return "return " + this.compileExpression(slice);
		}

		return Placeholder.wrap(input);
	}

	private String compileExpression(String input) {
		if (input.endsWith(")")) {
			final var slice = input.substring(0, input.length() - 1);
			final var i = slice.indexOf("(");
			if (i >= 0) {
				final var caller = slice.substring(0, i);
				final var arguments = slice.substring(i + 1);
				return this.compileExpression(caller) + "(" + this.compileExpression(arguments) + ")";
			}
		}

		final var i = input.indexOf(".");
		if (i >= 0) {
			final var child = input.substring(0, i).strip();
			final var name = input.substring(i + 1);
			return this.compileExpression(child) + "." + name;
		}

		if (this.isIdentifier(input)) {
			return input;
		}

		return Placeholder.wrap(input);
	}

	private String compileParameters(String input) {
		if (input.isEmpty()) {
			return "";
		}
		return this.compileDefinitionOrPlaceholder(input);
	}

	private String compileDefinitionOrPlaceholder(String input) {
		return this.compileDefinition(input).orElseGet(() -> Placeholder.wrap(input));
	}

	private Optional<String> compileDefinition(String input) {
		final var nameSeparator = input.lastIndexOf(" ");
		if (nameSeparator < 0) {
			return Optional.empty();
		}

		final var beforeName = input.substring(0, nameSeparator);
		final var name = input.substring(nameSeparator + 1).strip();
		final var typeSeparator = beforeName.lastIndexOf(" ");
		if (typeSeparator >= 0) {
			final var type = beforeName.substring(typeSeparator + 1).strip();
			return this.compileType(type).map(cppType -> cppType.generate() + " " + name);
		}

		return this.compileType(beforeName).map(cppType -> cppType.generate() + " " + name);
	}

	private Optional<CPPType> compileType(String input) {
		if (input.equals("void")) {
			return Optional.of(CPPPrimitiveType.Void);
		}

		if (input.endsWith("[]")) {
			final var slice = input.substring(0, input.length() - 2);
			return this.compileType(slice).map(CPointerType::new);
		}

		if (input.equals("String")) {
			return Optional.of(new CPointerType(CPPPrimitiveType.Char));
		}

		if (input.endsWith(">")) {
			final var withoutEnd = input.substring(0, input.length() - 1);
			final var i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final var base = withoutEnd.substring(0, i);
				final var typeArguments = withoutEnd.substring(i + 1);

				final var list = Arrays
						.stream(typeArguments.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(this::compileType)
						.flatMap(Optional::stream)
						.toList();

				return Optional.of(new CTemplateType(base, list));
			}
		}

		if (this.isIdentifier(input)) {
			if (input.equals("public")) {
				return Optional.empty();
			}

			return Optional.of(new CIdentifier(input));
		}

		return Optional.of(new Placeholder(input));
	}
}
