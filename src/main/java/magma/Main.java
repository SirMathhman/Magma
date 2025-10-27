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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
	private sealed interface Result<T, X> permits Err, Ok {}

	private record Err<T, X>(X error) implements Result<T, X> {}

	private record Ok<T, X>(T value) implements Result<T, X> {}

	public static void main(String[] args) {
		run().ifPresent(Throwable::printStackTrace);
	}

	private static Optional<IOException> run() {
		final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
		final var input = readString(source);
		return switch (input) {
			case Err<String, IOException> v -> Optional.of(v.error);
			case Ok<String, IOException> v -> compilePath(source, v.value);
		};
	}

	private static Optional<IOException> compilePath(Path source, String input) {
		final var target = source.resolveSibling("Main.cpp");
		final var output = compile(input);
		return writeString(target, output).or(() -> compileNative(target));
	}

	private static Optional<? extends IOException> compileNative(Path target) {
		final var clang = startCommand(List.of("clang", target.toAbsolutePath().toString(), "-o", "main.exe"));
		return switch (clang) {
			case Err<Process, IOException> v1 -> Optional.of(v1.error);
			case Ok<Process, IOException> v1 -> waitForProcess(v1.value);
		};
	}

	private static Optional<? extends IOException> waitForProcess(Process process) {
		return switch (waitFor(process)) {
			case Err<Integer, IOException> v2 -> Optional.of(v2.error);
			case Ok<Integer, IOException> v2 -> {
				System.out.println("Compilation failed with exit code: " + v2.value);
				yield Optional.empty();
			}
		};
	}

	private static Result<Integer, IOException> waitFor(Process process) {
		try {
			return new Ok<Integer, IOException>(process.waitFor());
		} catch (InterruptedException e) {
			return new Err<Integer, IOException>(new IOException(e));
		}
	}

	private static Result<Process, IOException> startCommand(List<String> command) {
		try {
			return new Ok<Process, IOException>(new ProcessBuilder(command).inheritIO().start());
		} catch (IOException e) {
			return new Err<Process, IOException>(e);
		}
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
		return compileStatements(input, Main::compileRootSegment) + "int main(){" + System.lineSeparator() + "\treturn " +
					 "0;" + System.lineSeparator() + "}";
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
			} else {
				if (c == '{') {
					depth++;
				}
				if (c == '}') {
					depth--;
				}
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

		return compileStructure("class", stripped).orElseGet(() -> wrap(input));
	}

	private static Optional<String> compileStructure(String type, String input) {
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
					Optional<String> maybeInterfaceType = Optional.empty();
					if (implementsIndex >= 0) {
						final var slice = beforeContent.substring(implementsIndex + "implements".length()).strip();
						maybeInterfaceType = Optional.of(compileType(slice));
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
						final var enumFields = variants.stream().map(Main::generateField).collect(Collectors.joining(","));

						final var unionFields = variants
								.stream()
								.map(slice -> System.lineSeparator() + "\t" + slice + " " + slice.toLowerCase() + ";")
								.collect(Collectors.joining());

						dependencies = "enum " + beforeContent + "Tag {" + enumFields + System.lineSeparator() + "};" +
													 System.lineSeparator() + templateString + "union " + beforeContent + "Data {" + unionFields +
													 System.lineSeparator() + "};" + System.lineSeparator();
					}

					final String fields;
					if (variants.isEmpty()) {
						fields = "";
					} else {
						fields = generateField(beforeContent + "Tag tag;") + generateField(beforeContent + "Data data");
					}

					if (maybeInterfaceType.isPresent()) {
						final var interfaceType = maybeInterfaceType.get();
						String joinedTypeArguments;
						if (typeParameters.isEmpty()) {
							joinedTypeArguments = "";
						} else {
							joinedTypeArguments = "<" + String.join(", ", typeParameters) + ">";
						}

						dependencies +=
								templateString + interfaceType + " to" + interfaceType + "_" + beforeContent + "(" + beforeContent +
								joinedTypeArguments + "* this" + "){}" + System.lineSeparator();
					}

					return Optional.of(
							dependencies + templateString + "struct " + beforeContent + " {" + fields + System.lineSeparator() +
							"};" + System.lineSeparator() + compileStatements(content, Main::compileClassSegment));
				}
			}
		}

		return Optional.empty();
	}

	private static String generateField(String content) {
		return System.lineSeparator() + "\t" + content;
	}

	private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			if (!Character.isLetter(input.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private static String compileClassSegment(String input) {
		final var maybeInterface = compileStructure("interface", input);
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var maybeRecord = compileStructure("record ", input);
		if (maybeRecord.isPresent()) {
			return maybeRecord.get();
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
					return compileDefinition(definition) + "(" + compileParameters(params) + ") {" +
								 compileStatements(content, Main::compileMethodSegment) + "}";
				}
			}
		}

		return wrap(input);
	}

	private static String compileMethodSegment(String input) {
		return wrap(input);
	}

	private static String compileParameters(String input) {
		if (input.isEmpty()) {
			return "";
		}
		return compileDefinition(input);
	}

	private static String compileDefinition(String input) {
		final var nameSeparator = input.lastIndexOf(" ");
		if (nameSeparator < 0) {
			return wrap(input);
		}

		final var beforeName = input.substring(0, nameSeparator);
		final var name = input.substring(nameSeparator + 1).strip();
		final var typeSeparator = beforeName.lastIndexOf(" ");
		if (typeSeparator >= 0) {
			final var beforeType = beforeName.substring(0, typeSeparator);
			final var type = beforeName.substring(typeSeparator + 1).strip();
			return wrap(beforeType) + " " + compileType(type) + " " + name;
		} else {
			return compileType(beforeName) + " " + name;
		}
	}

	private static String compileType(String input) {
		if (input.equals("void")) {
			return "void";
		}

		if (input.endsWith("[]")) {
			final var slice = input.substring(0, input.length() - 2);
			return compileType(slice) + "*";
		}

		if (input.equals("String")) {
			return "char*";
		}

		if (input.endsWith(">")) {
			final var withoutEnd = input.substring(0, input.length() - 1);
			final var i = withoutEnd.indexOf("<");
			if (i >= 0) {
				final var base = withoutEnd.substring(0, i);
				final var typeArguments = withoutEnd.substring(i + 1);

				final var joined = Arrays
						.stream(typeArguments.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.map(Main::compileType)
						.collect(Collectors.joining(", "));

				return base + "<" + joined + ">";
			}
		}

		if (isIdentifier(input)) {
			return input;
		}

		return wrap(input);
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
