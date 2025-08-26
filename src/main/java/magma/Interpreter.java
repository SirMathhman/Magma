package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		String trimmed = input == null ? "" : input.trim();
		// integer literal
		try {
			Integer.parseInt(trimmed);
			return new Ok<>(trimmed);
		} catch (NumberFormatException e) {
			// try minimal let-binding form: "let <name> = <int>; <name>"
			if (trimmed.startsWith("let ")) {
				String[] parts = trimmed.split(";", 2);
				if (parts.length == 2) {
					String decl = parts[0].trim();
					String use = parts[1].trim();
					if (decl.startsWith("let ") && decl.contains("=")) {
						String[] kv = decl.substring(4).split("=", 2);
						String name = kv[0].trim();
						String rhs = kv[1].trim();
						try {
							Integer.parseInt(rhs);
							if (name.equals(use)) {
								return new Ok<>(rhs);
							}
						} catch (NumberFormatException ignore) {
							// fall through to error
						}
					}
				}
			}
			return new Err<>(new InterpretError("Undefined value", input));
		}
	}
}
