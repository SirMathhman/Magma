package magma;

import java.util.Objects;

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
	public Result<String, InterpretError> interpret(String source, String input) {
		// Minimal implementation: if the source is a simple integer literal,
		// return it as the program output. Otherwise return Err with the source.
		if (Objects.isNull(source))
			return new Result.Err<>(new InterpretError("<missing source>", ""));

		String s = source.trim();

		// Support simple sequences with `let` bindings separated by ';'
		if (s.contains(";"))
			return evaluateSequence(s);

		// Attempt simple addition before falling back to literal parsing
		java.util.Optional<Result<String, InterpretError>> addRes = tryEvaluateAddition(s);
		if (addRes.isPresent())
			return addRes.get();
		ParseResult pr = parseSignAndDigits(s);
		if (!pr.valid)
			return new Result.Err<>(new InterpretError("invalid literal", source));

		// No suffix -> accept as-is
		if (pr.suffix.isEmpty())
			return new Result.Ok<>(pr.integerPart);

		// Delegate typed-suffix handling to a helper to keep complexity low
		return evaluateTypedSuffix(pr, source);
	}

	// Evaluate a semicolon-separated program supporting `let` declarations.
	private Result<String, InterpretError> evaluateSequence(String source) {
		String[] parts = source.split(";");
		java.util.Map<String, String> valEnv = new java.util.HashMap<>();
		java.util.Map<String, String> typeEnv = new java.util.HashMap<>();
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].trim();
			if (part.isEmpty())
				continue;
			if (i < parts.length - 1) {
				// Statement position: expect a let-binding
				java.util.Optional<Result<String, InterpretError>> stmtRes = handleStatement(part, valEnv, typeEnv, source);
				if (stmtRes.isPresent())
					return stmtRes.get();
			} else {
				// Final expression: evaluate and return
				return evaluateExpression(part, valEnv, typeEnv);
			}
		}
	return new Result.Err<>(new InterpretError("invalid program sequence", source));
	}

	private java.util.Optional<Result<String, InterpretError>> handleStatement(String stmt,
			java.util.Map<String, String> valEnv,
			java.util.Map<String, String> typeEnv, String source) {
		String s = stmt.trim();
		java.util.Optional<LetDeclaration> parsed = parseLetDeclaration(s);
		if (!parsed.isPresent())
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid let declaration", source)));
		LetDeclaration d = parsed.get();
		Result<String, InterpretError> rhsVal = evaluateExpression(d.rhs, valEnv, typeEnv);
		if (rhsVal instanceof Result.Err)
			return java.util.Optional.of((Result<String, InterpretError>) rhsVal);
		String value = ((Result.Ok<String, InterpretError>) rhsVal).value();
		if (!d.annotatedSuffix.isEmpty()) {
			java.util.Optional<Result<String, InterpretError>> v = validateAnnotatedSuffix(d.annotatedSuffix, value, source);
			if (v.isPresent())
				return v;
			typeEnv.put(d.name, d.annotatedSuffix);
		}
		valEnv.put(d.name, value);
		return java.util.Optional.empty();
	}

	// Small helper to represent a parsed let declaration
	private static final class LetDeclaration {
		final String name;
		final String annotatedSuffix;
		final String rhs;

		LetDeclaration(String name, String annotatedSuffix, String rhs) {
			this.name = name;
			this.annotatedSuffix = annotatedSuffix;
			this.rhs = rhs;
		}
	}

	// Parse a let declaration of the form: let <ident> (':' <suffix>)? '=' <expr>
	private java.util.Optional<LetDeclaration> parseLetDeclaration(String s) {
		if (!s.startsWith("let "))
			return java.util.Optional.empty();
		int idx = 4; // after 'let '
		int len = s.length();
		while (idx < len && Character.isWhitespace(s.charAt(idx)))
			idx++;
		int idStart = idx;
		if (idx >= len || !Character.isJavaIdentifierStart(s.charAt(idx)))
			return java.util.Optional.empty();
		idx++;
		while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx)))
			idx++;
		String name = s.substring(idStart, idx);
		while (idx < len && Character.isWhitespace(s.charAt(idx)))
			idx++;
		String annotatedSuffix = "";
		if (idx < len && s.charAt(idx) == ':') {
			idx++;
			int sufStart = idx;
			while (idx < len && s.charAt(idx) != '=')
				idx++;
			annotatedSuffix = s.substring(sufStart, idx).trim();
		}
		int eq = s.indexOf('=', idx);
		if (eq < 0)
			return java.util.Optional.empty();
		String rhs = s.substring(eq + 1).trim();
		return java.util.Optional.of(new LetDeclaration(name, annotatedSuffix, rhs));
	}

	private java.util.Optional<Result<String, InterpretError>> validateAnnotatedSuffix(String annotatedSuffix,
			String value,
			String source) {
		if (!isValidSuffix(annotatedSuffix))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid type suffix", source)));
		char kind = Character.toUpperCase(annotatedSuffix.charAt(0));
		int width;
		try {
			width = Integer.parseInt(annotatedSuffix.substring(1));
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid type width", source)));
		}
		try {
			java.math.BigInteger val = new java.math.BigInteger(value);
			if (kind == 'U') {
				if (val.signum() < 0 || !fitsUnsigned(val, width))
					return java.util.Optional.of(new Result.Err<>(new InterpretError("value does not fit annotated type", source)));
			} else if (kind == 'I') {
				if (!fitsSigned(val, width))
					return java.util.Optional.of(new Result.Err<>(new InterpretError("value does not fit annotated type", source)));
			} else {
				return java.util.Optional.of(new Result.Err<>(new InterpretError("unknown type kind", source)));
			}
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid integer value", source)));
		}
		return java.util.Optional.empty();
	}

	private Result<String, InterpretError> evaluateExpression(String expr, java.util.Map<String, String> valEnv,
			java.util.Map<String, String> typeEnv) {
		String s = expr.trim();
		// variable reference
		if (isSimpleIdentifier(s)) {
			if (valEnv.containsKey(s))
				return new Result.Ok<>(valEnv.get(s));
			return new Result.Err<>(new InterpretError("unknown identifier", expr));
		}
		// addition
		java.util.Optional<Result<String, InterpretError>> addRes = tryEvaluateAddition(s);
		if (addRes.isPresent())
			return addRes.get();
		// literal/typed-literal
		ParseResult pr = parseSignAndDigits(s);
		if (!pr.valid)
			return new Result.Err<>(new InterpretError("invalid literal", expr));
		if (pr.suffix.isEmpty())
			return new Result.Ok<>(pr.integerPart);
		return evaluateTypedSuffix(pr, expr);
	}

	private boolean isSimpleIdentifier(String s) {
		if (Objects.isNull(s) || s.isEmpty())
			return false;
		if (!Character.isJavaIdentifierStart(s.charAt(0)))
			return false;
		for (int i = 1; i < s.length(); i++) {
			if (!Character.isJavaIdentifierPart(s.charAt(i)))
				return false;
		}
		return true;
	}

	// Small helper struct to hold parse results
	private static final class ParseResult {
		final boolean valid;
		final String integerPart;
		final String suffix;

		private static final ParseResult INVALID = new ParseResult(false, "", "");

		ParseResult(boolean valid, String integerPart, String suffix) {
			this.valid = valid;
			this.integerPart = integerPart;
			this.suffix = suffix;
		}

		static ParseResult invalid() {
			return INVALID;
		}
	}

	// Parse optional sign and digits and return integer part and suffix
	private ParseResult parseSignAndDigits(String s) {
		int i = 0;
		int len = s.length();
		if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-'))
			i++;
		int digitsStart = i;
		while (i < len && Character.isDigit(s.charAt(i)))
			i++;
		if (i <= digitsStart)
			return ParseResult.invalid();
		String integerPart = s.substring(0, i);
		String suffix = s.substring(i).trim();
		return new ParseResult(true, integerPart, suffix);
	}

	private boolean isValidSuffix(String suffix) {
		if (suffix.length() < 2)
			return false;
		if (!Character.isLetter(suffix.charAt(0)))
			return false;
		String widthStr = suffix.substring(1);
		for (int j = 0; j < widthStr.length(); j++) {
			if (!Character.isDigit(widthStr.charAt(j)))
				return false;
		}
		return true;
	}

	private boolean fitsUnsigned(java.math.BigInteger val, int width) {
		java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(width).subtract(java.math.BigInteger.ONE);
		return val.compareTo(max) <= 0;
	}

	private boolean fitsSigned(java.math.BigInteger val, int width) {
		java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(width - 1).negate();
		java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(width - 1).subtract(java.math.BigInteger.ONE);
		return val.compareTo(min) >= 0 && val.compareTo(max) <= 0;
	}

	/**
	 * Try to evaluate a simple addition expression of the form "<int> + <int>".
	 * Returns an Ok Result on success or an empty Optional if not applicable.
	 */
	private java.util.Optional<Result<String, InterpretError>> tryEvaluateAddition(String s) {
		int plusIdx = s.indexOf('+');
		if (plusIdx <= 0)
			return java.util.Optional.empty();
		String leftRaw = s.substring(0, plusIdx).trim();
		String rightRaw = s.substring(plusIdx + 1).trim();

		// Parse each side for optional sign/digits and optional suffix
		ParseResult leftPr = parseSignAndDigits(leftRaw);
		ParseResult rightPr = parseSignAndDigits(rightRaw);
		if (!leftPr.valid || !rightPr.valid)
			return java.util.Optional.empty();

		// If either side has a suffix, we allow three cases:
		// 1) both have suffixes and they match exactly (same kind and width)
		// 2) one has a suffix and the other doesn't: the untyped side is accepted
		// only if its value fits into the typed width/kind of the typed side
		// 3) mixed kinds/widths (e.g., U vs I or different widths) are invalid
		// Validate typed/untyped suffixes; if invalid, return Err wrapped in Optional
		java.util.Optional<Result<String, InterpretError>> suffixCheck = validateTypedOperands(leftPr, rightPr, s);
		if (suffixCheck.isPresent())
			return suffixCheck;

		try {
			java.math.BigInteger a = new java.math.BigInteger(leftPr.integerPart);
			java.math.BigInteger b = new java.math.BigInteger(rightPr.integerPart);
			return java.util.Optional.of(new Result.Ok<>(a.add(b).toString()));
		} catch (NumberFormatException ex) {
			return java.util.Optional.empty();
		}
	}

	// Helper to validate typed/untyped operands for addition.
	private java.util.Optional<Result<String, InterpretError>> validateTypedOperands(ParseResult leftPr,
			ParseResult rightPr,
			String source) {
		boolean leftHas = !leftPr.suffix.isEmpty();
		boolean rightHas = !rightPr.suffix.isEmpty();
		if (!leftHas && !rightHas)
			return java.util.Optional.empty();
		if (leftHas && rightHas)
			return validateBothTyped(leftPr, rightPr, source);
		return validateOneTyped(leftPr, rightPr, source);
	}

	private java.util.Optional<Result<String, InterpretError>> validateBothTyped(ParseResult leftPr, ParseResult rightPr,
			String source) {
		if (!isValidSuffix(leftPr.suffix) || !isValidSuffix(rightPr.suffix))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid typed suffix on operand", source)));
		String l = leftPr.suffix.toUpperCase();
		String r = rightPr.suffix.toUpperCase();
		if (!l.equals(r))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("mismatched typed operand suffixes", source)));
		return java.util.Optional.empty();
	}

	private java.util.Optional<Result<String, InterpretError>> validateOneTyped(ParseResult leftPr, ParseResult rightPr,
			String source) {
		boolean leftHas = !leftPr.suffix.isEmpty();
		String typedSuffix = leftHas ? leftPr.suffix : rightPr.suffix;
		String untypedInteger = leftHas ? rightPr.integerPart : leftPr.integerPart;
		if (!isValidSuffix(typedSuffix))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid typed suffix", source)));
		char kind = Character.toUpperCase(typedSuffix.charAt(0));
		int width;
		try {
			width = Integer.parseInt(typedSuffix.substring(1));
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid type width", source)));
		}
		if (!(width == 8 || width == 16 || width == 32 || width == 64))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("unsupported type width", source)));
		try {
			java.math.BigInteger untypedVal = new java.math.BigInteger(untypedInteger);
			if (kind == 'U') {
				if (untypedVal.signum() < 0 || !fitsUnsigned(untypedVal, width))
					return java.util.Optional.of(new Result.Err<>(new InterpretError("untyped value does not fit unsigned type", source)));
				return java.util.Optional.empty();
			}
			if (kind == 'I') {
				if (!fitsSigned(untypedVal, width))
					return java.util.Optional.of(new Result.Err<>(new InterpretError("untyped value does not fit signed type", source)));
				return java.util.Optional.empty();
			}
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid integer in operand", source)));
		}
		return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid typed operand combination", source)));
	}

	private Result<String, InterpretError> evaluateTypedSuffix(ParseResult pr, String source) {
		if (!isValidSuffix(pr.suffix))
			return new Result.Err<>(new InterpretError("invalid typed literal suffix", source));

		char kind = Character.toUpperCase(pr.suffix.charAt(0));
		String widthStr = pr.suffix.substring(1);
		try {
			int width = Integer.parseInt(widthStr);
			if (!(width == 8 || width == 16 || width == 32 || width == 64))
				return new Result.Err<>(new InterpretError("unsupported type width", source));

			java.math.BigInteger val = new java.math.BigInteger(pr.integerPart);
			if (kind == 'U') {
				if (val.signum() < 0)
					return new Result.Err<>(new InterpretError("negative value for unsigned literal", source));
				if (fitsUnsigned(val, width))
					return new Result.Ok<>(pr.integerPart);
				return new Result.Err<>(new InterpretError("value does not fit typed literal", source));
			} else if (kind == 'I') {
				if (fitsSigned(val, width))
					return new Result.Ok<>(pr.integerPart);
				return new Result.Err<>(new InterpretError("value does not fit typed literal", source));
			}
		} catch (NumberFormatException | ArithmeticException e) {
			return new Result.Err<>(new InterpretError("invalid numeric literal", source));
		}

		return new Result.Err<>(new InterpretError("invalid typed literal", source));
	}
}
