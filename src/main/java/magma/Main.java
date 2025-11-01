package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
		return compileStatements(input, Main::compileRootSegment);
	}

	private static String compileStatements(String input, Function<String, String> mapper) {
		final var segments = new ArrayList<String>();
		var buffer = new StringBuffer();
		var depth = 0;
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
			} else if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
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

		return compileStructure(stripped, "class").orElseGet(() -> wrap(stripped));
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

					String beforeStruct = "";
					if (!variants.isEmpty()) {
						beforeStruct = "enum " + beforeContent + "Tag {" + variants
								.stream()
								.map(segment -> System.lineSeparator() + "\t" + segment + "Type")
								.collect(Collectors.joining(",")) + System.lineSeparator() + "};" + System.lineSeparator();
					}

					return Optional.of(beforeStruct + "struct " + beforeContent + " {};" + System.lineSeparator() +
														 compileStatements(content, Main::compileClassSegment));
				}
			}
		}

		return Optional.empty();
	}

	private static boolean isIdentifier(String input) {
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			if (!Character.isLetter(c)) {
				return false;
			}
		}

		return true;
	}

	private static String compileClassSegment(String input) {
		final var maybeInterface = compileStructure(input, "interface");
		if (maybeInterface.isPresent()) {
			return maybeInterface.get();
		}

		final var i = input.indexOf("(");
		if (i >= 0) {
			final var substring = input.substring(0, i);
			final var substring1 = input.substring(i + 1);
			final var i1 = substring1.indexOf(")");
			if (i1 >= 0) {
				final var substring2 = substring1.substring(0, i1);
				final var withBraces = substring1.substring(i1 + 1).strip();
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var content = withBraces.substring(1, withBraces.length() - 1);
					final var outputContent = compileStatements(content, Main::compileMethodSegment);

					return compileDefinition(substring) + "(" + compileDefinition(substring2) + ") {" + outputContent + "}" +
								 System.lineSeparator();
				}
			}
		}

		return wrap(input);
	}

	private static String compileMethodSegment(String input) {
		return wrap(input);
	}

	private static String compileDefinition(String input) {
		final var stripped = input.strip();
		final var i = stripped.lastIndexOf(" ");
		if (i >= 0) {
			final var substring = stripped.substring(0, i).strip();
			final var name = stripped.substring(i + 1);
			final var i1 = substring.lastIndexOf(" ");
			if (i1 >= 0) {
				final var substring1 = substring.substring(0, i1);
				final var substring2 = substring.substring(i1 + 1);
				return wrap(substring1) + " " + compileType(substring2) + " " + name;
			} else {
				return compileType(substring) + " " + name;
			}
		}

		return wrap(stripped);
	}

	private static String compileType(String input) {
		final var stripped = input.strip();
		if (stripped.equals("void")) {
			return "void";
		}

		if (stripped.endsWith("[]")) {
			return compileType(stripped.substring(0, stripped.length() - 2)) + "*";
		}

		if (stripped.equals("String")) {
			return "char*";
		}

		return wrap(stripped);
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
