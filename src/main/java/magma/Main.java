package magma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) {
		String defaultPath = ".\\src\\main\\magma\\magma\\Interpreter.mgs";
		String path = args.length > 0 ? args[0] : defaultPath;
		try {
			String content = Files.readString(Path.of(path), StandardCharsets.UTF_8);
			Interpreter interp = new Interpreter();
			String result = interp.interpret(content);
			System.out.println(result == null ? "" : result);
		} catch (InterpretException e) {
			System.err.println("Interpreter error: " + e.getMessage());
			System.exit(2);
		} catch (Exception e) {
			System.err.println("Error reading or running interpreter: " + e.getMessage());
			System.exit(1);
		}
	}
}
