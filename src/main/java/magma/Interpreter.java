package magma;

public class Interpreter {
	public static Result<String, InterpreterError> interpret(String source) {
		if (source.isEmpty())
			return new Ok<>("");

		// Handle simple addition expressions like "1 + 2"
		if (source.contains("+")) {
			String[] parts = source.split("\\+");
			if (parts.length == 2) {
				try {
					int a = Integer.parseInt(parts[0].trim());
					int b = Integer.parseInt(parts[1].trim());
					return new Ok<>(String.valueOf(a + b));
				} catch (NumberFormatException ignored) {
					// Try extracting leading digits from each operand (e.g. "1U8")
					String left = parts[0].trim();
					String right = parts[1].trim();
					StringBuilder la = new StringBuilder();
					for (int i = 0; i < left.length(); i++) {
						char c = left.charAt(i);
						if (Character.isDigit(c))
							la.append(c);
						else
							break;
					}
					StringBuilder ra = new StringBuilder();
					for (int i = 0; i < right.length(); i++) {
						char c = right.charAt(i);
						if (Character.isDigit(c))
							ra.append(c);
						else
							break;
					}
					if (la.length() > 0 && ra.length() > 0) {
						int a2 = Integer.parseInt(la.toString());
						int b2 = Integer.parseInt(ra.toString());
						return new Ok<>(String.valueOf(a2 + b2));
					}
				}
			}
		}

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
