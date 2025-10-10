package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final Path source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final String input = Files.readString(source);
			final Path target = source.resolveSibling("main.cpp");
			Files.writeString(target, "/*" + input.replace("/*", "start").replace("*/", "end") + "*/");
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}