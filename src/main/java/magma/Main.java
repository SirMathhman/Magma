package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	private sealed interface Result<T, X> permits Err, Ok {
		<R> Result<R, X> mapValue(Function<T, R> mapper);
	}

	private record Err<T, X>(X error) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(Function<T, R> mapper) {
			return new Err<R, X>(this.error);
		}
	}

	private record Ok<T, X>(T value) implements Result<T, X> {
		@Override
		public <R> Result<R, X> mapValue(Function<T, R> mapper) {
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

	public static void main(String[] args) {
		run().ifPresent(Throwable::printStackTrace);
	}

	private static Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var target = source.resolveSibling("Main.cpp");
		final var input = readString(source).mapValue(Main::compile);

		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> writeString(target, v.value);
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
		return compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(String input, Function<String, String> mapper) {
		return compileAll(input, mapper, Main::foldStatement, "");
	}

	private static String compileAll(String input,
																	 Function<String, String> mapper,
																	 BiFunction<State, Character, State> folder,
																	 String delimiter) {
		return divide(input, folder).map(mapper).collect(Collectors.joining(delimiter));
	}

	private static Stream<String> divide(String input, BiFunction<State, Character, State> folder) {
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

	private static State foldStatement(State current, Character next) {
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

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return compileStructure("class", stripped).orElseGet(() -> wrap(stripped));
	}

	private static Optional<String> compileStructure(String type, String stripped) {
		final var i = stripped.indexOf(type + " ");
		if (i < 0) {return Optional.empty();}
		final var modifiers = stripped.substring(0, i).strip();
		final var afterKeyword = stripped.substring(i + (type + " ").length()).strip();

		final var i1 = afterKeyword.indexOf("{");
		if (i1 < 0) {return Optional.empty();}
		var beforeContent = afterKeyword.substring(0, i1).strip();
		final var content = afterKeyword.substring(i1 + 1);

		List<String> variants = new ArrayList<String>();
		final var i2 = beforeContent.indexOf("permits ");
		if (i2 >= 0) {
			final var substring1 = beforeContent.substring(i2 + "permits ".length());
			beforeContent = beforeContent.substring(0, i2);

			variants = splitValues(substring1);
		}

		List<String> typeParameters = new ArrayList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			beforeContent = beforeContent.substring(0, i3);
			if (substring1.endsWith(">")) {
				final var substring = substring1.substring(0, substring1.length() - 1);
				typeParameters = splitValues(substring);
			}
		}

		if (!isIdentifier(beforeContent)) {return Optional.empty();}

		final var modifiersList = Arrays
				.stream(modifiers.split(Pattern.quote(" ")))
				.map(String::strip)
				.filter(slice -> !slice.isEmpty())
				.collect(Collectors.toCollection(ArrayList::new));
		var name = beforeContent;

		final var templateString = generateTemplateString(typeParameters);

		final String fields;
		final String dependencies;
		if (!variants.isEmpty() && modifiersList.contains("sealed")) {
			modifiersList.remove("sealed");

			final var enumFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Variant")
					.collect(Collectors.joining(", "));

			final var generatedEnum =
					"enum " + name + "Variant {" + enumFields + System.lineSeparator() + "};" + System.lineSeparator();

			final String joinedTypeParameters;
			if (typeParameters.isEmpty()) {
				joinedTypeParameters = "";
			} else {
				joinedTypeParameters = typeParameters.stream().collect(Collectors.joining(", ", "<", ">"));
			}

			final var unionFields = variants
					.stream()
					.map(variant -> System.lineSeparator() + "\t" + variant + "Data" + joinedTypeParameters + " " +
													variant.toLowerCase() + ";")
					.collect(Collectors.joining());

			final var generatedUnion =
					templateString + "union " + name + "Data {" + unionFields + System.lineSeparator() + "};" +
					System.lineSeparator();

			fields = System.lineSeparator() + "\t" + name + "Variant variant;" + System.lineSeparator() + "\t" + name +
							 "Data data;";

			dependencies = generatedEnum + generatedUnion;
		} else {
			fields = "";
			dependencies = "";
		}

		final String joinedModifiers;
		if (modifiersList.isEmpty()) {
			joinedModifiers = "";
		} else {
			joinedModifiers =
					modifiersList.stream().map(Main::wrap).map(modifier -> modifier + " ").collect(Collectors.joining());
		}

		var finalTypeParameters = typeParameters;
		return Optional.of(
				dependencies + templateString + joinedModifiers + "struct " + name + " {" + fields + System.lineSeparator() +
				"};" + System.lineSeparator() +
				compileStatements(content, input1 -> compileClassSegment(input1, name, finalTypeParameters)));

	}

	private static List<String> splitValues(String input) {
		return Arrays.stream(input.split(Pattern.quote(","))).map(String::strip).filter(slice -> !slice.isEmpty()).toList();
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

	private static boolean isIdentifier(String input) {
		final var stripped = input.strip();
		for (var i = 0; i < stripped.length(); i++) {
			final var c = stripped.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}

	private static String compileClassSegment(String input, String structName, List<String> typeParameters) {
		final var stripped = input.strip();

		final var maybeInterface = compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var i = stripped.indexOf("(");
		if (i >= 0) {
			final var declaration = stripped.substring(0, i);
			final var substring1 = stripped.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var parameters = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();

				final var compiledParameters = divide(parameters, Main::foldValue)
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList()
						.stream()
						.map(param -> compileDeclaration(param, structName, typeParameters))
						.collect(Collectors.joining(", "));

				final var header = compileDeclaration(declaration, structName, typeParameters) + "(" + compiledParameters +
													 ")";

				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);

					return header + "{" + compileStatements(content, Main::compileMethodSegment) + System.lineSeparator() + "}" +
								 System.lineSeparator();
				} else {
					return header + ";" + System.lineSeparator();
				}
			}
		}

		return wrap(stripped);
	}

	private static State foldValue(State state, Character next) {
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

	private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private static String compileDeclaration(String input, String structName, List<String> typeParameters) {
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

			if (typeSeparator < 0) {
				return compileType(beforeName) + " " + name;
			}

			var beforeType = beforeName.substring(0, typeSeparator).strip();

			final var copy = new ArrayList<String>(typeParameters);
			if (beforeType.endsWith(">")) {
				final var substring = beforeType.substring(0, beforeType.length() - 1);
				final var i = substring.indexOf("<");
				if (i >= 0) {
					final var substring2 = substring.substring(i + 1);
					copy.addAll(splitValues(substring2));

					beforeType = substring.substring(0, i);
				}
			}

			var beforeDeclaration = generateTemplateString(copy);

			final var typeString = beforeName.substring(typeSeparator + 1);
			final String beforeTypeOutput;
			if (beforeType.isEmpty()) {
				beforeTypeOutput = "";
			} else {
				beforeTypeOutput = wrap(beforeType) + " ";
			}

			return beforeDeclaration + beforeTypeOutput + compileType(typeString) + " " + name + "_" + structName;
		}

		return wrap(stripped);
	}

	private static String compileType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return "void";
		}

		if (stripped.endsWith("[]")) {
			final var slice = stripped.substring(0, stripped.length() - 2);
			return compileType(slice) + "*";
		}

		if (stripped.equals("String")) {
			return "char*";
		}

		if (stripped.endsWith(">")) {
			final var substring = stripped.substring(0, stripped.length() - 1);
			final var i = substring.indexOf("<");
			if (i >= 0) {
				final var base = substring.substring(0, i);
				final var parameters = substring.substring(i + 1);
				final var typeArguments = compileValues(parameters, Main::compileType);
				return base + "<" + typeArguments + ">";
			}
		}

		if (isIdentifier(stripped)) {
			return stripped;
		}

		return wrap(stripped);
	}

	private static String compileValues(String input, Function<String, String> mapper) {
		return compileAll(input, mapper, Main::foldValue, ", ");
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
