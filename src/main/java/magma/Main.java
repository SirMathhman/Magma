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
		return wrap(input);
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
