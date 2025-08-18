package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
	public static int run(String input) throws IOException {
		final var output = Compiler.compile(input);
		final var temp = Files.createTempFile("main", ".c");
		Files.writeString(temp, output);

		final var process = new ProcessBuilder("clang", "-o", "main", temp.toString())
				.inheritIO()
				.start();
		
		return 0;
	}
}
