package magma;

public class Executor {
	public static Result<String, String> execute(String input) {
		var opt = java.util.Optional.ofNullable(input).filter(s -> !s.isEmpty());
		if (opt.isEmpty()) {
			return new Result.Ok<>("");
		}
		var s = opt.get().trim();
		return runSequence(s);
	}

	private static Result<String, String> runSequence(String s) {
		var parts = s.split(";");
		var nonEmpty = new java.util.ArrayList<String>();
		for (var p : parts) {
			var t = p.trim();
			if (!t.isEmpty())
				nonEmpty.add(t);
		}
		var env = new java.util.HashMap<String, String[]>();
		if (nonEmpty.isEmpty())
			return new Result.Ok<>("");
		var last = nonEmpty.get(nonEmpty.size() - 1);
		if (last.startsWith("let ")) {
			var buildErr = processLetStatements(nonEmpty, env);
			if (buildErr.isPresent())
				return buildErr.get();
			return new Result.Ok<>("");
		}
		var toProcess = nonEmpty.subList(0, nonEmpty.size() - 1);
		var buildErr = processLetStatements(toProcess, env);
		if (buildErr.isPresent())
			return buildErr.get();
		return evaluateFinal(nonEmpty.get(nonEmpty.size() - 1), env);
	}

	private static java.util.Optional<Result<String, String>> processLetStatements(java.util.List<String> stmts,
			java.util.Map<String, String[]> env) {
		for (var stmt : stmts) {
			var err = processSingleLet(stmt, env);
			if (err.isPresent())
				return err;
		}
		return java.util.Optional.empty();
	}

	private static java.util.Optional<Result<String, String>> processSingleLet(String stmt,
			java.util.Map<String, String[]> env) {
		if (!stmt.startsWith("let "))
			return java.util.Optional.of(new Result.Err<>("Non-empty input not allowed"));
		int eq = stmt.indexOf('=', 4);
		if (eq <= 4)
			return java.util.Optional.of(new Result.Err<>("Non-empty input not allowed"));
		var lhs = stmt.substring(4, eq).trim();
		var ident = extractIdentFromLhs(lhs);
		if (env.containsKey(ident))
			return java.util.Optional.of(new Result.Err<>("Duplicate binding"));
		int colonPos = lhs.indexOf(':');
		var declared = "";
		if (colonPos > 0)
			declared = lhs.substring(colonPos + 1).trim();
		var rhs = stmt.substring(eq + 1).trim();
		if (rhs.isEmpty())
			return java.util.Optional.of(new Result.Err<>("Non-empty input not allowed"));

		var pairOpt = parseRhsPair(rhs, env);
		if (pairOpt.isEmpty())
			return java.util.Optional.of(new Result.Err<>("Non-empty input not allowed"));
		var pair = pairOpt.get();
		var suffix = pair[1];
		if (!isDeclaredCompatible(declared, suffix))
			return java.util.Optional.of(new Result.Err<>("Declared type does not match expression suffix"));
		env.put(ident, pair);
		return java.util.Optional.empty();
	}

	private static java.util.Optional<String[]> parseRhsPair(String rhs, java.util.Map<String, String[]> env) {
		if (rhs.startsWith("&")) {
			var pointee = rhs.substring(1).trim();
			if (!env.containsKey(pointee))
				return java.util.Optional.empty();
			var pointeeSuffix = env.get(pointee)[1];
			return java.util.Optional.of(new String[] { pointee, "*" + pointeeSuffix });
		}
		var rhsOpt = evaluateSingleWithSuffix(rhs);
		if (rhsOpt.isPresent())
			return rhsOpt;
		if (!env.containsKey(rhs))
			return java.util.Optional.empty();
		return java.util.Optional.of(env.get(rhs));
	}

	private static boolean isDeclaredCompatible(String declared, String suffix) {
		if (java.util.Objects.isNull(declared) || declared.isEmpty())
			return true;
		if (declared.startsWith("*")) {
			var baseDeclared = declared.substring(1).trim();
			var pointeeSuffix = suffix.startsWith("*") ? suffix.substring(1) : suffix;
			return checkBasePointeeCompatibility(baseDeclared, pointeeSuffix);
		}
		return declared.equals(suffix);
	}

	private static boolean checkBasePointeeCompatibility(String baseDeclared, String pointeeSuffix) {
		if (baseDeclared.isEmpty() || pointeeSuffix.isEmpty())
			return true;
		return baseDeclared.equals(pointeeSuffix);
	}

	private static Result<String, String> evaluateFinal(String stmt, java.util.Map<String, String[]> env) {
		// if it's an identifier, return its value from env
		if (env.containsKey(stmt)) {
			var pair = env.get(stmt);
			return new Result.Ok<>(pair[0]);
		}
		// pointer dereference expression like *y
		if (stmt.startsWith("*")) {
			var ref = stmt.substring(1).trim();
			if (!env.containsKey(ref))
				return new Result.Err<>("Non-empty input not allowed");
			var target = env.get(ref)[0];
			if (env.containsKey(target)) {
				var pair = env.get(target);
				return new Result.Ok<>(pair[0]);
			}
			return new Result.Err<>("Non-empty input not allowed");
		}
		// otherwise evaluate as single expression
		return evaluateSingle(stmt);
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

	private static String extractIdentFromLhs(String lhs) {
		var ident = lhs;
		int colon = lhs.indexOf(':');
		if (colon > 0) {
			ident = lhs.substring(0, colon).trim();
		}
		return ident;
	}
}