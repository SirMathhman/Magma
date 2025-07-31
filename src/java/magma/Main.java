package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final String content = Files.readString(Paths.get("src/java/magma/Main.java"));
			final Path targetPath = Path.of("./src/node/magma/Main.ts");
			Files.createDirectories(targetPath.getParent());
			Files.writeString(targetPath, "/*" + System.lineSeparator() + content + System.lineSeparator() + "*/");
		} catch (final IOException e) {
			System.err.println("Error copying file: " + e.getMessage());
		}
	}
}
