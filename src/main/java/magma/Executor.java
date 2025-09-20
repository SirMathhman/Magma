package magma;

public class Executor {
	public static Result<String, String> execute(String input) {
		var opt = java.util.Optional.ofNullable(input).filter(s -> !s.isEmpty());
		if (opt.isEmpty()) {
			return new Result.Ok<>("");
		}
		var s = opt.get().trim();
		// support a simple sequence with a let-binding like: "let x = 1U8 + 2U8; x"
		int semi = s.indexOf(';');
		if (semi > 0) {
			var first = s.substring(0, semi).trim();
			var rest = s.substring(semi + 1).trim();
			// only handle the simple pattern: let <ident> = <expr>; <ident>
			if (first.startsWith("let ") && rest.length() > 0) {
				int eq = first.indexOf('=', 4);
				if (eq > 4) {
					var lhs = first.substring(4, eq).trim();
					// allow optional type annotation on the lhs, e.g. "x : U8"
					var ident = lhs;
					int colon = lhs.indexOf(':');
					if (colon > 0) {
						ident = lhs.substring(0, colon).trim();
					}
					var expr = first.substring(eq + 1).trim();
					// evaluate the expression on the right-hand side
					// evaluate expression and capture value + optional suffix for validation
					var rhsWithSuffix = evaluateSingleWithSuffix(expr);
					if (rhsWithSuffix.isPresent()) {
						var pair = rhsWithSuffix.get();
						var value = pair[0];
						var suffix = pair[1];
						// if lhs declared a type, ensure it matches the suffix (e.g., U8)
						int colonPos = lhs.indexOf(':');
						if (colonPos > 0) {
							var declared = lhs.substring(colonPos + 1).trim();
							if (!declared.isEmpty() && !declared.equals(suffix)) {
								return new Result.Err<>("Declared type does not match expression suffix");
							}
						}
						if (rest.equals(ident)) {
							return new Result.Ok<>(value);
						}
					} else {
						// propagate evaluation error
						return new Result.Err<>("Non-empty input not allowed");
					}
				}
			}
		}
		// fallback to evaluate the whole input as a single expression
		return evaluateSingle(s);
	}

	private static Result<String, String> evaluateSingle(String s) {
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
				// suffixes are the remaining parts after the leading integer
				var leftSuffix = left.substring(leftNum.length());
				var rightSuffix = right.substring(rightNum.length());
				if (!leftSuffix.equals(rightSuffix)) {
					return new Result.Err<>("Mismatched operand suffixes");
				}
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

	/**
	 * Evaluate the single expression and also return the suffix of the resulting
	 * value if present. Returns Optional of String[2] where [0]=value and [1]=suffix
	 * (empty string if none). Returns empty Optional if evaluation failed.
	 */
	private static java.util.Optional<String[]> evaluateSingleWithSuffix(String s) {
		int plusIndex = s.indexOf('+');
		if (plusIndex >= 0 && plusIndex == s.lastIndexOf('+')) {
			var left = s.substring(0, plusIndex).trim();
			var right = s.substring(plusIndex + 1).trim();
			var leftNum = leadingInteger(left);
			var rightNum = leadingInteger(right);
			if (!leftNum.isEmpty() && !rightNum.isEmpty()) {
				var leftSuffix = left.substring(leftNum.length());
				var rightSuffix = right.substring(rightNum.length());
				if (!leftSuffix.equals(rightSuffix)) {
					return java.util.Optional.empty();
				}
				try {
					var a = Integer.parseInt(leftNum);
					var b = Integer.parseInt(rightNum);
					return java.util.Optional.of(new String[]{String.valueOf(a + b), leftSuffix});
				} catch (NumberFormatException ex) {
					return java.util.Optional.empty();
				}
			}
		}
		// leading digits case
		var i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			var num = s.substring(0, i);
			var suffix = s.substring(i);
			return java.util.Optional.of(new String[]{num, suffix});
		}
		return java.util.Optional.empty();
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
