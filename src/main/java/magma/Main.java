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
			final String replaced = input.replace("/*", "start").replace("*/", "end");
			Files.writeString(source.resolveSibling("main.c"), "/*" + replaced + "*/");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
