package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
		final var segments = new ArrayList<String>();
		var buffer = new StringBuilder();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';') {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			}
		}
		segments.add(buffer.toString());

		final var joined = segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());

		return joined + System.lineSeparator() + "int main(){" + System.lineSeparator() + "\treturn 0;" +
					 System.lineSeparator() + "}";
	}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return wrap(input);
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
