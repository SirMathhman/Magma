package magma.interpret;

import magma.Err;
import magma.Ok;
import magma.Result;
import java.util.Optional;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		// try simple addition like "2 + 3" (with optional spaces)
		Optional<Result<String, InterpretError>> addRes = tryParseBinary(input, '+');
		if (addRes.isPresent())
			return addRes.get();
		Optional<Result<String, InterpretError>> subRes = tryParseBinary(input, '-');
		if (subRes.isPresent())
			return subRes.get();
		Optional<Result<String, InterpretError>> mulRes = tryParseBinary(input, '*');
		if (mulRes.isPresent())
			return mulRes.get();

		// integer literal (decimal)
		// accept a leading decimal integer even if followed by other characters,
		// e.g. "5I32" should be interpreted as the integer literal "5".
		int i = 0;
		while (i < input.length() && Character.isDigit(input.charAt(i)))
			i++;
		if (i > 0)
			return new Ok<>(input.substring(0, i));
		return new Err<>(new InterpretError("Undefined identifier: " + input));
	}

	private Optional<Result<String, InterpretError>> tryParseBinary(String input, char op) {
		String trimmed = input.trim();
		int idx = trimmed.indexOf(op);
		if (idx < 0)
			return Optional.empty();
		String left = trimmed.substring(0, idx).trim();
		String right = trimmed.substring(idx + 1).trim();
		if (left.isEmpty() || right.isEmpty())
			return Optional.empty();

		int li = leadingDigits(left);
		if (li == 0)
			return Optional.empty();
		int ri = leadingDigits(right);
		if (ri == 0)
			return Optional.empty();

		try {
			long a = Long.parseLong(left.substring(0, li));
			long b = Long.parseLong(right.substring(0, ri));
			String leftSuffix = left.substring(li).trim();
			String rightSuffix = right.substring(ri).trim();
			if (hasSuffix(leftSuffix) && hasSuffix(rightSuffix) && isMixedSignedness(leftSuffix, rightSuffix))
				return Optional.of(new Err<>(new InterpretError("Mixed signedness in operation: " + input)));
			long res;
			switch (op) {
				case '+':
					res = a + b;
					break;
				case '-':
					res = a - b;
					break;
				case '*':
					res = a * b;
					break;
				default:
					return Optional.empty();
			}
			return Optional.of(new Ok<>(Long.toString(res)));
		} catch (NumberFormatException e) {
			// treat overflow as not-a-match so other rules can apply
			return Optional.empty();
		}
	}

	private static int leadingDigits(String s) {
		int i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i)))
			i++;
		return i;
	}

	private static boolean hasSuffix(String s) {
		return !s.isEmpty();
	}

	private static boolean isMixedSignedness(String a, String b) {
		char la = Character.toUpperCase(a.charAt(0));
		char lb = Character.toUpperCase(b.charAt(0));
		return (la == 'U' && lb == 'I') || (la == 'I' && lb == 'U');
	}
}
