package magma;

import java.util.Objects;

/**
 * Interpreter for a tiny language used by the project tests.
 */
public class Interpreter {
	private java.util.Optional<Result<String, InterpretError>> err(String msg, String source) {
		return java.util.Optional.of(new Result.Err<>(new InterpretError(msg, source)));
	}

	// Helper to extract the return expression for a function body.
	// It supports a block form '{ ... }' or a compact form '=> return <expr>;' (no
	// braces).
	private static final class FnBodyParse {
		final java.util.Optional<Result<String, InterpretError>> error;
		final String retExpr;

		FnBodyParse(java.util.Optional<Result<String, InterpretError>> error, String retExpr) {
			this.error = error;
			this.retExpr = retExpr;
		}
	}

	private FnBodyParse extractFnReturnExpr(String s, int arrowIdx, Env env) {
		// look for a block first
		int bodyOpen = s.indexOf('{', arrowIdx + 2);
		if (bodyOpen >= 0) {
			int j = findMatchingBrace(s, bodyOpen);
			if (j < 0)
				return new FnBodyParse(java.util.Optional.of(new Result.Err<>(
						new InterpretError("unterminated fn body", env.source))), "");
			String body = s.substring(bodyOpen + 1, j).trim();
			int retIdx = body.indexOf("return");
			if (retIdx < 0)
				return new FnBodyParse(java.util.Optional.of(new Result.Err<>(
						new InterpretError("missing return in fn body", env.source))), "");
			int semi = body.indexOf(';', retIdx);
			if (semi < 0)
				return new FnBodyParse(java.util.Optional.of(new Result.Err<>(
						new InterpretError("missing ';' after return", env.source))), "");
			String expr = body.substring(retIdx + 6, semi).trim();
			return new FnBodyParse(java.util.Optional.empty(), expr);
		}
		// compact form: accept either 'return <expr>;' OR a direct expression after the
		// arrow
		int searchStart = arrowIdx + 2;
		int retIdx = s.indexOf("return", searchStart);
		int exprStart;
		if (retIdx >= 0) {
			exprStart = retIdx + 6; // after 'return'
		} else {
			exprStart = skipWs(s, searchStart, s.length());
			if (exprStart >= s.length())
				return new FnBodyParse(
						java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn body", env.source))), "");
		}
		int semi = s.indexOf(';', exprStart);
		int end = semi >= 0 ? semi : s.length();
		String expr = s.substring(exprStart, end).trim();
		if (expr.isEmpty())
			return new FnBodyParse(java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn body", env.source))),
					"");
		return new FnBodyParse(java.util.Optional.empty(), expr);
	}

	// Find the index of the matching closing '}' for the opening brace at openIdx.
	// Returns -1 if no matching brace is found.
	private int findMatchingBrace(String s, int openIdx) {
		int len = s.length();
		int depth = 0;
		for (int i = openIdx; i < len; i++) {
			char c = s.charAt(i);
			if (c == '{')
				depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
	}

	// Try to evaluate a function call expression like 'name()' or 'name(arg)'.
	// Returns Optional.empty() if the expression is not a function call, otherwise
	// returns the Result (Ok or Err) wrapped in Optional.
	private java.util.Optional<Result<String, InterpretError>> tryEvalFunctionCall(String s, Env env) {
		int parenIdx = s.indexOf('(');
		if (parenIdx <= 0 || !s.endsWith(")"))
			return java.util.Optional.empty();
		String name = s.substring(0, parenIdx).trim();
		String inside = s.substring(parenIdx + 1, s.length() - 1).trim();
		if (!isSimpleIdentifier(name))
			return java.util.Optional.empty();
		if (!env.fnEnv.containsKey(name))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("unknown identifier", s)));
		FunctionDecl fd = env.fnEnv.get(name);
		java.util.List<String> paramNames = env.fnParamNames.getOrDefault(name, java.util.Collections.emptyList());
		java.util.List<String> paramTypes = env.fnParamTypes.getOrDefault(name, java.util.Collections.emptyList());
		// parse call arguments (comma-separated)
		java.util.List<String> args = new java.util.ArrayList<>();
		if (!inside.isEmpty()) {
			// split on commas (simple splitter; no nested expressions supported yet)
			for (String a : inside.split(","))
				args.add(a.trim());
		}
		if (args.size() != paramNames.size()) {
			return java.util.Optional.of(new Result.Err<>(
					new InterpretError("argument count mismatch in call to " + name, s)));
		}
		// evaluate each argument in caller env
		java.util.List<String> evaluatedArgs = new java.util.ArrayList<>();
		for (String a : args) {
			Result<String, InterpretError> r = evaluateExpression(a, env);
			if (r instanceof Result.Err)
				return java.util.Optional.of((Result<String, InterpretError>) r);
			evaluatedArgs.add(((Result.Ok<String, InterpretError>) r).value());
		}
		// create a temporary env for function body evaluation: copy maps but do not
		// mutate caller env; bind parameters
		java.util.Map<String, String> newVals = new java.util.HashMap<>(env.valEnv);
		java.util.Map<String, String> newTypes = new java.util.HashMap<>(env.typeEnv);
		Env fnEnv = new Env(newVals, newTypes, env.source);
		fnEnv.fnEnv.putAll(env.fnEnv);
		fnEnv.mutEnv.putAll(env.mutEnv);
		fnEnv.fnParamNames.putAll(env.fnParamNames);
		fnEnv.fnParamTypes.putAll(env.fnParamTypes);
		// bind parameter values and types
		for (int i = 0; i < paramNames.size(); i++) {
			String p = paramNames.get(i);
			String val = evaluatedArgs.get(i);
			// Validate argument value against parameter annotated type (if any)
			String ptype = i < paramTypes.size() ? paramTypes.get(i) : "";
			if (!ptype.isEmpty()) {
				java.util.Optional<Result<String, InterpretError>> annErr = checkAnnotatedSuffix(ptype, val, env);
				if (annErr.isPresent())
					return java.util.Optional.of(annErr.get());
				fnEnv.typeEnv.put(p, ptype);
			}
			fnEnv.valEnv.put(p, val);
		}
		return java.util.Optional.of(evaluateExpression(fd.bodyExpr, fnEnv));
	}

	// Extracted helper to parse and record a zero-arg function declaration to
	// reduce
	// cyclomatic complexity of handleStatement.
	private java.util.Optional<Result<String, InterpretError>> handleFnDecl(String s, Env env) {
		int len = s.length();
		int idx = skipWs(s, 2, len); // after 'fn'
		java.util.Optional<ParseId> pidOpt = parseId(s, idx, len);
		if (!pidOpt.isPresent())
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn declaration", env.source)));
		ParseId pid = pidOpt.get();
		String name = pid.name;
		idx = skipWs(s, pid.idx, len);
		// parse empty parameter list '()'
		if (idx >= len || s.charAt(idx) != '(')
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn syntax", env.source)));
		int close = s.indexOf(')', idx + 1);
		if (close < 0)
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn syntax", env.source)));
		String params = s.substring(idx + 1, close).trim();
		java.util.List<String> paramNames = new java.util.ArrayList<>();
		java.util.List<String> paramTypes = new java.util.ArrayList<>();
		if (!params.isEmpty()) {
			// split on commas and parse each param of the form: <ident> (':' <suffix>)?
			for (String part : params.split(",")) {
				String p = part.trim();
				int plen = p.length();
				java.util.Optional<ParseId> pPid = parseId(p, 0, plen);
				if (!pPid.isPresent())
					return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn parameter", env.source)));
				ParseId pp = pPid.get();
				String pname = pp.name;
				int after = skipWs(p, pp.idx, plen);
				String ptype = parseAnnotatedSuffix(p, after, plen);
				paramNames.add(pname);
				paramTypes.add(ptype);
			}
		}
		// annotated suffix after ')'
		String ann = parseAnnotatedSuffix(s, close + 1, len);
		// find '=>' after annotation
		int arrow = s.indexOf("=>", close + 1);
		if (arrow < 0)
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid fn body", env.source)));
		FnBodyParse fb = extractFnReturnExpr(s, arrow, env);
		if (fb.error.isPresent())
			return java.util.Optional.of(fb.error.get());
		String retExpr = fb.retExpr;
		// record the function declaration in env
		env.fnEnv.put(name, new FunctionDecl(name, ann, retExpr));
		if (!paramNames.isEmpty()) {
			env.fnParamNames.put(name, paramNames);
		}
		if (!paramTypes.isEmpty()) {
			env.fnParamTypes.put(name, paramTypes);
		}
		return java.util.Optional.empty();
	}

	// Extracted helper to handle while statements to reduce cyclomatic complexity
	private java.util.Optional<Result<String, InterpretError>> handleWhile(String s, Env env) {
		int open = s.indexOf('(');
		int close = s.indexOf(')', open + 1);
		if (open < 0 || close < 0)
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid while syntax", env.source)));
		String condExpr = s.substring(open + 1, close).trim();
		String after = computeAfter(new String[] { s }, 0); // compute inline after
		java.util.Optional<String> bodyStmt = java.util.Optional.empty();
		if (!after.isEmpty()) {
			bodyStmt = java.util.Optional.of(after);
		} else {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("while requires inline body", env.source)));
		}

		// Execute the loop
		while (true) {
			Result<String, InterpretError> condRes = evaluateExpression(condExpr, env);
			if (condRes instanceof Result.Err)
				return java.util.Optional.of((Result<String, InterpretError>) condRes);
			String condVal = ((Result.Ok<String, InterpretError>) condRes).value();
			boolean condTrue = "true".equals(condVal);
			if (!condTrue)
				break;
			java.util.Optional<Result<String, InterpretError>> bodyRes = handleStatement(bodyStmt.get(), env);
			if (bodyRes.isPresent())
				return bodyRes;
		}
		return java.util.Optional.empty();
	}

	// Helper to hold consequent/alternative extraction result
	private static final class ConsAlt {
		final String cons;
		final String alt;
		final int consumedIdx;
		final java.util.Optional<Result<String, InterpretError>> error;

		ConsAlt(String cons, String alt, int consumedIdx) {
			this.cons = cons;
			this.alt = alt;
			this.consumedIdx = consumedIdx;
			this.error = java.util.Optional.empty();
		}

		ConsAlt(java.util.Optional<Result<String, InterpretError>> error) {
			this.cons = "";
			this.alt = "";
			this.consumedIdx = -1;
			this.error = error;
		}
	}

	// Extract consequent and alternative strings for an if starting at parts[idx].
	// closeParenIdx is the index of ')' within parts[idx] used to find inline text.
	private ConsAlt findConsAlt(String[] parts, int idx, Env env) {
		String after = computeAfter(parts, idx);
		if (!after.isEmpty())
			return inlineAfter(parts, idx, env);
		return extractSeparated(parts, idx, env);
	}

	private ConsAlt inlineAfter(String[] parts, int idx, Env env) {
		String after = computeAfter(parts, idx);
		int elseIdx = indexOfElse(after);
		if (elseIdx >= 0) {
			String cons = after.substring(0, elseIdx).trim();
			String alt = after.substring(elseIdx + 4).trim();
			return new ConsAlt(cons, alt, idx);
		}
		String cons = after;
		AltInfo ai = nextAltInfo(parts, idx + 1, env);
		if (ai.error.isPresent())
			return new ConsAlt(ai.error);
		return new ConsAlt(cons, ai.alt, ai.consumedIdx);
	}

	private ConsAlt extractSeparated(String[] parts, int idx, Env env) {
		int consIdx = idx + 1;
		while (consIdx < parts.length && parts[consIdx].trim().isEmpty())
			consIdx++;
		if (consIdx >= parts.length)
			return new ConsAlt(
					java.util.Optional.of(new Result.Err<>(new InterpretError("missing consequent in if", env.source))));
		String consPart = parts[consIdx].trim();
		int elseIdx = indexOfElse(consPart);
		if (elseIdx >= 0) {
			String cons = consPart.substring(0, elseIdx).trim();
			String alt = consPart.substring(elseIdx + 4).trim();
			return new ConsAlt(cons, alt, consIdx);
		}
		AltInfo ai = nextAltInfo(parts, consIdx + 1, env);
		if (ai.error.isPresent())
			return new ConsAlt(ai.error);
		return new ConsAlt(consPart, ai.alt, ai.consumedIdx);
	}

	private static final class AltInfo {
		final String alt;
		final int consumedIdx;
		final java.util.Optional<Result<String, InterpretError>> error;

		AltInfo(String alt, int consumedIdx) {
			this.alt = alt;
			this.consumedIdx = consumedIdx;
			this.error = java.util.Optional.empty();
		}

		AltInfo(java.util.Optional<Result<String, InterpretError>> error) {
			this.alt = "";
			this.consumedIdx = -1;
			this.error = error;
		}
	}

	private AltInfo nextAltInfo(String[] parts, int startIdx, Env env) {
		int altIdx = startIdx;
		while (altIdx < parts.length && parts[altIdx].trim().isEmpty())
			altIdx++;
		if (altIdx >= parts.length)
			return new AltInfo(
					java.util.Optional.of(new Result.Err<>(new InterpretError("missing alternative in if", env.source))));
		String altPart = parts[altIdx].trim();
		String altPartTrimmed = altPart.startsWith("else") ? altPart.substring(4).trim() : altPart;
		return new AltInfo(altPartTrimmed, altIdx);
	}

	// Compute inline text following the closing ')' in parts[idx], or empty if
	// there's no inline consequent. Extracted to avoid duplicated code paths.
	private String computeAfter(String[] parts, int idx) {
		String part = parts[idx].trim();
		int close = part.indexOf(')');
		String after = "";
		if (close >= 0 && close + 1 < part.length())
			after = part.substring(close + 1).trim();
		return after;
	}

	// Split source into top-level parts separated by semicolons, ignoring
	// semicolons that appear inside brace-delimited blocks.
	private String[] splitTopLevel(String source) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		StringBuilder sb = new StringBuilder();
		int depth = 0;
		int len = source.length();
		for (int j = 0; j < len; j++) {
			char c = source.charAt(j);
			if (c == '{') {
				depth++;
				sb.append(c);
			} else if (c == '}') {
				// closing a brace
				depth--;
				sb.append(c);
				// If we've returned to depth 0, and the following non-space token is
				// not 'else' and is not a semicolon or end-of-input, then treat this
				// as a statement boundary (implicit semicolon) so that trailing
				// expressions after a block (e.g., "} x") become a separate part.
				if (depth == 0) {
					int k = j + 1;
					while (k < len && Character.isWhitespace(source.charAt(k)))
						k++;
					if (k < len) {
						char nc = source.charAt(k);
						if (nc != ';') {
							String rest = source.substring(k);
							if (!rest.startsWith("else")) {
								parts.add(sb.toString());
								sb.setLength(0);
							}
						}
					}
				}
			} else if (c == ';' && depth == 0) {
				parts.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		// add trailing part
		parts.add(sb.toString());
		return parts.toArray(new String[0]);
	}

	// Small holder for runtime environments and program source so we avoid
	// passing multiple maps/strings as separate parameters (keeps parameter
	// counts below the Checkstyle limit).
	private static final class Env {
		final java.util.Map<String, String> valEnv;
		final java.util.Map<String, String> typeEnv;
		final java.util.Map<String, FunctionDecl> fnEnv;
		final java.util.Map<String, java.util.List<String>> fnParamNames;
		final java.util.Map<String, java.util.List<String>> fnParamTypes;
		final java.util.Map<String, Boolean> mutEnv;
		final String source;

		Env(java.util.Map<String, String> valEnv, java.util.Map<String, String> typeEnv, String source) {
			this.valEnv = valEnv;
			this.typeEnv = typeEnv;
			this.fnEnv = new java.util.HashMap<>();
			this.fnParamNames = new java.util.HashMap<>();
			this.fnParamTypes = new java.util.HashMap<>();
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

	// Small helper to represent a zero-arg function declaration with a typed
	// Helper to represent a function declaration (supports optional single
	// parameter). The bodyExpr holds the expression returned by the function.
	private static final class FunctionDecl {
		final String name;
		final String returnType; // e.g., I32 or Bool
		final String bodyExpr; // expression returned by the function

		FunctionDecl(String name, String returnType, String bodyExpr) {
			this.name = name;
			this.returnType = returnType;
			this.bodyExpr = bodyExpr;
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
		return evaluateExpression(s, env);
	}

	// Evaluate a semicolon-separated program supporting `let` declarations.
	private Result<String, InterpretError> evaluateSequence(String source) {
		// Split at top-level semicolons only (ignore semicolons inside braces)
		String[] parts = splitTopLevel(source);
		java.util.Map<String, String> valEnv = new java.util.HashMap<>();
		java.util.Map<String, String> typeEnv = new java.util.HashMap<>();
		Env env = new Env(valEnv, typeEnv, source);
		int i = 0;

		while (i < parts.length) {
			String part = parts[i].trim();
			if (part.isEmpty()) {
				i++;
				continue;
			}
			boolean isLast = (i == parts.length - 1);
			if (!isLast) {
				// Statement position
				// Support a simple if-statement form that uses the next two parts as
				// consequent and alternative: `if (cond) <stmt>; else <stmt>;`
				if (part.startsWith("if ") || part.startsWith("if(")) {
					IfOutcome fo = processIfPart(parts, i, env);
					if (fo.result.isPresent())
						return fo.result.get();
					i = fo.consumedIdx + 1;
					continue;
				}

				// Statement position: expect a let-binding or assignment
				java.util.Optional<Result<String, InterpretError>> stmtRes = handleStatement(part, env);
				if (stmtRes.isPresent())
					return stmtRes.get();
				i++;
			} else {
				// Final expression: evaluate and return
				return evaluateExpression(part, env);
			}
		}
		// If we reached the end without a final expression (e.g., trailing semicolon or
		// only statements),
		// return empty string as the program result.
		return new Result.Ok<>("");
	}

	private java.util.Optional<Result<String, InterpretError>> handleStatement(String stmt, Env env) {
		String s = stmt.trim();
		// Support block statements of the form `{ ... }` containing one or more
		// inner statements separated by semicolons. Execute inner statements in
		// the same environment. If any inner statement returns a Result (error
		// or an expression result), surface it immediately.
		if (s.startsWith("{")) {
			int close = s.lastIndexOf('}');
			String body = close > 0 ? s.substring(1, close) : "";
			String[] inner = body.split(";", -1);
			// Evaluate block body in a child env so inner lets/assignments do not
			// mutate the outer environment.
			Env child = makeChildEnv(env);
			for (String part : inner) {
				String t = part.trim();
				if (t.isEmpty())
					continue;
				java.util.Optional<Result<String, InterpretError>> innerRes = handleStatement(t, child);
				if (innerRes.isPresent())
					return innerRes;
			}
			return java.util.Optional.empty();
		}
		// Support while statements by delegating to helper to keep this method small
		if (s.startsWith("while ") || s.startsWith("while(")) {
			return handleWhile(s, env);
		}
		// Support function declarations: fn <id>() : <suffix> => { return <expr>; }
		if (s.startsWith("fn ") || s.startsWith("fn(")) {
			return handleFnDecl(s, env);
		}
		// Support assignment statements and let declarations; extracted to reduce
		// cyclomatic complexity of this method.
		return handleSimpleStmt(s, env);
	}

	// Extracted helper to handle assignments, compound assignments and let
	// declarations.
	private java.util.Optional<Result<String, InterpretError>> handleSimpleStmt(String s, Env env) {
		// Support compound assignment '+=', and simple assignment '=' in statement
		// position
		int plusEqIdx = s.indexOf("+=");
		if (plusEqIdx >= 0 && !s.startsWith("let ")) {
			return handleCompAssign(s, plusEqIdx, env);
		}
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
		// If there was no explicit annotation but the initializer is a boolean
		// literal, record an inferred Bool annotation so future assignments are
		// checked against the boolean type.
		if (d.annotatedSuffix.isEmpty()) {
			if ("true".equals(value) || "false".equals(value)) {
				env.typeEnv.put(d.name, "Bool");
			}
		}
		env.valEnv.put(d.name, value);
		// Record mutability
		env.mutEnv.put(d.name, d.mutable ? Boolean.TRUE : Boolean.FALSE);
		return java.util.Optional.empty();
	}

	private Result<String, InterpretError> getRhsValue(String rhs,
			java.util.Map<String, String> valEnv,
			java.util.Map<String, String> typeEnv) {
		Env tmp = new Env(valEnv, typeEnv, "");
		return evaluateExpression(rhs, tmp);
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
		AssignPrep prep = prepAssignOps(lhs, rhs, env);
		if (prep.error.isPresent())
			return java.util.Optional.of(prep.error.get());
		String evaluated = prep.evaluatedRhs.get();
		// Enforce assignment value compatibility against annotated/inferred type
		java.util.Optional<Result<String, InterpretError>> valCheck = vAssignValue(lhs, evaluated, env);
		if (valCheck.isPresent())
			return java.util.Optional.of(valCheck.get());

		env.valEnv.put(lhs, evaluated);
		return java.util.Optional.empty();
	}

	// Small helper to hold prepared assignment operands
	private static final class AssignPrep {
		final java.util.Optional<String> evaluatedRhs;
		final java.util.Optional<Result<String, InterpretError>> error;

		AssignPrep(String evaluatedRhs) {
			this.evaluatedRhs = java.util.Optional.of(evaluatedRhs);
			this.error = java.util.Optional.empty();
		}

		AssignPrep(Result<String, InterpretError> error) {
			this.evaluatedRhs = java.util.Optional.empty();
			this.error = java.util.Optional.of(error);
		}
	}

	// Prepare and validate lhs/rhs for assignment: validate identifier, mutability,
	// check suffix compatibility, and evaluate the RHS. Returns AssignPrep where
	// 'error' is present on failure; otherwise 'evaluatedRhs' holds the RHS value.
	private AssignPrep prepAssignOps(String lhs, String rhs, Env env) {
		if (!isSimpleIdentifier(lhs))
			return new AssignPrep(new Result.Err<>(new InterpretError("invalid assignment lhs", env.source)));
		// Unknown in valEnv can be acceptable if declared without initializer (present
		// in typeEnv)
		if (!env.valEnv.containsKey(lhs) && !env.typeEnv.containsKey(lhs))
			return new AssignPrep(new Result.Err<>(new InterpretError("unknown identifier in assignment", env.source)));
		Boolean isMut = env.mutEnv.getOrDefault(lhs, Boolean.FALSE);
		if (!isMut)
			return new AssignPrep(new Result.Err<>(new InterpretError("assignment to immutable variable", env.source)));

		// Validate RHS suffix vs annotated/inferred type (if any) before evaluating
		java.util.Optional<Result<String, InterpretError>> suffixCheck = vRhsSuffix(lhs, rhs, env);
		if (suffixCheck.isPresent())
			return new AssignPrep(suffixCheck.get());

		Result<String, InterpretError> rhsVal = evaluateExpression(rhs, env);
		if (rhsVal instanceof Result.Err)
			return new AssignPrep((Result<String, InterpretError>) rhsVal);
		String value = ((Result.Ok<String, InterpretError>) rhsVal).value();
		return new AssignPrep(value);
	}

	// Handle compound assignment of the form '<ident> += <expr>'
	private java.util.Optional<Result<String, InterpretError>> handleCompAssign(String stmt, int plusEqIdx,
			Env env) {
		String lhs = stmt.substring(0, plusEqIdx).trim();
		String rhs = stmt.substring(plusEqIdx + 2).trim();
		// Reuse common preparation logic for assignments
		AssignPrep ap = prepAssignOps(lhs, rhs, env);
		if (ap.error.isPresent())
			return java.util.Optional.of(ap.error.get());
		// ap.evaluatedRhs is the evaluated RHS value
		String rhsValue = ap.evaluatedRhs.get();

		// Fetch current lhs value; it must exist in valEnv for compound assignment
		if (!env.valEnv.containsKey(lhs))
			return java.util.Optional.of(new Result.Err<>(new InterpretError("read of uninitialized variable", env.source)));
		String lhsValue = env.valEnv.get(lhs);

		try {
			java.math.BigInteger a = new java.math.BigInteger(lhsValue);
			java.math.BigInteger b = new java.math.BigInteger(rhsValue);
			java.math.BigInteger sum = a.add(b);
			String sumStr = sum.toString();

			// Validate resulting assignment against annotated/inferred type
			java.util.Optional<Result<String, InterpretError>> valCheck = vAssignValue(lhs, sumStr, env);
			if (valCheck.isPresent())
				return java.util.Optional.of(valCheck.get());

			env.valEnv.put(lhs, sumStr);
			return java.util.Optional.empty();
		} catch (NumberFormatException ex) {
			return java.util.Optional
					.of(new Result.Err<>(new InterpretError("invalid integer in compound assignment", env.source)));
		}
	}

	// Validate RHS textual suffix against the variable's annotated/inferred type
	private java.util.Optional<Result<String, InterpretError>> vRhsSuffix(String lhs, String rhs,
			Env env) {
		ParseResult rhsPr = parseSignAndDigits(rhs.trim());
		if (!env.typeEnv.containsKey(lhs))
			return java.util.Optional.empty();
		String ann = env.typeEnv.get(lhs);
		if (ann.equalsIgnoreCase("Bool")) {
			// If RHS is a typed literal (e.g., 3U8) that's incompatible with Bool
			if (rhsPr.valid && !rhsPr.suffix.isEmpty())
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", env.source)));
			return java.util.Optional.empty();
		}
		if (rhsPr.valid && !rhsPr.suffix.isEmpty()) {
			String rhsSuf = rhsPr.suffix.toUpperCase();
			if (!ann.toUpperCase().equals(rhsSuf))
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", env.source)));
		}
		return java.util.Optional.empty();
	}

	// Validate the evaluated RHS value against the variable's annotated/inferred
	// type
	private java.util.Optional<Result<String, InterpretError>> vAssignValue(String lhs, String value,
			Env env) {
		if (!env.typeEnv.containsKey(lhs))
			return java.util.Optional.empty();
		String ann = env.typeEnv.get(lhs);
		if (ann.equalsIgnoreCase("Bool")) {
			if (!"true".equals(value) && !"false".equals(value))
				return java.util.Optional
						.of(new Result.Err<>(new InterpretError("mismatched assignment to Bool variable", env.source)));
		} else {
			// For numeric typed annotations, ensure the evaluated RHS fits the annotated
			// type
			java.util.Optional<Result<String, InterpretError>> chk = checkAnnotatedSuffix(ann, value, env);
			if (chk.isPresent())
				return chk;
		}
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

	private Result<String, InterpretError> evaluateExpression(String expr, Env env) {
		String s = stripUnaryPrefixes(expr.trim());
		// Block expression handling delegated to helper to keep this method small.
		if (s.startsWith("{") && s.endsWith("}"))
			return evalBlockExpr(s, env);
		// Boolean literals
		if (s.equals("true") || s.equals("false"))
			return new Result.Ok<>(s);

		// function call: <ident>(...) (supports zero-arg or single-arg calls)
		java.util.Optional<Result<String, InterpretError>> fnCall = tryEvalFunctionCall(s, env);
		if (fnCall.isPresent())
			return fnCall.get();
		// variable reference
		if (isSimpleIdentifier(s)) {
			if (env.valEnv.containsKey(s))
				return new Result.Ok<>(env.valEnv.get(s));
			return new Result.Err<>(new InterpretError("unknown identifier", expr));
		}
		// comparison (e.g., '<')
		java.util.Optional<Result<String, InterpretError>> cmpRes = tryEvalComp(s, env);
		if (cmpRes.isPresent())
			return cmpRes.get();
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

	// Strip leading transparent unary prefixes '*' and '&' from the expression
	// and return the trimmed remainder. Extracted into a helper to keep
	// evaluateExpression under the cyclomatic complexity threshold.
	private String stripUnaryPrefixes(String s) {
		int p = 0;
		int len = s.length();
		while (p < len) {
			char c = s.charAt(p);
			if (c == '*' || c == '&')
				p++;
			else
				break;
		}
		if (p > 0)
			return s.substring(p).trim();
		return s;
	}

	// Try to evaluate a simple comparison of the form "<expr> < <expr>" where
	// each side is an expression that evaluates to an integer. Returns an
	// Ok("true"/"false")
	// or an empty Optional if not applicable.
	private java.util.Optional<Result<String, InterpretError>> tryEvalComp(String s, Env env) {
		int ltIdx = s.indexOf('<');
		if (ltIdx <= 0)
			return java.util.Optional.empty();
		String leftRaw = s.substring(0, ltIdx).trim();
		String rightRaw = s.substring(ltIdx + 1).trim();

		// Evaluate both sides as expressions (they may be additions or identifiers)
		Result<String, InterpretError> leftRes = evaluateExpression(leftRaw, env);
		if (leftRes instanceof Result.Err)
			return java.util.Optional.of((Result<String, InterpretError>) leftRes);
		Result<String, InterpretError> rightRes = evaluateExpression(rightRaw, env);
		if (rightRes instanceof Result.Err)
			return java.util.Optional.of((Result<String, InterpretError>) rightRes);
		String leftVal = ((Result.Ok<String, InterpretError>) leftRes).value();
		String rightVal = ((Result.Ok<String, InterpretError>) rightRes).value();
		try {
			java.math.BigInteger a = new java.math.BigInteger(leftVal);
			java.math.BigInteger b = new java.math.BigInteger(rightVal);
			return java.util.Optional.of(new Result.Ok<>(a.compareTo(b) < 0 ? "true" : "false"));
		} catch (NumberFormatException ex) {
			return java.util.Optional.of(new Result.Err<>(new InterpretError("invalid integer in comparison", s)));
		}
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

	// Return the index of the token "else" in s if it appears as a separate
	// token or at the start of s; otherwise -1. This is a small helper used by
	// the inline-if parsing to detect an 'else' following a consequent.
	private int indexOfElse(String s) {
		if (Objects.isNull(s))
			return -1;
		String trimmed = s.trim();
		if (trimmed.startsWith("else ") || trimmed.equals("else"))
			return 0;
		// find ' else ' with surrounding whitespace
		int idx = trimmed.indexOf(" else ");
		return idx;
	}

	// Evaluate a brace-delimited block expression and return the value of the
	// final inner expression (or appropriate Err). This is separated from
	// evaluateExpression to reduce cyclomatic complexity and satisfy Checkstyle.
	private Result<String, InterpretError> evalBlockExpr(String block, Env env) {
		String s = block.trim();
		String body = s.length() > 1 ? s.substring(1, s.length() - 1) : "";
		String[] inner = body.split(";", -1);
		Result<String, InterpretError> last = new Result.Ok<>("");
		// Use a child environment for the block so inner let bindings do not
		// leak into the outer environment. The child shares functions and
		// mutability info but has its own value/type maps that start as copies
		// of the parent's maps.
		Env child = makeChildEnv(env);
		for (String part : inner) {
			String t = part.trim();
			if (t.isEmpty())
				continue;
			if (isStmtLike(t)) {
				java.util.Optional<Result<String, InterpretError>> stmtRes = handleStatement(t, child);
				if (stmtRes.isPresent()) {
					Result<String, InterpretError> r = stmtRes.get();
					if (r instanceof Result.Err)
						return r;
					last = r;
					continue;
				}
				continue;
			}
			Result<String, InterpretError> r = evaluateExpression(t, child);
			if (r instanceof Result.Err)
				return r;
			last = r;
		}
		return last;
	}

	// Create a child Env for block evaluation: shallow-copy val/type maps so
	// that assignments and let-declarations inside the block do not mutate the
	// outer environment.
	private Env makeChildEnv(Env parent) {
		java.util.Map<String, String> newVals = new java.util.HashMap<>(parent.valEnv);
		java.util.Map<String, String> newTypes = new java.util.HashMap<>(parent.typeEnv);
		Env child = new Env(newVals, newTypes, parent.source);
		child.fnEnv.putAll(parent.fnEnv);
		child.fnParamNames.putAll(parent.fnParamNames);
		child.fnParamTypes.putAll(parent.fnParamTypes);
		child.mutEnv.putAll(parent.mutEnv);
		return child;
	}

	private boolean isStmtLike(String t) {
		return t.startsWith("{") || t.startsWith("while ") || t.startsWith("while(")
				|| t.startsWith("fn ") || t.startsWith("fn(") || t.startsWith("if ") || t.startsWith("if(")
				|| t.startsWith("let ") || t.indexOf("+=") >= 0 || (t.indexOf('=') > 0 && !t.startsWith("let "));
	}

	// Outcome of processing an if-part: either a Result to return (if the
	// branch produced an expression result or an error), or the index of the
	// last consumed part so the caller can advance the main loop.
	private static final class IfOutcome {
		final java.util.Optional<Result<String, InterpretError>> result;
		final int consumedIdx;

		IfOutcome(java.util.Optional<Result<String, InterpretError>> result, int consumedIdx) {
			this.result = result;
			this.consumedIdx = consumedIdx;
		}
	}

	// Process an if-statement part starting at parts[idx]. Returns an IfOutcome
	// containing either a Result to surface to the caller, or the index of the
	// consumed part when the if completed normally.
	private IfOutcome processIfPart(String[] parts, int idx, Env env) {
		String part = parts[idx].trim();
		int open = part.indexOf('(');
		int close = part.indexOf(')', open + 1);
		if (open < 0 || close < 0)
			return new IfOutcome(java.util.Optional.of(new Result.Err<>(new InterpretError("invalid if syntax", env.source))),
					idx);
		String condExpr = part.substring(open + 1, close).trim();

		// Delegate extraction of consequent and alternative to helper
		ConsAlt ca = findConsAlt(parts, idx, env);
		if (ca.error.isPresent())
			return new IfOutcome(ca.error, idx);
		String cons = ca.cons;
		String alt = ca.alt;
		int consumedAltIdx = ca.consumedIdx;

		// Evaluate condition
		Result<String, InterpretError> condRes = evaluateExpression(condExpr, env);
		if (condRes instanceof Result.Err)
			return new IfOutcome(java.util.Optional.of((Result<String, InterpretError>) condRes), consumedAltIdx);
		String condVal = ((Result.Ok<String, InterpretError>) condRes).value();
		boolean condTrue = "true".equals(condVal);

		java.util.Optional<Result<String, InterpretError>> stmtRes = condTrue ? handleStatement(cons, env)
				: handleStatement(alt, env);
		if (stmtRes.isPresent())
			return new IfOutcome(stmtRes, consumedAltIdx);

		return new IfOutcome(java.util.Optional.empty(), consumedAltIdx);
	}
}
