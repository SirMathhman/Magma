package magma;

import java.util.Objects;

/**
 * Interpreter for a tiny language used by the project tests.
 */
public class Interpreter {
	private java.util.Optional<Result<String, InterpretError>> err(String msg, String source) {
		return java.util.Optional.of(new Result.Err<>(new InterpretError(msg, source)));
	}

	// Small holder for runtime environments and program source so we avoid
	// passing multiple maps/strings as separate parameters (keeps parameter
	// counts below the Checkstyle limit).
	private static final class Env {
		final java.util.Map<String, String> valEnv;
		final java.util.Map<String, String> typeEnv;
		final java.util.Map<String, Boolean> mutEnv;
		final String source;

		Env(java.util.Map<String, String> valEnv, java.util.Map<String, String> typeEnv, String source) {
			this.valEnv = valEnv;
			this.typeEnv = typeEnv;
			this.mutEnv = new java.util.HashMap<>();
			this.source = source;
		}
	}

	// Helper to represent a parsed type kind and width together so checks can
	// take a single object rather than separate primitive params.
	private static final class TypeSpec {
		final char kind;
		final int width;

		TypeSpec(char kind, int width) {
			this.kind = kind;
			this.width = width;
		}
	}

	/**
	 * Interpret the given source with the provided input and produce a result
	 * wrapped in a Result (Ok or Err).
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

		// For single-expression programs, evaluate the expression with empty
		// environments so that expression handling (including literals,
		// boolean literals, addition, and typed literals) is centralized in
		// evaluateExpression(...).
		java.util.Map<String, String> valEnv = new java.util.HashMap<>();
		java.util.Map<String, String> typeEnv = new java.util.HashMap<>();
		Env env = new Env(valEnv, typeEnv, source);
		return evaluateExpression(s, env.valEnv, env.typeEnv);
	}

	// Evaluate a semicolon-separated program supporting `let` declarations.
	private Result<String, InterpretError> evaluateSequence(String source) {
		// Use split with negative limit to preserve trailing empty segments
		// so that a trailing semicolon produces an empty final part.
		String[] parts = source.split(";", -1);
		java.util.Map<String, String> valEnv = new java.util.HashMap<>();
		java.util.Map<String, String> typeEnv = new java.util.HashMap<>();
		Env env = new Env(valEnv, typeEnv, source);
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].trim();
			if (part.isEmpty())
				continue;
			if (i < parts.length - 1) {
				// Statement position: expect a let-binding or assignment
				java.util.Optional<Result<String, InterpretError>> stmtRes = handleStatement(part, env);
				if (stmtRes.isPresent())
					return stmtRes.get();
			} else {
				// Final expression: evaluate and return
				return evaluateExpression(part, env.valEnv, env.typeEnv);
			}
		}
		// If we reached the end without a final expression (e.g., trailing semicolon or
		// only statements),
		// return empty string as the program result.
		return new Result.Ok<>("");
	}

	private java.util.Optional<Result<String, InterpretError>> handleStatement(String stmt, Env env) {
		String s = stmt.trim();
		// Support assignment statements: <ident> = <expr>
		int eqIdx = s.indexOf('=');
		if (eqIdx > 0 && !s.startsWith("let ")) {
			return handleAssignment(s, eqIdx, env);
		}
		java.util.Optional<LetDeclaration> parsed = parseLetDeclaration(s);
		if (!parsed.isPresent())
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid let declaration", env.source)));
		LetDeclaration d = parsed.get();
		return handleLetDeclaration(d, env);
	}

	// Helper to process let declarations (extracted to reduce complexity)
	private java.util.Optional<Result<String, InterpretError>> handleLetDeclaration(LetDeclaration d, Env env) {
		// If there's no RHS (declaration only), record the annotation and mark as
		// mutable
		if (d.rhs.isEmpty()) {
			if (!d.annotatedSuffix.isEmpty()) {
				java.util.Optional<Result<String, InterpretError>> wErr = checkAnnotatedSuffix(d.annotatedSuffix, "0", env);
				if (wErr.isPresent())
					return wErr;
				env.typeEnv.put(d.name, d.annotatedSuffix);
			}
			// Declarations without initializer are implicitly mutable to allow later
			// assignment
			env.mutEnv.put(d.name, Boolean.TRUE);
			// Do not place a value in valEnv yet; assignment will set it.
			return java.util.Optional.empty();
		}
		Result<String, InterpretError> rhsRes = getRhsValue(d.rhs, env.valEnv, env.typeEnv);
		if (rhsRes instanceof Result.Err)
			return java.util.Optional.of((Result<String, InterpretError>) rhsRes);
		String value = ((Result.Ok<String, InterpretError>) rhsRes).value();
		java.util.Optional<Result<String, InterpretError>> annRes = recordAnn(d, value, env);
		if (annRes.isPresent())
			return annRes;
		env.valEnv.put(d.name, value);
		// Record mutability
		env.mutEnv.put(d.name, d.mutable ? Boolean.TRUE : Boolean.FALSE);
		return java.util.Optional.empty();
	}

	private Result<String, InterpretError> getRhsValue(String rhs,
			java.util.Map<String, String> valEnv,
			java.util.Map<String, String> typeEnv) {
		return evaluateExpression(rhs, valEnv, typeEnv);
	}

	// Validate annotated suffix for a let declaration and record the annotation in
	// typeEnv
	private java.util.Optional<Result<String, InterpretError>> recordAnn(LetDeclaration d, String value, Env env) {
		if (d.annotatedSuffix.isEmpty())
			return java.util.Optional.empty();
		java.util.Optional<Result<String, InterpretError>> check = chkRhs(d.annotatedSuffix, d.rhs, env.source);
		if (check.isPresent())
			return check;
		java.util.Optional<Result<String, InterpretError>> v = checkAnnotatedSuffix(d.annotatedSuffix, value, env);
		if (v.isPresent())
			return v;
		env.typeEnv.put(d.name, d.annotatedSuffix);
		return java.util.Optional.empty();
	}

	private java.util.Optional<Result<String, InterpretError>> chkRhs(String annSuffix, String rhs, String source) {
		ParseResult rhsPr = parseSignAndDigits(rhs.trim());
		if (rhsPr.valid && !rhsPr.suffix.isEmpty()) {
			String ann = annSuffix.toUpperCase();
			String rhsSuf = rhsPr.suffix.toUpperCase();
			if (!ann.equals(rhsSuf))
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", source)));
		}
		return java.util.Optional.empty();
	}

	// Helper to handle assignment statements to reduce complexity
	private java.util.Optional<Result<String, InterpretError>> handleAssignment(String stmt, int eqIdx, Env env) {
		String lhs = stmt.substring(0, eqIdx).trim();
		String rhs = stmt.substring(eqIdx + 1).trim();
		if (!isSimpleIdentifier(lhs))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid assignment lhs", env.source)));
		// Unknown in valEnv can be acceptable if declared without initializer (present
		// in typeEnv)
		if (!env.valEnv.containsKey(lhs) && !env.typeEnv.containsKey(lhs))
			return java.util.Optional
					.of(new Result.Err<>(new InterpretError("unknown identifier in assignment", env.source)));
		Boolean isMut = env.mutEnv.getOrDefault(lhs, Boolean.FALSE);
		if (!isMut)
			return java.util.Optional
					.of(new Result.Err<>(new InterpretError("assignment to immutable variable", env.source)));

		// If RHS is a typed literal and the variable has an annotated type, ensure
		// the suffix matches the annotation (same kind and width). Reject if
		// mismatched.
		ParseResult rhsPr = parseSignAndDigits(rhs.trim());
		if (rhsPr.valid && !rhsPr.suffix.isEmpty() && env.typeEnv.containsKey(lhs)) {
			String ann = env.typeEnv.get(lhs).toUpperCase();
			String rhsSuf = rhsPr.suffix.toUpperCase();
			if (!ann.equals(rhsSuf))
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", env.source)));
		}
		Result<String, InterpretError> rhsVal = evaluateExpression(rhs, env.valEnv, env.typeEnv);
		if (rhsVal instanceof Result.Err)
			return java.util.Optional.of((Result<String, InterpretError>) rhsVal);
		String value = ((Result.Ok<String, InterpretError>) rhsVal).value();
		env.valEnv.put(lhs, value);
		return java.util.Optional.empty();
	}

	// Small helper to represent a parsed let declaration
	private static final class LetDeclaration {
		final String name;
		final String annotatedSuffix;
		boolean mutable;
		final String rhs;

		LetDeclaration(String name, String annotatedSuffix, String rhs) {
			this.name = name;
			this.annotatedSuffix = annotatedSuffix;
			this.rhs = rhs;
			this.mutable = false;
		}
	}

	// Parse a let declaration of the form: let <ident> (':' <suffix>)? '=' <expr>
	private java.util.Optional<LetDeclaration> parseLetDeclaration(String s) {
		if (!s.startsWith("let "))
			return java.util.Optional.empty();
		int len = s.length();
		int idx = skipWs(s, 4, len); // after 'let '
		boolean mutable = false;
		// support optional 'mut' keyword: 'let mut x = ...'
		if (s.startsWith("mut ", idx)) {
			mutable = true;
			idx = skipWs(s, idx + 4, len);
		}
		java.util.Optional<ParseId> pidOpt = parseId(s, idx, len);
		if (!pidOpt.isPresent())
			return java.util.Optional.empty();
		ParseId pid = pidOpt.get();
		String name = pid.name;
		idx = skipWs(s, pid.idx, len);
		String annotatedSuffix = parseAnnotatedSuffix(s, idx, len);
		// find '=' after annotation (if any). Allow declarations without initializer
		int eq = s.indexOf('=', idx);
		String rhs = "";
		if (eq >= 0) {
			rhs = s.substring(eq + 1).trim();
		}
		LetDeclaration d = new LetDeclaration(name, annotatedSuffix, rhs);
		d.mutable = mutable;
		return java.util.Optional.of(d);
	}

	// helper to skip whitespace from index start up to len
	private int skipWs(String s, int start, int len) {
		int i = start;
		while (i < len && Character.isWhitespace(s.charAt(i)))
			i++;
		return i;
	}

	// small helper to return parsed identifier and next index
	private static final class ParseId {
		final String name;
		final int idx;

		ParseId(String name, int idx) {
			this.name = name;
			this.idx = idx;
		}
	}

	private java.util.Optional<ParseId> parseId(String s, int idx, int len) {
		if (idx >= len || !Character.isJavaIdentifierStart(s.charAt(idx)))
			return java.util.Optional.empty();
		int start = idx;
		idx++;
		while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx)))
			idx++;
		return java.util.Optional.of(new ParseId(s.substring(start, idx), idx));
	}

	private String parseAnnotatedSuffix(String s, int idx, int len) {
		if (idx < len && s.charAt(idx) == ':') {
			idx++;
			int sufStart = idx;
			while (idx < len && s.charAt(idx) != '=')
				idx++;
			return s.substring(sufStart, idx).trim();
		}
		return "";
	}

	private java.util.Optional<Result<String, InterpretError>> checkAnnotatedSuffix(String annotatedSuffix, String value,
			Env env) {
		// Special-case Bool annotation which does not follow the <Letter><digits>
 		// pattern used by integer typed suffixes (e.g. U8, I32).
		if (annotatedSuffix.equalsIgnoreCase("Bool")) {
			// For Bool, the value must be a boolean literal string "true" or "false"
			if ("true".equals(value) || "false".equals(value))
				return java.util.Optional.empty();
			return err("invalid boolean value for Bool annotation", env.source);
		}
		if (!isValidSuffix(annotatedSuffix))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid type suffix", env.source)));
		char kind = Character.toUpperCase(annotatedSuffix.charAt(0));
		int[] widthHolder = new int[1];
		java.util.Optional<Result<String, InterpretError>> wErr = parseWidth(annotatedSuffix, widthHolder, env.source);
		if (wErr.isPresent())
			return wErr;
		int width = widthHolder[0];
		return checkFits(new TypeSpec(kind, width), value, env);
	}

	private java.util.Optional<Result<String, InterpretError>> parseWidth(String annotatedSuffix, int[] outWidth,
			String source) {
		try {
			outWidth[0] = Integer.parseInt(annotatedSuffix.substring(1));
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid type width", source)));
		}
		return java.util.Optional.empty();
	}

	private java.util.Optional<Result<String, InterpretError>> checkFits(TypeSpec ts, String value, Env env) {
		try {
			java.math.BigInteger val = new java.math.BigInteger(value);
			if (ts.kind == 'U') {
				if (val.signum() < 0 || !fitsUnsigned(val, ts.width))
					return err("value does not fit annotated type", env.source);
				return java.util.Optional.empty();
			} else if (ts.kind == 'I') {
				if (!fitsSigned(val, ts.width))
					return err("value does not fit annotated type", env.source);
				return java.util.Optional.empty();
			}
			return err("unknown type kind", env.source);
		} catch (NumberFormatException ex) {
			return err("invalid integer value", env.source);
		}
	}

	private Result<String, InterpretError> evaluateExpression(String expr, java.util.Map<String, String> valEnv,
			java.util.Map<String, String> typeEnv) {
		String s = expr.trim();
		// Boolean literals
		if (s.equals("true") || s.equals("false"))
			return new Result.Ok<>(s);
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
		java.util.Optional<Result<String, InterpretError>> suffixCheck = checkTypedOperands(leftPr, rightPr, s);
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
	private java.util.Optional<Result<String, InterpretError>> checkTypedOperands(ParseResult leftPr,
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
		return vTyped(typedSuffix, untypedInteger, source);
	}

	private java.util.Optional<Result<String, InterpretError>> vTyped(String typedSuffix,
			String untypedInteger,
			String source) {
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
		java.math.BigInteger untypedVal;
		try {
			untypedVal = parseBigInteger(untypedInteger);
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid integer in operand", source)));
		}
		if (kind == 'U') {
			if (untypedVal.signum() < 0 || !fitsUnsigned(untypedVal, width))
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("untyped value does not fit unsigned type", source)));
			return java.util.Optional.empty();
		}
		if (kind == 'I') {
			if (!fitsSigned(untypedVal, width))
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("untyped value does not fit signed type", source)));
			return java.util.Optional.empty();
		}
		return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid typed operand combination", source)));
	}

	private java.math.BigInteger parseBigInteger(String s) {
		return new java.math.BigInteger(s);
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
