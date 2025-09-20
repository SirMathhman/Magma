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
			// If there is no trailing reference after the semicolon, return empty Ok
			if (rest.isEmpty() && first.startsWith("let ")) {
				return new Result.Ok<>("");
			}
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
		var resOpt = evaluateArithmeticOrLeading(s);
		if (resOpt.isPresent())
			return resOpt.get();
		return new Result.Err<>("Non-empty input not allowed");
	}

	/**
	 * Evaluate the single expression and also return the suffix of the resulting
	 * value if present. Returns Optional of String[2] where [0]=value and
	 * [1]=suffix
	 * (empty string if none). Returns empty Optional if evaluation failed.
	 */
	private static java.util.Optional<String[]> evaluateSingleWithSuffix(String s) {
		// Try arithmetic or leading-digit parsing and also provide suffix
		var arithOpt = evaluateArithmeticWithSuffix(s);
		if (arithOpt.isPresent())
			return arithOpt;
		// leading digits case
		var leading = extractLeadingDigits(s);
		if (leading.isPresent()) {
			return java.util.Optional.of(new String[] { leading.get()[0], leading.get()[1] });
		}
		return java.util.Optional.empty();
	}

	// Helper that consolidates arithmetic and leading-digit handling returning
	// Optional<Result> empty when not applicable. This removes duplication detected
	// by CPD.
	private static java.util.Optional<Result<String, String>> evaluateArithmeticOrLeading(String s) {
		// arithmetic case handled by shared parser
		var opOpt = parsePlusOperands(s);
		if (opOpt.isPresent()) {
			var op = opOpt.get();
			if (!op.leftSuffix.equals(op.rightSuffix)) {
				return java.util.Optional.of(new Result.Err<>("Mismatched operand suffixes"));
			}
			return java.util.Optional.of(new Result.Ok<>(op.sum));
		}
		// leading digits
		var leading = extractLeadingDigits(s);
		if (leading.isPresent()) {
			return java.util.Optional.of(new Result.Ok<>(leading.get()[0]));
		}
		return java.util.Optional.empty();
	}

	// Helper that evaluates arithmetic and returns Optional of [value,suffix]
	// empty when not applicable
	private static java.util.Optional<String[]> evaluateArithmeticWithSuffix(String s) {
		var opOpt = parsePlusOperands(s);
		if (opOpt.isEmpty())
			return java.util.Optional.empty();
		var op = opOpt.get();
		if (!op.leftSuffix.equals(op.rightSuffix))
			return java.util.Optional.empty();
		return java.util.Optional.of(new String[] { op.sum, op.leftSuffix });
	}

	// Parse a plus expression like "1U8 + 2U8" and return sum and suffixs when
	// parsable
	private static java.util.Optional<PlusOperands> parsePlusOperands(String s) {
		int plusIndex = s.indexOf('+');
		if (plusIndex >= 0 && plusIndex == s.lastIndexOf('+')) {
			var left = s.substring(0, plusIndex).trim();
			var right = s.substring(plusIndex + 1).trim();
			var leftNum = leadingInteger(left);
			var rightNum = leadingInteger(right);
			if (!leftNum.isEmpty() && !rightNum.isEmpty()) {
				var leftSuffix = left.substring(leftNum.length());
				var rightSuffix = right.substring(rightNum.length());
				try {
					var a = Integer.parseInt(leftNum);
					var b = Integer.parseInt(rightNum);
					return java.util.Optional.of(new PlusOperands(String.valueOf(a + b), leftSuffix, rightSuffix));
				} catch (NumberFormatException ex) {
					return java.util.Optional.empty();
				}
			}
		}
		return java.util.Optional.empty();
	}

	private static final class PlusOperands {
		final String sum;
		final String leftSuffix;
		final String rightSuffix;

		PlusOperands(String sum, String leftSuffix, String rightSuffix) {
			this.sum = sum;
			this.leftSuffix = leftSuffix;
			this.rightSuffix = rightSuffix;
		}
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

	private static java.util.Optional<String[]> extractLeadingDigits(String s) {
		if (java.util.Objects.isNull(s) || s.isEmpty())
			return java.util.Optional.empty();
		int i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			var num = s.substring(0, i);
			var suffix = s.substring(i);
			return java.util.Optional.of(new String[] { num, suffix });
		}
		return java.util.Optional.empty();
	}
}
