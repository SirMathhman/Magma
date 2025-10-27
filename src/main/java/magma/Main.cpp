/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "java", "magma", "Main.java");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.cpp");
			final var replaced = input.replace("start", "start").replace("end", "end");
			Files.writeString(target, "start" + replaced + "end");

			new ProcessBuilder("clang", target.toAbsolutePath().toString(), "-o", "main.exe")
					.inheritIO()
					.start()
					.waitFor();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
*/