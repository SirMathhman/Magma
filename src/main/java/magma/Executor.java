package magma;

public class Executor {
	public static Result<String, String> execute(String input) {
		var opt = java.util.Optional.ofNullable(input).filter(s -> !s.isEmpty());
		if (opt.isEmpty()) {
			return new Result.Ok<>("");
		}
		var s = opt.get();
		// Support simple addition expressions like "1 + 2" (optional spaces) without
		// regex
		int plusIndex = s.indexOf('+');
		if (plusIndex >= 0 && plusIndex == s.lastIndexOf('+')) {
			var left = s.substring(0, plusIndex).trim();
			var right = s.substring(plusIndex + 1).trim();
			// allow operands like "1U8" by extracting leading integer portion
			var leftNum = leadingInteger(left);
			var rightNum = leadingInteger(right);
			if (!leftNum.isEmpty() && !rightNum.isEmpty()) {
				try {
					var a = Integer.parseInt(leftNum);
					var b = Integer.parseInt(rightNum);
					return new Result.Ok<>(String.valueOf(a + b));
				} catch (NumberFormatException ex) {
					// fall through to other handling if numbers are out of int range
				}
			}
		}
		// If the input starts with one or more digits, return those leading digits as
		// Ok
		var i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			return new Result.Ok<>(s.substring(0, i));
		}
		return new Result.Err<>("Non-empty input not allowed");
	}

	private static String leadingInteger(String s) {
		if (java.util.Objects.isNull(s) || s.isEmpty()) {
			return "";
		}
		int idx = 0;
		char c = s.charAt(0);
		if (c == '+' || c == '-') {
			idx = 1;
		}
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		return s.substring(0, idx);
	}
}
