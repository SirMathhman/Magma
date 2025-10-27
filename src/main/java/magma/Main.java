package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.cpp");
			Files.writeString(target, compile(input));

			new ProcessBuilder("clang", target.toAbsolutePath().toString(), "-o", "main.exe").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String compile(String input) {
		return wrap(input) + System.lineSeparator() + "int main(){" + System.lineSeparator() + "\treturn 0;" +
					 System.lineSeparator() + "}";
	}

	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}
}
