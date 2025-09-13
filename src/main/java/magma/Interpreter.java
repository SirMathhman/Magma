package magma;

import java.util.Optional;

public class Interpreter {
	public static Result<String, InterpreterError> interpret(String source) {
		if (source.isEmpty())
			return new Ok<>("");

		// Try addition first
		Optional<Result<String, InterpreterError>> add = tryAddition(source);
		if (add.isPresent())
			return add.get();

		// Try parse as integer
		try {
			Integer.parseInt(source);
			return new Ok<>(source);
		} catch (NumberFormatException e) {
			// Extract leading digits if present
			String lead = leadingDigits(source);
			if (!lead.isEmpty())
				return new Ok<>(lead);
			return new Err<>(new InterpreterError("Invalid input", source));
		}
	}

	private static Optional<Result<String, InterpreterError>> tryAddition(String source) {
		if (!source.contains("+"))
			return Optional.empty();
		String[] parts = source.split("\\+");
		if (parts.length != 2)
			return Optional.empty();

		String left = parts[0].trim();
		String right = parts[1].trim();

		// Try direct integer parse
		try {
			int a = Integer.parseInt(left);
			int b = Integer.parseInt(right);
			return Optional.of(new Ok<>(String.valueOf(a + b)));
		} catch (NumberFormatException ignored) {
			// fall through
		}

		// Try extracting leading digits
		String la = leadingDigits(left);
		String ra = leadingDigits(right);
		if (la.isEmpty() || ra.isEmpty())
			return Optional.empty();

		String suffixLeft = left.substring(la.length());
		String suffixRight = right.substring(ra.length());
		if (!suffixLeft.isEmpty() && !suffixRight.isEmpty() && !suffixLeft.equals(suffixRight)) {
			return Optional.of(new Err<>(new InterpreterError("Invalid input", source)));
		}

		int a2 = Integer.parseInt(la);
		int b2 = Integer.parseInt(ra);
		return Optional.of(new Ok<>(String.valueOf(a2 + b2)));
	}

	private static String leadingDigits(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c))
				sb.append(c);
			else
				break;
		}
		return sb.toString();
	}
}
