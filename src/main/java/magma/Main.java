package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final Path path = Paths.get(".", "docs", "diagrams");
			if (!Files.exists(path)) Files.createDirectories(path);

			final Path architecture = path.resolve("architecture.puml");
			Files.writeString(architecture,
												"@startuml" + System.lineSeparator() + "class Main" + System.lineSeparator() + "@end uml");
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
