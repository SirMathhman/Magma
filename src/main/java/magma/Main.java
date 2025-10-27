package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.cpp");
			Files.writeString(target, compile(input));

			new ProcessBuilder("clang", target.toAbsolutePath().toString(), "-o", "main.exe").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
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

		final var classIndex = stripped.indexOf("class");
		if (classIndex >= 0) {
			final var afterKeyword = stripped.substring(classIndex + "class".length());
			final var contentStart = afterKeyword.indexOf("{");
			if (contentStart >= 0) {
				final var name = afterKeyword.substring(0, contentStart).strip();
				if (isIdentifier(name)) {
					final var withEnd = afterKeyword.substring(contentStart + "{".length()).strip();
					if (withEnd.endsWith("}")) {
						final var content = withEnd.substring(0, withEnd.length() - 1);
						return "struct " + name + " {};" + System.lineSeparator() +
									 compileStatements(content, Main::compileClassSegment);
					}
				}
			}
		}

		return wrap(input);
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
		final var paramStart = input.indexOf("(");
		if (paramStart >= 0) {
			final var definition = input.substring(0, paramStart).strip();
			final var withParams = input.substring(paramStart + 1);
			final var paramEnd = withParams.indexOf(")");
			if (paramEnd >= 0) {
				final var params = withParams.substring(0, paramEnd).strip();
				final var content = withParams.substring(paramEnd + 1);
				return compileDefinition(definition) + "(" + compileParameters(params) + ")" + wrap(content);
			}
		}

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
		return wrap(input);
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
