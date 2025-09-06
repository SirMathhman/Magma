package magma;

import java.util.Objects;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Err<>(new InterpretError("input is absent"));
		}
		String trimmed = input.trim();
		if (trimmed.equals("")) {
			return new Ok<>("");
		}
		// try a very small expression evaluator for binary integer ops: a <op> b
		// support + - * /
		try {
			java.util.regex.Matcher m = java.util.regex.Pattern.compile("^([+-]?\\d+)\\s*([+\\-\\*/])\\s*([+-]?\\d+)$")
					.matcher(trimmed);
			if (m.find()) {
				long a = Long.parseLong(m.group(1));
				String op = m.group(2);
				long b = Long.parseLong(m.group(3));
				long res;
				switch (op) {
					case "+":
						res = a + b;
						break;
					case "-":
						res = a - b;
						break;
					case "*":
						res = a * b;
						break;
					case "/":
						if (b == 0) {
							return new Err<>(new InterpretError("division by zero"));
						}
						res = a / b;
						break;
					default:
						return new Ok<>(trimmed); // shouldn't happen
				}
				return new Ok<>(Long.toString(res));
			}
		} catch (NumberFormatException ex) {
			// fall through to echo
		}
		// fallback: echo input
		return new Ok<>(input);
	}
}
