package magma;

/**
 * Interpreter provides functionality to interpret source code with a given
 * input.
 */
public class Interpreter {

	/**
	 * Interpret the given source with the provided input and produce a result
	 * string.
	 *
	 * Currently this method is a stub and returns an error Result containing the
	 * offending source.
	 *
	 * @param source the source code to interpret
	 * @param input  the runtime input for the program
	 * @return the result of interpretation wrapped in a Result (Ok or Err)
	 */
	public Result<String, String> interpret(String source, String input) {
		// Minimal implementation: if the source is a simple integer literal,
		// return it as the program output. Otherwise, keep the previous stub
		// behavior and return Err with the source.
		if (source != null) {
			String s = source.trim();
			// Manual parse to avoid using java.util.regex which is banned by project
			// Checkstyle rules.
			// Accept: optional '+' or '-' sign, followed by one or more digits, optionally
			// followed by an alphanumeric suffix.
			int i = 0;
			int len = s.length();
			if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
				i++;
			}
			int digitsStart = i;
			while (i < len && Character.isDigit(s.charAt(i))) {
				i++;
			}
			if (i > digitsStart) {
				// There is at least one digit. Extract the integer portion.
				String integerPart = s.substring(0, i);
				// Remaining chars (if any) are considered a suffix; validate they are
				// alphanumeric if present.
				boolean suffixOk = true;
				for (int j = i; j < len; j++) {
					char c = s.charAt(j);
					if (!Character.isLetterOrDigit(c)) {
						suffixOk = false;
						break;
					}
				}
				if (suffixOk) {
					return new Result.Ok<>(integerPart);
				}
			}
		}
		return new Result.Err<>(source);
	}
}
