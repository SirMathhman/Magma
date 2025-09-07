package magma.interpret;

import magma.Err;
import magma.Ok;
import magma.Result;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		// try simple addition like "2 + 3" (with optional spaces)
		String[] addOut = new String[1];
		if (tryParseAddition(input, addOut))
			return new Ok<>(addOut[0]);

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

	private boolean tryParseAddition(String input, String[] out) {
		String trimmed = input.trim();
		int plusIndex = -1;
		for (int idx = 0; idx < trimmed.length(); idx++) {
			if (trimmed.charAt(idx) == '+') {
				plusIndex = idx;
				break;
			}
		}
		if (plusIndex < 0)
			return false;
		String left = trimmed.substring(0, plusIndex).trim();
		String right = trimmed.substring(plusIndex + 1).trim();
		if (left.isEmpty() || right.isEmpty())
			return false;
		// accept a leading decimal integer on each side even if followed by other
		// characters
		int li = 0;
		while (li < left.length() && Character.isDigit(left.charAt(li)))
			li++;
		if (li == 0)
			return false;
		int ri = 0;
		while (ri < right.length() && Character.isDigit(right.charAt(ri)))
			ri++;
		if (ri == 0)
			return false;

		try {
			long a = Long.parseLong(left.substring(0, li));
			long b = Long.parseLong(right.substring(0, ri));
			out[0] = Long.toString(a + b);
			return true;
		} catch (NumberFormatException e) {
			// treat overflow as not-a-match so other rules can apply
			return false;
		}
	}
}
