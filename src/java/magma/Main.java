package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class Main {
	private Main() {}

	private static String wrapInComment(final String content) {
		return "/*" + System.lineSeparator() + content + System.lineSeparator() + "*/";
	}

	private static String splitAndWrapInComments(final String content) {
		final var list = Arrays.stream(content.split(";", -1)).map(Main::wrapInComment).toList();
		return String.join("", list);
	}

	public static void main(final String[] args) {
		try {
			final String content = Files.readString(Paths.get("src/java/magma/Main.java"));
			final Path targetPath = Path.of("./src/node/magma/Main.ts");
			Files.createDirectories(targetPath.getParent());
			Files.writeString(targetPath, Main.splitAndWrapInComments(content));
		} catch (final IOException e) {
			System.err.println("Error copying file: " + e.getMessage());
		}
	}
}
