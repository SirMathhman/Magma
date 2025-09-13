package magma;

public class Interpreter {
	public static Result<String, InterpreterError> interpret(String source) {
		if (source.isEmpty())
			return new Ok<>("");

		try {
			Integer.parseInt(source);
			return new Ok<>(source);
		} catch (NumberFormatException e) {
			// If the input contains a leading integer followed by non-digits,
			// extract the leading digit sequence without using java.util.regex.
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < source.length(); i++) {
				char c = source.charAt(i);
				if (Character.isDigit(c))
					sb.append(c);
				else
					break;
			}
			if (sb.length() > 0) {
				return new Ok<>(sb.toString());
			}
			return new Err<>(new InterpreterError("Invalid input", source));
		}
	}
}
