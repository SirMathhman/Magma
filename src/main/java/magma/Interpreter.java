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
		if (parts.length < 2)
			return Optional.empty();

		int sum = 0;
		String commonSuffix = "";
		boolean anyParsed = false;

		for (String raw : parts) {
			String part = raw.trim();

			// Try direct integer parse
			try {
				int v = Integer.parseInt(part);
				sum += v;
				anyParsed = true;
				continue;
			} catch (NumberFormatException ignored) {
				// fall through
			}

			String ld = leadingDigits(part);
			if (ld.isEmpty())
				return Optional.empty();

			String suffix = part.substring(ld.length());
			if (!suffix.isEmpty()) {
				if (commonSuffix.isEmpty())
					commonSuffix = suffix;
				else if (!commonSuffix.equals(suffix)) {
					return Optional.of(new Err<>(new InterpreterError("Invalid input", source)));
				}
			}

			sum += Integer.parseInt(ld);
			anyParsed = true;
		}

		if (!anyParsed)
			return Optional.empty();
		return Optional.of(new Ok<>(String.valueOf(sum)));
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
