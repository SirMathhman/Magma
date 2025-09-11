package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Minimal runner that reads ./src/main/magma/magma/Main.mgs and sends its
 * contents to Interpreter.interpret, printing the result.
 */
public class Main {
	public static void main(String[] args) {
		Path p = Paths.get("src", "main", "magma", "magma", "Main.mgs");
		String src;
		try {
			src = Files.readString(p);
		} catch (IOException e) {
			System.err.println("Failed to read " + p + ": " + e.getMessage());
			System.exit(2);
			return;
		}

		Interpreter interp = new Interpreter();
		Result<String, InterpretError> r = interp.interpret(src);
		if (r instanceof Result.Ok) {
			String v = ((Result.Ok<String, InterpretError>) r).value();
			System.out.println(v);
			System.exit(0);
		} else {
			InterpretError err = ((Result.Err<String, InterpretError>) r).error();
			System.err.println("Interpreter error: " + err.display());
			System.exit(1);
		}
	}
}
