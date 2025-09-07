package magma;

import magma.interpret.Interpreter;
import magma.interpret.InterpretError;
// result wrapper types are only used through Interpreter.interpret's return

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

/**
 * Simple command-line runner for the interpreter.
 * Reads the file `src/main/magma/magma/Main.mgs` by default (or the first
 * command-line argument if provided), feeds it to the Interpreter, and prints
 * the resulting value or error message.
 */
public final class Main {

	public static void main(String[] args) {
		Path path = (args.length > 0)
				? Path.of(args[0])
				: Path.of("src", "main", "magma", "magma", "Main.mgs");

		String input;
		try {
			input = Files.readString(path);
		} catch (IOException e) {
			System.err.println("Failed to read file '" + path + "': " + e.getMessage());
			// exit with non-zero to indicate failure
			System.exit(2);
			return;
		}

		Interpreter interpreter = new Interpreter();
		Result<String, InterpretError> res = interpreter.interpret(input);

		if (res instanceof Ok<String, InterpretError> ok) {
			System.out.println(ok.value());
			return;
		}
		if (res instanceof Err<String, InterpretError> er) {
			System.err.println("Error: " + er.error().display());
			System.exit(1);
			return;
		}
		// Should not happen, but print something useful
		System.err.println("Interpreter returned unexpected result");
		System.exit(3);
	}
}
