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
		final var segments = new ArrayList<String>();
		var buffer = new StringBuilder();
		var depth = 0;

		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';' && depth == 0) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				continue;
			}
			if (c == '}' && depth == 1) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
				depth--;
				continue;
			}
			if (c == '{') {
				depth++;
			}
			if (c == '}') {
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

			variants = Arrays
					.stream(substring1.split(Pattern.quote(",")))
					.map(String::strip)
					.filter(slice -> !slice.isEmpty())
					.toList();
		}

		List<String> typeParameters = new ArrayList<String>();
		final var i3 = beforeContent.indexOf("<");
		if (i3 >= 0) {
			final var substring1 = beforeContent.substring(i3 + 1).strip();
			beforeContent = beforeContent.substring(0, i3);
			if (substring1.endsWith(">")) {
				typeParameters = Arrays
						.stream(substring1.split(Pattern.quote(",")))
						.map(String::strip)
						.filter(slice -> !slice.isEmpty())
						.toList();
			}
		}

		if (!isIdentifier(beforeContent)) {return Optional.empty();}
		String name = beforeContent;
		return Optional.of(wrap(modifiers) + "struct " + name + " {};" + System.lineSeparator() +
											 compileStatements(content, input1 -> compileClassSegment(input1, name)));

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

	private static String compileClassSegment(String input, String structName) {
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
				if (withBraces.startsWith("{") && withBraces.endsWith("}")) {
					final var substring = withBraces.substring(1, withBraces.length() - 1);

					final var compiledParameters = compileDeclaration(parameters, structName);
					return compileDeclaration(declaration, structName) + "(" + compiledParameters + "){" +
								 compileStatements(substring, Main::compileMethodSegment) + System.lineSeparator() + "}" +
								 System.lineSeparator();
				}
			}
		}

		return wrap(stripped);
	}

	private static String compileMethodSegment(String input) {
		final var stripped = input.strip();
		if (stripped.isEmpty()) {
			return "";
		}

		return System.lineSeparator() + "\t" + wrap(stripped);
	}

	private static String compileDeclaration(String input, String structName) {
		final var stripped = input.strip();
		final var nameSeparator = stripped.lastIndexOf(" ");
		if (nameSeparator >= 0) {
			final var beforeName = stripped.substring(0, nameSeparator);
			final var name = stripped.substring(nameSeparator + 1).strip();
			final var typeSeparator = beforeName.lastIndexOf(" ");
			if (typeSeparator >= 0) {
				final var substring = beforeName.substring(0, typeSeparator);
				final var substring1 = beforeName.substring(typeSeparator + 1);
				return wrap(substring) + " " + compileType(substring1) + " " + name + "_" + structName;
			} else {
				return compileType(beforeName) + " " + name;
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
			final var slice = stripped.substring(0, stripped.length() - 2);
			return compileType(slice) + "*";
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
