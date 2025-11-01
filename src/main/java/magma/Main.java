package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "main", "java", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "src", "main", "windows", "magma", "Main.c"), compile(input));
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		final var segments = new ArrayList<String>();
		var buffer = new StringBuffer();
		for (var i = 0; i < input.length(); i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (c == ';') {
				segments.add(buffer.toString());
				buffer = new StringBuffer();
			}
		}
		segments.add(buffer.toString());

		return segments.stream().map(Main::compileRootSegment).collect(Collectors.joining());
	}

	private static String compileRootSegment(String input) {
		final var stripped = input.strip();
		if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
			return "";
		}

		return wrap(stripped);
	}

	private static String wrap(String input) {
		final var replaced = input.replace("/*", "start").replace("*/", "end");
		return "/*" + replaced + "*/";
	}
}
