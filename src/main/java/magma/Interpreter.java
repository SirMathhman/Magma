package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
	public static Result<String, InterpreterError> interpret(String source) {
		if (source.isEmpty())
			return new Ok<>("");

		try {
			Integer.parseInt(source);
			return new Ok<>(source);
		} catch (NumberFormatException e) {
			// If the input contains a leading integer followed by non-digits,
			// return the leading integer (e.g. "1U8" -> "1").
			Pattern p = Pattern.compile("^(\\d+)");
			Matcher m = p.matcher(source);
			if (m.find()) {
				return new Ok<>(m.group(1));
			}
			return new Err<>(new InterpreterError("Invalid input", source));
		}
	}
}
