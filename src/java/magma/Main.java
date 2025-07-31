package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final String content = Files.readString(Paths.get("src/java/magma/Main.java"));
			System.out.println(content);
		} catch (final IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
		}
	}
}
