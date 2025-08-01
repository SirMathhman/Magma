package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

			final var targetParent = Paths.get(".", "src", "windows", "magma");
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

			final var target = targetParent.resolve("Main.c");

			final var segments = Main.divide(input);
			final var joined = segments.stream().map(Main::generatePlaceholder).collect(Collectors.joining());
			Files.writeString(target, joined);
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static ArrayList<String> divide(final CharSequence input) {
		final var segments = new ArrayList<String>();
		var buffer = new StringBuilder();
		final var length = input.length();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			buffer.append(c);
			if (';' == c) {
				segments.add(buffer.toString());
				buffer = new StringBuilder();
			}
		}
		segments.add(buffer.toString());
		return segments;
	}

	private static String generatePlaceholder(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
