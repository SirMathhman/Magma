package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final var source = Paths.get(".", "src", "main", "magma", "Main.mgs");
			final var input = Files.readString(source);
			final var target = source.resolveSibling("Main.c");
			final var compactC = Compiler.compile(input);
			final var prettyC = CPrettyPrinter.prettyPrint(compactC);
			Files.writeString(target, prettyC);
		} catch (IOException | CompileException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
