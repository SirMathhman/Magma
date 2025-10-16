package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final Path diagram = Paths.get(".", "diagram.puml");
			final String content = "@startuml" + System.lineSeparator() + "@enduml";
			Files.writeString(diagram, content);
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
