package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			final var replaced = input.replace("/*", "start").replace("*/", "end");
			Files.writeString(target, "/*" + replaced + "*/");
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}