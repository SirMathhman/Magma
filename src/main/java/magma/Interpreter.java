package magma;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

/**
 * Interpreter for a tiny language used by the project tests.
 */
public class Interpreter {
	// Helper to extract the return expression for a function body.
	// It supports a block form '{ ... }' or a compact form '=> return <expr>;' (no
	// braces).
	private record FnBodyParse(Optional<Result<String, InterpretError>> error, String retExpr) {
	}

	// Evaluate a brace-delimited block statement and manage block-local let
	// declarations. This helper was extracted to reduce cyclomatic complexity
	// in handleStatement.
	private Optional<Result<String, InterpretError>> evaluateBlock(String s, Env env) {
		int close = s.lastIndexOf('}');
		String body = close > 0 ? s.substring(1, close) : "";
		String[] inner = body.split(";", -1);
		boolean createdLocalSet = false;
		if (env.localDecls.isEmpty()) {
			env.localDecls = java.util.Optional.of(new HashSet<String>());
			createdLocalSet = true;
		}
		for (String part : inner) {
			String t = part.trim();
			if (t.isEmpty())
				continue;
			Optional<Result<String, InterpretError>> innerRes = handleStatement(t, env);
			if (innerRes.isPresent()) {
				cleanupLocalDecls(env, createdLocalSet);
				return innerRes;
			}
		}
		cleanupLocalDecls(env, createdLocalSet);
		return Optional.empty();
	}

	// Remove block-local declarations that were created for the current block.
	private void cleanupLocalDecls(Env env, boolean createdLocalSet) {
		if (createdLocalSet && env.localDecls.isPresent()) {
			for (String name : env.localDecls.get()) {
				env.valEnv.remove(name);
				env.typeEnv.remove(name);
				env.mutEnv.remove(name);
			}
			env.localDecls = java.util.Optional.empty();
		}
	}

	// Helper to hold consequent/alternative extraction result
	private static final class ConsAlt {
		final String cons;
		final String alt;
		final int consumedIdx;
		final Optional<Result<String, InterpretError>> error;

		ConsAlt(String cons, String alt, int consumedIdx) {
			this.cons = cons;
			this.alt = alt;
			this.consumedIdx = consumedIdx;
			this.error = Optional.empty();
		}

		ConsAlt(Optional<Result<String, InterpretError>> error) {
			this.cons = "";
			this.alt = "";
			this.consumedIdx = -1;
			this.error = error;
		}
	}

	// Request object to resolve LHS into the actual target variable name. Use
	// a compact 3-arg constructor to satisfy ParameterNumber check.
	private static final class LhsResolveReq {
		final String derefTarget; // non-empty if LHS was a deref like '*x'
		final String indexBase; // non-empty if LHS was indexed like 'x[0]'
		final String lhsRaw; // original lhs text

		LhsResolveReq(String derefTarget, String indexBase, String lhsRaw) {
			this.derefTarget = Objects.toString(derefTarget, "");
			this.indexBase = Objects.toString(indexBase, "");
			this.lhsRaw = Objects.toString(lhsRaw, "");
		}
	}

	// Resolve an LHS into the actual variable name that should receive assignment.
	// Returns Ok(name) or Err(...)
	private Result<String, InterpretError> resolveLhsTarget(LhsResolveReq req, Env env) {
		if (!req.derefTarget.isEmpty()) {
			DerefResolve dr = resolveDerefTarget(req.derefTarget, env);
			if (dr.error.isPresent())
				return dr.error.get();
			return new Result.Ok<>(dr.targetName);
		}
		if (!req.indexBase.isEmpty()) {
			Optional<Result<String, InterpretError>> chk = chkExistsMut(req.indexBase, env);
			if (chk.isPresent())
				return chk.get();
			return new Result.Ok<>(req.indexBase);
		}
		// simple identifier
		Optional<Result<String, InterpretError>> chk = chkExistsMut(req.lhsRaw, env);
		if (chk.isPresent())
			return chk.get();
		return new Result.Ok<>(req.lhsRaw);
	}

	private static final class AltInfo {
		final String alt;
		final int consumedIdx;
		final Optional<Result<String, InterpretError>> error;

		AltInfo(String alt, int consumedIdx) {
			this.alt = alt;
			this.consumedIdx = consumedIdx;
			this.error = Optional.empty();
		}

		AltInfo(Optional<Result<String, InterpretError>> error) {
			this.alt = "";
			this.consumedIdx = -1;
			this.error = error;
		}
	}

	// Small holder for runtime environments and program source so we avoid
	// passing multiple maps/strings as separate parameters (keeps parameter
	// counts below the Checkstyle limit).
	private static final class Env {
		final Map<String, String> valEnv;
		final Map<String, String> typeEnv;
		final Map<String, FunctionDecl> fnEnv;
		final Map<String, List<String>> fnParamNames;
		final Map<String, List<String>> fnParamTypes;
		final Map<String, Boolean> mutEnv;
		final Map<String, java.util.List<String>> structEnv;
		final Map<String, java.util.List<String>> structFieldTypes;
		final String source;

		// Optional collector of let-declarations that should be treated as
		// block-local so they can be removed/restored after the block completes.
		java.util.Optional<Set<String>> localDecls;

		Env(Map<String, String> valEnv, Map<String, String> typeEnv, String source) {
			this.valEnv = valEnv;
			this.typeEnv = typeEnv;
			this.fnEnv = new HashMap<>();
			this.fnParamNames = new HashMap<>();
			this.fnParamTypes = new HashMap<>();
			this.mutEnv = new HashMap<>();
			this.structEnv = new HashMap<>();
			this.structFieldTypes = new HashMap<>();
			this.source = source;
			this.localDecls = java.util.Optional.empty();
		}

	}

	// Small record to hold a field name and its annotated type (may be empty).
	private record FieldSpec(String name, String type) {
	}

	// Helper to represent a parsed type kind and width together so checks can
	// take a single object rather than separate primitive params.
	private record TypeSpec(char kind, int width) {
	}

	/**
	 * @param returnType e.g., I32 or Bool
	 * @param bodyExpr   expression returned by the function
	 */ // Small helper to represent a zero-arg function declaration with a typed
	// Helper to represent a function declaration (supports optional single
	// parameter). The bodyExpr holds the expression returned by the function.
	private record FunctionDecl(String name, String returnType, String bodyExpr) {
	}

	// Small helper to hold prepared assignment operands
	private static final class AssignPrep {
		final Optional<String> evaluatedRhs;
		final Optional<Result<String, InterpretError>> error;

		AssignPrep(String evaluatedRhs) {
			this.evaluatedRhs = Optional.of(evaluatedRhs);
			this.error = Optional.empty();
		}

		AssignPrep(Result<String, InterpretError> error) {
			this.evaluatedRhs = Optional.empty();
			this.error = Optional.of(error);
		}
	}

	// Helper used to resolve a dereference target variable's referenced target.
	private static final class DerefResolve {
		final String targetName;
		final Optional<Result<String, InterpretError>> error;

		DerefResolve(String targetName) {
			this.targetName = targetName;
			this.error = Optional.empty();
		}

		DerefResolve(Optional<Result<String, InterpretError>> error) {
			this.targetName = "";
			this.error = error;
		}
	}

	// Small helper to represent a parsed let declaration
	private static final class LetDeclaration {
		final String name;
		final String annotatedSuffix;
		final String rhs;
		boolean mutable;

		LetDeclaration(String name, String annotatedSuffix, String rhs) {
			this.name = name;
			this.annotatedSuffix = annotatedSuffix;
			this.rhs = rhs;
			this.mutable = false;
		}
	}

	// Helper to represent an indexed LHS parse result
	private static final class IndexedLhs {
		final String base;
		final String indexExpr;

		IndexedLhs(String base, String indexExpr) {
			this.base = base;
			this.indexExpr = indexExpr;
		}
	}

	// Parse an indexed LHS like 'x[0]' and return base and index expression.
	private java.util.Optional<IndexedLhs> parseIndexedLhs(String s) {
		int lastBracket = s.lastIndexOf(']');
		if (lastBracket != s.length() - 1)
			return java.util.Optional.empty();
		int openBracket = findOpenBracket(s, lastBracket);
		if (!(openBracket > 0))
			return java.util.Optional.empty();
		String base = s.substring(0, openBracket).trim();
		String idx = s.substring(openBracket + 1, lastBracket).trim();
		return java.util.Optional.of(new IndexedLhs(base, idx));
	}

	// Find opening '[' that matches the trailing ']' at lastBracket. Returns -1 if
	// none.
	private int findOpenBracket(String s, int lastBracket) {
		int openBracket = -1;
		int depth = 0;
		for (int i = lastBracket; i >= 0; i--) {
			char c = s.charAt(i);
			if (c == ']')
				depth++;
			else if (c == '[') {
				depth--;
				if (depth == 0) {
					openBracket = i;
					break;
				}
			}
		}
		return openBracket;
	}

	// Request object for indexed assignment to keep handler parameter count low
	private static final class IndexedAssignReq {
		final String base;
		final String indexExpr;
		final String valueExpr;

		IndexedAssignReq(String base, String indexExpr, String valueExpr) {
			this.base = base;
			this.indexExpr = indexExpr;
			this.valueExpr = valueExpr;
		}
	}

	// Check that a variable exists and is mutable; returns Optional error Result
	private Optional<Result<String, InterpretError>> chkExistsMut(String name, Env env) {
		if (!env.valEnv.containsKey(name) && !env.typeEnv.containsKey(name))
			return Optional.of(new Result.Err<>(new InterpretError("unknown identifier in assignment", env.source)));
		Boolean isMut = env.mutEnv.getOrDefault(name, Boolean.FALSE);
		if (!isMut)
			return Optional.of(new Result.Err<>(new InterpretError("assignment to immutable variable", env.source)));
		return Optional.empty();
	}

	// New handler with short name and low parameter count
	private AssignPrep handleIdxAssign(IndexedAssignReq req, Env env) {
		// Evaluate index
		Result<String, InterpretError> idxRes = evaluateExpression(req.indexExpr, env);
		if (idxRes instanceof Result.Err)
			return new AssignPrep(idxRes);
		String idxVal = ((Result.Ok<String, InterpretError>) idxRes).value();
		int idxInt;
		try {
			idxInt = Integer.parseInt(idxVal);
		} catch (NumberFormatException ex) {
			return new AssignPrep(new Result.Err<>(new InterpretError("invalid index", env.source)));
		}
		// Evaluate RHS value expression
		Result<String, InterpretError> rhsRes = evaluateExpression(req.valueExpr, env);
		if (rhsRes instanceof Result.Err)
			return new AssignPrep(rhsRes);
		String value = ((Result.Ok<String, InterpretError>) rhsRes).value();
		// Validate base exists and mutability
		Optional<Result<String, InterpretError>> chk = chkExistsMut(req.base, env);
		if (chk.isPresent())
			return new AssignPrep(chk.get());
		String baseVal = env.valEnv.getOrDefault(req.base, "");
		if (!baseVal.startsWith(ARR_PREFIX))
			return new AssignPrep(new Result.Err<>(new InterpretError("invalid array assignment", env.source)));
		String elemsJoined = baseVal.substring(ARR_PREFIX.length());
		java.util.List<String> elems = new java.util.ArrayList<>();
		if (!elemsJoined.isEmpty()) {
			for (String p : elemsJoined.split("\\|"))
				elems.add(p);
		}
		if (idxInt < 0 || idxInt >= elems.size())
			return new AssignPrep(new Result.Err<>(new InterpretError("index out of bounds", env.source)));
		elems.set(idxInt, value);
		String join = String.join("|", elems);
		return new AssignPrep(DREF_ASSIGN_PREFIX + req.base + ":" + ARR_PREFIX + join);
	}

	// small helper to return parsed identifier and next index
	private record ParseId(String name, int idx) {
	}

	// Small helper struct to hold parse results
	private record ParseResult(boolean valid, String integerPart, String suffix) {
		private static final ParseResult INVALID = new ParseResult(false, "", "");

		static ParseResult invalid() {
			return INVALID;
		}
	}

	// Outcome of processing an if-part: either a Result to return (if the
	// branch produced an expression result or an error), or the index of the
	// last consumed part so the caller can advance the main loop.
	private record IfOutcome(Optional<Result<String, InterpretError>> result, int consumedIdx) {
	}

	// Reference marker prefixes used to encode reference values in the string
	// result type. These are internal-only and chosen to avoid colliding with
	// numeric literal strings.
	private static final String REF_PREFIX = "@REF:";
	private static final String REFMUT_PREFIX = "@REFMUT:";
	// Internal marker for array values stored as strings: "@ARR:elem1|elem2|..."
	private static final String ARR_PREFIX = "@ARR:";
	// Internal marker for struct values: "@STR:Type|field=val|..."
	private static final String STR_PREFIX = "@STR:";
	private static final String DREF_ASSIGN_PREFIX = "@DREFASSIGN:";

	private Optional<Result<String, InterpretError>> err(String msg, String source) {
		return Optional.of(new Result.Err<>(new InterpretError(msg, source)));
	}

	private FnBodyParse extractFnReturnExpr(String s, int arrowIdx, Env env) {
		// look for a block first
		int bodyOpen = s.indexOf('{', arrowIdx + 2);
		if (bodyOpen >= 0) {
			int j = findMatchingBrace(s, bodyOpen);
			if (j < 0)
				return new FnBodyParse(Optional.of(new Result.Err<>(new InterpretError("unterminated fn body", env.source))),
						"");
			String body = s.substring(bodyOpen + 1, j).trim();
			int retIdx = body.indexOf("return");
			if (retIdx < 0)
				// No explicit return: treat the entire brace body as a block expression
				// evaluated at call time (e.g., `{ let x = 100; this }`).
				return new FnBodyParse(Optional.empty(), "{" + body + "}");
			int semi = body.indexOf(';', retIdx);
			if (semi < 0)
				return new FnBodyParse(
						Optional.of(new Result.Err<>(new InterpretError("missing ';' after return", env.source))), "");
			String expr = body.substring(retIdx + 6, semi).trim();
			return new FnBodyParse(Optional.empty(), expr);
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
				return new FnBodyParse(Optional.of(new Result.Err<>(new InterpretError("invalid fn body", env.source))), "");
		}
		int semi = s.indexOf(';', exprStart);
		int end = semi >= 0 ? semi : s.length();
		String expr = s.substring(exprStart, end).trim();
		if (expr.isEmpty())
			return new FnBodyParse(Optional.of(new Result.Err<>(new InterpretError("invalid fn body", env.source))), "");
		return new FnBodyParse(Optional.empty(), expr);
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
	private Optional<Result<String, InterpretError>> tryEvalFunctionCall(String s, Env env) {
		int parenIdx = s.indexOf('(');
		if (parenIdx <= 0 || !s.endsWith(")"))
			return Optional.empty();
		String name = s.substring(0, parenIdx).trim();
		String inside = s.substring(parenIdx + 1, s.length() - 1).trim();
		if (!isSimpleIdentifier(name))
			return Optional.empty();
		if (!env.fnEnv.containsKey(name))
			return Optional.of(new Result.Err<>(new InterpretError("unknown identifier", s)));
		FunctionDecl fd = env.fnEnv.get(name);
		List<String> paramNames = env.fnParamNames.getOrDefault(name, Collections.emptyList());
		List<String> paramTypes = env.fnParamTypes.getOrDefault(name, Collections.emptyList());
		// parse call arguments (comma-separated)
		List<String> args = new ArrayList<>();
		if (!inside.isEmpty()) {
			// split on commas (simple splitter; no nested expressions supported yet)
			for (String a : inside.split(",")) {
				args.add(a.trim());
			}
		}
		if (args.size() != paramNames.size()) {
			return Optional.of(new Result.Err<>(new InterpretError("argument count mismatch in call to " + name, s)));
		}
		// evaluate each argument in caller env
		List<String> evaluatedArgs = new ArrayList<>();
		for (String a : args) {
			Result<String, InterpretError> r = evaluateExpression(a, env);
			if (r instanceof Result.Err)
				return Optional.of(r);
			evaluatedArgs.add(((Result.Ok<String, InterpretError>) r).value());
		}
		// create a temporary env for function body evaluation: copy maps but do not
		// mutate caller env; bind parameters
		Map<String, String> newVals = new HashMap<>(env.valEnv);
		Map<String, String> newTypes = new HashMap<>(env.typeEnv);
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
				Optional<Result<String, InterpretError>> annErr = checkAnnotatedSuffix(ptype, val, env);
				if (annErr.isPresent())
					return annErr;
				fnEnv.typeEnv.put(p, ptype);
			}
			fnEnv.valEnv.put(p, val);
		}
		// Mark parameters as block-local declarations so `this` captured from a
		// block inside the function will include them. This allows returning
		// `this` to capture both parameters and inner let declarations.
		if (!paramNames.isEmpty()) {
			fnEnv.localDecls = java.util.Optional.of(new java.util.HashSet<>(paramNames));
		}
		return Optional.of(evaluateExpression(fd.bodyExpr, fnEnv));
	}

	// Extracted helper to parse and record a zero-arg function declaration to
	// reduce
	// cyclomatic complexity of handleStatement.
	private Optional<Result<String, InterpretError>> handleFnDecl(String s, Env env) {
		int len = s.length();
		int idx = skipWs(s, 2, len); // after 'fn'
		Optional<ParseId> pidOpt = parseId(s, idx, len);
		if (pidOpt.isEmpty())
			return Optional.of(new Result.Err<>(new InterpretError("invalid fn declaration", env.source)));
		ParseId pid = pidOpt.get();
		String name = pid.name;
		idx = skipWs(s, pid.idx, len);
		// parse empty parameter list '()'
		if (idx >= len || s.charAt(idx) != '(')
			return Optional.of(new Result.Err<>(new InterpretError("invalid fn syntax", env.source)));
		int close = s.indexOf(')', idx + 1);
		if (close < 0)
			return Optional.of(new Result.Err<>(new InterpretError("invalid fn syntax", env.source)));
		String params = s.substring(idx + 1, close).trim();
		List<String> paramNames = new ArrayList<>();
		List<String> paramTypes = new ArrayList<>();
		if (!params.isEmpty()) {
			// split on commas and parse each param of the form: <ident> (':' <suffix>)?
			for (String part : params.split(",")) {
				String p = part.trim();
				int plen = p.length();
				Optional<ParseId> pPid = parseId(p, 0, plen);
				if (pPid.isEmpty())
					return Optional.of(new Result.Err<>(new InterpretError("invalid fn parameter", env.source)));
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
			return Optional.of(new Result.Err<>(new InterpretError("invalid fn body", env.source)));
		FnBodyParse fb = extractFnReturnExpr(s, arrow, env);
		if (fb.error.isPresent())
			return fb.error;
		String retExpr = fb.retExpr;
		// record the function declaration in env
		env.fnEnv.put(name, new FunctionDecl(name, ann, retExpr));
		if (!paramNames.isEmpty()) {
			env.fnParamNames.put(name, paramNames);
		}
		if (!paramTypes.isEmpty()) {
			env.fnParamTypes.put(name, paramTypes);
		}
		return Optional.empty();
	}

	// Extracted helper to handle while statements to reduce cyclomatic complexity
	private Optional<Result<String, InterpretError>> handleWhile(String s, Env env) {
		int open = s.indexOf('(');
		int close = s.indexOf(')', open + 1);
		if (open < 0 || close < 0)
			return Optional.of(new Result.Err<>(new InterpretError("invalid while syntax", env.source)));
		String condExpr = s.substring(open + 1, close).trim();
		String after = computeAfter(new String[] { s }, 0); // compute inline after
		Optional<String> bodyStmt;
		if (!after.isEmpty()) {
			bodyStmt = Optional.of(after);
		} else {
			return Optional.of(new Result.Err<>(new InterpretError("while requires inline body", env.source)));
		}

		// Execute the loop
		while (true) {
			Result<String, InterpretError> condRes = evaluateExpression(condExpr, env);
			if (condRes instanceof Result.Err)
				return Optional.of(condRes);
			String condVal = ((Result.Ok<String, InterpretError>) condRes).value();
			boolean condTrue = "true".equals(condVal);
			if (!condTrue)
				break;
			Optional<Result<String, InterpretError>> bodyRes = handleStatement(bodyStmt.get(), env);
			if (bodyRes.isPresent())
				return bodyRes;
		}
		return Optional.empty();
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
		AltInfo ai = nextAltInfo(parts, idx + 1, env);
		if (ai.error.isPresent())
			return new ConsAlt(ai.error);
		return new ConsAlt(after, ai.alt, ai.consumedIdx);
	}

	private ConsAlt extractSeparated(String[] parts, int idx, Env env) {
		int consIdx = idx + 1;
		while (consIdx < parts.length && parts[consIdx].trim().isEmpty())
			consIdx++;
		if (consIdx >= parts.length)
			return new ConsAlt(Optional.of(new Result.Err<>(new InterpretError("missing consequent in if", env.source))));
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

	private AltInfo nextAltInfo(String[] parts, int startIdx, Env env) {
		int altIdx = startIdx;
		while (altIdx < parts.length && parts[altIdx].trim().isEmpty())
			altIdx++;
		if (altIdx >= parts.length)
			return new AltInfo(Optional.of(new Result.Err<>(new InterpretError("missing alternative in if", env.source))));
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
		List<String> parts = new ArrayList<>();
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

	/**
	 * Interpret the given source with the provided input and produce a result
	 * wrapped in a Result (Ok or Err).
	 *
	 * @param source the source code to interpret
	 * @return the result of interpretation wrapped in a Result (Ok or Err)
	 */
	public Result<String, InterpretError> interpret(String source) {
		// return it as the program output. Otherwise return Err with the source.
		if (Objects.isNull(source))
			return new Result.Err<>(new InterpretError("<missing source>", ""));

		String s = source.trim();

		// Support simple sequences with `let` bindings separated by ';'
		if (s.contains(";"))
			return evaluateSequence(s);

		// Otherwise attempt to evaluate as a single expression. Special-case
		// the 'fn <name>... <expr>' pattern: some valid programs declare a
		// function and immediately follow with an expression without a
		// semicolon (e.g., 'fn Wrapper() => { this } Wrapper().x'). If a
		// single-expression parse fails with 'invalid literal', fall back to
		// sequence parsing for sources that start with 'fn'. This avoids
		// interfering with struct inline-literal handling.
		Map<String, String> valEnv = new HashMap<>();
		Map<String, String> typeEnv = new HashMap<>();
		Env env = new Env(valEnv, typeEnv, source);
		Result<String, InterpretError> single = evaluateExpression(s, env);
		if (single instanceof Result.Ok)
			return single;
		if (single instanceof Result.Err && (s.startsWith("fn ") || s.startsWith("fn("))) {
			Result.Err<String, InterpretError> err = (Result.Err<String, InterpretError>) single;
			InterpretError ie = err.error();
			if ("invalid literal".equals(ie.errorReason()))
				return evaluateSequence(s);
		}
		return single;
	}

	// Evaluate a semicolon-separated program supporting `let` declarations.
	private Result<String, InterpretError> evaluateSequence(String source) {
		// Split at top-level semicolons only (ignore semicolons inside braces)
		String[] parts = splitTopLevel(source);
		Map<String, String> valEnv = new HashMap<>();
		Map<String, String> typeEnv = new HashMap<>();
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
				Optional<Result<String, InterpretError>> stmtRes = handleStatement(part, env);
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

	private Optional<Result<String, InterpretError>> handleStatement(String stmt, Env env) {
		String s = stmt.trim();
		// Support block statements of the form `{ ... }` containing one or more
		// inner statements separated by semicolons. Execute inner statements in
		// the same environment. If any inner statement returns a Result (error
		// or an expression result), surface it immediately.
		if (s.startsWith("{")) {
			return evaluateBlock(s, env);
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
	private Optional<Result<String, InterpretError>> handleSimpleStmt(String s, Env env) {
		// Accept a leading struct declaration in statement position. This allows
		// code like `struct T { f : I32 } let x = T { 1 };` where the struct
		// declaration and the let appear in the same semicolon-delimited part.
		if (s.startsWith("struct ")) {
			int nameStart = 7;
			int braceOpen = s.indexOf('{', nameStart);
			if (braceOpen <= 0)
				return Optional.of(new Result.Err<>(new InterpretError("invalid struct declaration", env.source)));
			int braceClose = findMatchingBrace(s, braceOpen);
			if (braceClose < 0)
				return Optional.of(new Result.Err<>(new InterpretError("unterminated struct declaration", env.source)));
			String structName = s.substring(nameStart, braceOpen).trim();
			String fieldsBlock = s.substring(braceOpen + 1, braceClose).trim();
			Optional<Result<String, InterpretError>> declErr = declareStruct(structName, fieldsBlock, env);
			if (declErr.isPresent())
				return declErr;
			String rest = s.substring(braceClose + 1).trim();
			if (rest.isEmpty())
				return Optional.empty();
			// Process the trailing text as another simple statement (e.g., a let)
			return handleSimpleStmt(rest, env);
		}
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
		Optional<LetDeclaration> parsed = parseLetDeclaration(s);
		if (parsed.isEmpty())
			return Optional.of(new Result.Err<>(new InterpretError("invalid let declaration", env.source)));
		LetDeclaration d = parsed.get();
		return handleLetDeclaration(d, env);
	}

	// Helper to process let declarations (extracted to reduce complexity)
	private Optional<Result<String, InterpretError>> handleLetDeclaration(LetDeclaration d, Env env) {
		// If there's no RHS (declaration only), record the annotation and mark as
		// mutable
		if (d.rhs.isEmpty()) {
			if (!d.annotatedSuffix.isEmpty()) {
				Optional<Result<String, InterpretError>> wErr = checkAnnotatedSuffix(d.annotatedSuffix, "0", env);
				if (wErr.isPresent())
					return wErr;
				env.typeEnv.put(d.name, d.annotatedSuffix);
			}
			// Declarations without initializer are implicitly mutable to allow later
			// assignment
			env.mutEnv.put(d.name, Boolean.TRUE);
			// Do not place a value in valEnv yet; assignment will set it.
			return Optional.empty();
		}
		Result<String, InterpretError> rhsRes = getRhsValue(d.rhs, env);
		if (rhsRes instanceof Result.Err)
			return Optional.of(rhsRes);
		String value = ((Result.Ok<String, InterpretError>) rhsRes).value();
		Optional<Result<String, InterpretError>> annRes = recordAnn(d, value, env);
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
		// If the block is tracking local declarations, record this name so it can
		// be removed when the block ends. This keeps block-local lets from
		// polluting the outer environment while allowing assignments to update
		// existing outer variables.
		if (env.localDecls.isPresent()) {
			env.localDecls.get().add(d.name);
		}
		return Optional.empty();
	}

	private Result<String, InterpretError> getRhsValue(String rhs, Env env) {
		// Evaluate RHS in a child environment so the evaluated expression
		// can see functions, struct definitions, and mutability info while
		// keeping value/type maps separate for safety.
		Env tmp = makeChildEnv(env);
		return evaluateExpression(rhs, tmp);
	}

	// Validate annotated suffix for a let declaration and record the annotation in
	// typeEnv
	private Optional<Result<String, InterpretError>> recordAnn(LetDeclaration d, String value, Env env) {
		if (d.annotatedSuffix.isEmpty())
			return Optional.empty();
		Optional<Result<String, InterpretError>> check = chkRhs(d.annotatedSuffix, d.rhs, env.source);
		if (check.isPresent())
			return check;
		Optional<Result<String, InterpretError>> v = checkAnnotatedSuffix(d.annotatedSuffix, value, env);
		if (v.isPresent())
			return v;
		env.typeEnv.put(d.name, d.annotatedSuffix);
		return Optional.empty();
	}

	private Optional<Result<String, InterpretError>> chkRhs(String annSuffix, String rhs, String source) {
		ParseResult rhsPr = parseSignAndDigits(rhs.trim());
		if (rhsPr.valid && !rhsPr.suffix.isEmpty()) {
			String ann = annSuffix.toUpperCase();
			String rhsSuf = rhsPr.suffix.toUpperCase();
			if (!ann.equals(rhsSuf))
				return Optional.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", source)));
		}
		return Optional.empty();
	}

	// Helper to handle assignment statements to reduce complexity
	private Optional<Result<String, InterpretError>> handleAssignment(String stmt, int eqIdx, Env env) {
		String lhs = stmt.substring(0, eqIdx).trim();
		String rhs = stmt.substring(eqIdx + 1).trim();
		AssignPrep prep = prepAssignOps(lhs, rhs, env);
		if (prep.error.isPresent() || prep.evaluatedRhs.isEmpty())
			return prep.error;
		String evaluated = prep.evaluatedRhs.get();
		// Enforce assignment value compatibility against annotated/inferred type
		Optional<Result<String, InterpretError>> valCheck = vAssignValue(lhs, evaluated, env);
		if (valCheck.isPresent())
			return valCheck;

		// Handle deref-assignment marker produced by prepAssignOps: DREF_ASSIGN_PREFIX
		// + target + ":" + value
		if (evaluated.startsWith(DREF_ASSIGN_PREFIX)) {
			String rest = evaluated.substring(DREF_ASSIGN_PREFIX.length());
			int colon = rest.indexOf(':');
			if (colon <= 0)
				return Optional.of(new Result.Err<>(new InterpretError("invalid deref assignment", env.source)));
			String target = rest.substring(0, colon);
			String value = rest.substring(colon + 1);
			// Validate assignment value against the target's annotated/inferred type
			Optional<Result<String, InterpretError>> valCheckTarget = vAssignValue(target, value, env);
			if (valCheckTarget.isPresent())
				return valCheckTarget;
			env.valEnv.put(target, value);
			return Optional.empty();
		}

		env.valEnv.put(lhs, evaluated);
		return Optional.empty();
	}

	// Prepare and validate lhs/rhs for assignment: validate identifier, mutability,
	// check suffix compatibility, and evaluate the RHS. Returns AssignPrep where
	// 'error' is present on failure; otherwise 'evaluatedRhs' holds the RHS value.
	private AssignPrep prepAssignOps(String lhs, String rhs, Env env) {
		// support indexed-assignment of the form '<ident>[<expr>] = <expr>' and
		// deref-assignment of the form '*<ident> = <expr>' where lhs may
		// be a dereference of a reference value stored in a variable
		boolean isDeref = false;
		String derefTarget = "";
		boolean isIndexed = false;
		String indexBase = "";
		String indexExpr = "";
		// Detect indexed LHS like 'x[0]'
		if (lhs.contains("[")) {
			java.util.Optional<IndexedLhs> parsed = parseIndexedLhs(lhs);
			if (parsed.isPresent()) {
				isIndexed = true;
				indexBase = parsed.get().base;
				indexExpr = parsed.get().indexExpr;
			}
		}
		if (lhs.startsWith("*")) {
			isDeref = true;
			String inner = lhs.substring(1).trim();
			if (!isSimpleIdentifier(inner))
				return new AssignPrep(new Result.Err<>(new InterpretError("invalid assignment lhs", env.source)));
			derefTarget = inner;
		} else if (!isIndexed) {
			if (!isSimpleIdentifier(lhs))
				return new AssignPrep(new Result.Err<>(new InterpretError("invalid assignment lhs", env.source)));
		}
		// Determine the actual variable name that will receive the assignment.
		// If LHS is a dereference like '*y', resolve the variable y's value and
		// ensure it is a reference; for a mutable reference allow assignment to
		// the referenced target.
		// Resolve the actual target variable name for the assignment (handles
		// deref and indexed cases). This extraction reduces cyclomatic complexity
		// in this method.
		LhsResolveReq lr = new LhsResolveReq(isDeref ? derefTarget : "", isIndexed ? indexBase : "", lhs);
		Result<String, InterpretError> lhsRes = resolveLhsTarget(lr, env);
		if (lhsRes instanceof Result.Err)
			return new AssignPrep(lhsRes);
		String lhsName = ((Result.Ok<String, InterpretError>) lhsRes).value();

		// Validate RHS suffix vs annotated/inferred type (if any) before evaluating
		Optional<Result<String, InterpretError>> suffixCheck = vRhsSuffix(lhsName, rhs, env);
		if (suffixCheck.isPresent())
			return new AssignPrep(suffixCheck.get());

		// If this is an indexed assignment, delegate to helper which will
		// evaluate the RHS itself to keep parameter counts low and simplify flow.
		if (isIndexed)
			return handleIdxAssign(new IndexedAssignReq(lhsName, indexExpr, rhs), env);

		Result<String, InterpretError> rhsVal = evaluateExpression(rhs, env);
		if (rhsVal instanceof Result.Err)
			return new AssignPrep(rhsVal);
		String value = ((Result.Ok<String, InterpretError>) rhsVal).value();
		// If this is a deref-assignment, return marker so caller writes to resolved
		// target
		if (isDeref) {
			return new AssignPrep(DREF_ASSIGN_PREFIX + lhsName + ":" + value);
		}
		return new AssignPrep(value);
	}

	private DerefResolve resolveDerefTarget(String holder, Env env) {
		if (!env.valEnv.containsKey(holder) && !env.typeEnv.containsKey(holder))
			return new DerefResolve(
					Optional.of(new Result.Err<>(new InterpretError("unknown identifier in assignment", env.source))));
		String refVal = env.valEnv.getOrDefault(holder, "");
		if (refVal.startsWith(REFMUT_PREFIX)) {
			String target = refVal.substring(REFMUT_PREFIX.length());
			Boolean isMutTarget = env.mutEnv.getOrDefault(target, Boolean.FALSE);
			if (!isMutTarget)
				return new DerefResolve(
						Optional.of(new Result.Err<>(new InterpretError("assignment to immutable variable", env.source))));
			return new DerefResolve(target);
		}
		if (refVal.startsWith(REF_PREFIX)) {
			return new DerefResolve(
					Optional.of(new Result.Err<>(new InterpretError("assignment to immutable variable", env.source))));
		}
		return new DerefResolve(Optional.of(new Result.Err<>(new InterpretError("invalid dereference", env.source))));
	}

	// Handle compound assignment of the form '<ident> += <expr>'
	private Optional<Result<String, InterpretError>> handleCompAssign(String stmt, int plusEqIdx, Env env) {
		String lhs = stmt.substring(0, plusEqIdx).trim();
		String rhs = stmt.substring(plusEqIdx + 2).trim();
		// Reuse common preparation logic for assignments
		AssignPrep ap = prepAssignOps(lhs, rhs, env);
		if (ap.error.isPresent() || ap.evaluatedRhs.isEmpty())
			return ap.error;
		// ap.evaluatedRhs is the evaluated RHS value
		String rhsValue = ap.evaluatedRhs.get();

		// Fetch current lhs value; it must exist in valEnv for compound assignment
		if (!env.valEnv.containsKey(lhs))
			return Optional.of(new Result.Err<>(new InterpretError("read of uninitialized variable", env.source)));
		String lhsValue = env.valEnv.get(lhs);

		try {
			BigInteger a = new BigInteger(lhsValue);
			BigInteger b = new BigInteger(rhsValue);
			BigInteger sum = a.add(b);
			String sumStr = sum.toString();

			// Validate resulting assignment against annotated/inferred type
			Optional<Result<String, InterpretError>> valCheck = vAssignValue(lhs, sumStr, env);
			if (valCheck.isPresent())
				return valCheck;

			env.valEnv.put(lhs, sumStr);
			return Optional.empty();
		} catch (NumberFormatException ex) {
			return Optional.of(new Result.Err<>(new InterpretError("invalid integer in compound assignment", env.source)));
		}
	}

	// Validate RHS textual suffix against the variable's annotated/inferred type
	private Optional<Result<String, InterpretError>> vRhsSuffix(String lhs, String rhs, Env env) {
		ParseResult rhsPr = parseSignAndDigits(rhs.trim());
		if (!env.typeEnv.containsKey(lhs))
			return Optional.empty();
		String ann = env.typeEnv.get(lhs);
		if (ann.equalsIgnoreCase("Bool")) {
			// If RHS is a typed literal (e.g., 3U8) that's incompatible with Bool
			if (rhsPr.valid && !rhsPr.suffix.isEmpty())
				return Optional.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", env.source)));
			return Optional.empty();
		}
		if (rhsPr.valid && !rhsPr.suffix.isEmpty()) {
			String rhsSuf = rhsPr.suffix.toUpperCase();
			if (!ann.toUpperCase().equals(rhsSuf))
				return Optional.of(new Result.Err<>(new InterpretError("mismatched typed literal in assignment", env.source)));
		}
		return Optional.empty();
	}

	// Validate the evaluated RHS value against the variable's annotated/inferred
	// type
	private Optional<Result<String, InterpretError>> vAssignValue(String lhs, String value, Env env) {
		if (!env.typeEnv.containsKey(lhs))
			return Optional.empty();
		String ann = env.typeEnv.get(lhs);
		if (ann.equalsIgnoreCase("Bool")) {
			if (!"true".equals(value) && !"false".equals(value))
				return Optional.of(new Result.Err<>(new InterpretError("mismatched assignment to Bool variable", env.source)));
		} else {
			// For numeric typed annotations, ensure the evaluated RHS fits the annotated
			// type
			Optional<Result<String, InterpretError>> chk = checkAnnotatedSuffix(ann, value, env);
			if (chk.isPresent())
				return chk;
		}
		return Optional.empty();
	}

	// Parse a let declaration of the form: let <ident> (':' <suffix>)? '=' <expr>
	private Optional<LetDeclaration> parseLetDeclaration(String s) {
		if (!s.startsWith("let "))
			return Optional.empty();
		int len = s.length();
		int idx = skipWs(s, 4, len); // after 'let '
		boolean mutable = false;
		// support optional 'mut' keyword: 'let mut x = ...'
		if (s.startsWith("mut ", idx)) {
			mutable = true;
			idx = skipWs(s, idx + 4, len);
		}
		Optional<ParseId> pidOpt = parseId(s, idx, len);
		if (pidOpt.isEmpty())
			return Optional.empty();
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
		return Optional.of(d);
	}

	// helper to skip whitespace from index start up to len
	private int skipWs(String s, int start, int len) {
		int i = start;
		while (i < len && Character.isWhitespace(s.charAt(i)))
			i++;
		return i;
	}

	private Optional<ParseId> parseId(String s, int idx, int len) {
		if (idx >= len || !Character.isJavaIdentifierStart(s.charAt(idx)))
			return Optional.empty();
		int start = idx;
		do
			idx++;
		while (idx < len && Character.isJavaIdentifierPart(s.charAt(idx)));
		return Optional.of(new ParseId(s.substring(start, idx), idx));
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

	private Optional<Result<String, InterpretError>> checkAnnotatedSuffix(String annotatedSuffix, String value, Env env) {
		// Special-case Bool annotation which does not follow the <Letter><digits>
		// pattern used by integer typed suffixes (e.g. U8, I32).
		if (annotatedSuffix.equalsIgnoreCase("Bool")) {
			// For Bool, the value must be a boolean literal string "true" or "false"
			if ("true".equals(value) || "false".equals(value))
				return Optional.empty();
			return err("invalid boolean value for Bool annotation", env.source);
		}
		if (isInvalidSuffix(annotatedSuffix))
			return Optional.of(new Result.Err<>(new InterpretError("invalid type suffix", env.source)));
		char kind = Character.toUpperCase(annotatedSuffix.charAt(0));
		int[] widthHolder = new int[1];
		Optional<Result<String, InterpretError>> wErr = parseWidth(annotatedSuffix, widthHolder, env.source);
		if (wErr.isPresent())
			return wErr;
		int width = widthHolder[0];
		return checkFits(new TypeSpec(kind, width), value, env);
	}

	private Optional<Result<String, InterpretError>> parseWidth(String annotatedSuffix, int[] outWidth, String source) {
		try {
			outWidth[0] = Integer.parseInt(annotatedSuffix.substring(1));
		} catch (NumberFormatException ex) {
			return Optional.of(new Result.Err<>(new InterpretError("invalid type width", source)));
		}
		return Optional.empty();
	}

	private Optional<Result<String, InterpretError>> checkFits(TypeSpec ts, String value, Env env) {
		try {
			BigInteger val = new BigInteger(value);
			if (ts.kind == 'U') {
				if (val.signum() < 0 || !fitsUnsigned(val, ts.width))
					return err("value does not fit annotated type", env.source);
				return Optional.empty();
			} else if (ts.kind == 'I') {
				if (!fitsSigned(val, ts.width))
					return err("value does not fit annotated type", env.source);
				return Optional.empty();
			}
			return err("unknown type kind", env.source);
		} catch (NumberFormatException ex) {
			return err("invalid integer value", env.source);
		}
	}

	private Result<String, InterpretError> evaluateExpression(String expr, Env env) {
		String s = expr.trim();
		Optional<Result<String, InterpretError>> structRes = tryEvalStruct(s, env);
		if (structRes.isPresent())
			return structRes.get();
		Optional<Result<String, InterpretError>> memberRes = tryEvalMemberAccess(s, env);
		if (memberRes.isPresent())
			return memberRes.get();
		// Try handling prefix operations: &mut, &, and * (deref)
		Optional<Result<String, InterpretError>> pref = tryEvalPrefix(s, env);
		if (pref.isPresent())
			return pref.get();
		// Try a standalone struct literal of the form `Type { ... }` when a type
		// named in env.structEnv is present. This covers cases where the struct
		// was declared earlier and an instance is created later.
		Optional<Result<String, InterpretError>> standaloneStruct = tryEvalStructLitSta(s, env);
		if (standaloneStruct.isPresent())
			return standaloneStruct.get();
		// Block expression handling delegated to helper to keep this method small.
		if (s.startsWith("{") && s.endsWith("}"))
			return evalBlockExpr(s, env);
		// 'this' expression captures block-local lets inside a block expression
		if (s.equals("this"))
			return evalThisExpr(env, s);
		// Boolean literals
		if (s.equals("true") || s.equals("false"))
			return new Result.Ok<>(s);

		Optional<Result<String, InterpretError>> p1 = tryEvalPrimary1(s, env);
		if (p1.isPresent())
			return p1.get();
		Optional<Result<String, InterpretError>> p2 = tryEvalPrimary2(s, env);
		if (p2.isPresent())
			return p2.get();
		return new Result.Err<>(new InterpretError("invalid literal", expr));
	}

	// Try member access `<expr>.field` and return Optional.empty() if not
	// applicable
	private Optional<Result<String, InterpretError>> tryEvalMemberAccess(String s, Env env) {
		int dotIdx = s.lastIndexOf('.');
		if (dotIdx <= 0 || dotIdx >= s.length() - 1)
			return Optional.empty();
		String base = s.substring(0, dotIdx).trim();
		String field = s.substring(dotIdx + 1).trim();
		if (!isSimpleIdentifier(field))
			return Optional.empty();
		Result<String, InterpretError> baseRes = evaluateExpression(base, env);
		if (baseRes instanceof Result.Err)
			return Optional.of(baseRes);
		String baseVal = ((Result.Ok<String, InterpretError>) baseRes).value();
		if (!baseVal.startsWith(STR_PREFIX))
			return Optional.of(new Result.Err<>(new InterpretError("invalid struct field access", s)));
		String payload = baseVal.substring(STR_PREFIX.length());
		int sep = payload.indexOf('|');
		String fieldsPart = sep >= 0 ? payload.substring(sep + 1) : "";
		if (fieldsPart.isEmpty())
			return Optional.of(new Result.Err<>(new InterpretError("unknown field", s)));
		for (String p : fieldsPart.split("\\|")) {
			int eq = p.indexOf('=');
			if (eq <= 0)
				continue;
			String fname = p.substring(0, eq);
			String fval = p.substring(eq + 1);
			if (fname.equals(field))
				return Optional.of(new Result.Ok<>(fval));
		}
		return Optional.of(new Result.Err<>(new InterpretError("unknown field", s)));
	}

	// Primary evaluation part 1: block, boolean, array/index, function call,
	// variable
	private Optional<Result<String, InterpretError>> tryEvalPrimary1(String s, Env env) {
		// Block expression
		if (s.startsWith("{") && s.endsWith("}"))
			return Optional.of(evalBlockExpr(s, env));
		// 'this' expression
		if (s.equals("this"))
			return Optional.of(evalThisExpr(env, s));
		// Boolean literals
		if (s.equals("true") || s.equals("false"))
			return Optional.of(new Result.Ok<>(s));
		// array literal / indexing
		Optional<Result<String, InterpretError>> arrIdx = tryEvalArrayOrIndex(s, env);
		if (arrIdx.isPresent())
			return arrIdx;
		// function call
		Optional<Result<String, InterpretError>> fnCall = tryEvalFunctionCall(s, env);
		if (fnCall.isPresent())
			return fnCall;
		// variable reference
		if (isSimpleIdentifier(s)) {
			if (env.valEnv.containsKey(s))
				return Optional.of(new Result.Ok<>(env.valEnv.get(s)));
			return Optional.of(new Result.Err<>(new InterpretError("unknown identifier", s)));
		}
		return Optional.empty();
	}

	// Primary evaluation part 2: comparison, addition, and literals
	private Optional<Result<String, InterpretError>> tryEvalPrimary2(String s, Env env) {
		Optional<Result<String, InterpretError>> cmpRes = tryEvalComp(s, env);
		if (cmpRes.isPresent())
			return cmpRes;
		Optional<Result<String, InterpretError>> addRes = tryEvaluateAddition(s);
		if (addRes.isPresent())
			return addRes;
		ParseResult pr = parseSignAndDigits(s);
		if (!pr.valid)
			return Optional.empty();
		if (pr.suffix.isEmpty())
			return Optional.of(new Result.Ok<>(pr.integerPart));
		return Optional.of(evaluateTypedSuffix(pr, s));
	}

	// Try to evaluate either an array literal like `[1,2]` or an indexing
	// expression
	// like `<expr>[<expr>]`. Returns Optional.empty() if not applicable.
	private Optional<Result<String, InterpretError>> tryEvalArrayOrIndex(String s, Env env) {
		// Try indexing first, then array literal. Extract to smaller helpers for
		// cyclomatic complexity reduction.
		Optional<Result<String, InterpretError>> idx = tryEvalIndexing(s, env);
		if (idx.isPresent())
			return idx;
		return tryEvalArrayLiteral(s, env);
	}

	// Extracted helper: evaluate indexing expression of the form
	// <baseExpr>[<idxExpr>] where the trailing ']' matches an earlier '['.
	private Optional<Result<String, InterpretError>> tryEvalIndexing(String s, Env env) {
		int lastBracket = s.lastIndexOf(']');
		if (lastBracket != s.length() - 1)
			return Optional.empty();
		// Find the matching '[' for the trailing ']' by scanning backwards and
		// counting nested brackets. This ensures nested array literals like
		// "[[1]]" are not mis-detected as an indexing expression.
		int openBracket = findOpenBracket(s, lastBracket);
		if (!(openBracket > 0))
			return Optional.empty();
		String baseExpr = s.substring(0, openBracket).trim();
		String idxExpr = s.substring(openBracket + 1, lastBracket).trim();
		Result<String, InterpretError> baseRes = evaluateExpression(baseExpr, env);
		if (baseRes instanceof Result.Err)
			return Optional.of(baseRes);
		String baseVal = ((Result.Ok<String, InterpretError>) baseRes).value();
		if (!baseVal.startsWith(ARR_PREFIX))
			return Optional.of(new Result.Err<>(new InterpretError("invalid array indexing", s)));
		String elemsJoined = baseVal.substring(ARR_PREFIX.length());
		List<String> elems = new ArrayList<>();
		if (!elemsJoined.isEmpty()) {
			Collections.addAll(elems, elemsJoined.split("\\|"));
		}
		Result<String, InterpretError> idxRes = evaluateExpression(idxExpr, env);
		if (idxRes instanceof Result.Err)
			return Optional.of(idxRes);
		String idxVal = ((Result.Ok<String, InterpretError>) idxRes).value();
		try {
			int idx = Integer.parseInt(idxVal);
			if (idx < 0 || idx >= elems.size())
				return Optional.of(new Result.Err<>(new InterpretError("index out of bounds", s)));
			return Optional.of(new Result.Ok<>(elems.get(idx)));
		} catch (NumberFormatException ex) {
			return Optional.of(new Result.Err<>(new InterpretError("invalid index", s)));
		}
	}

	// Extracted helper: evaluate array literal like [a,b,c] returning an ARR_PREFIX
	// string
	private Optional<Result<String, InterpretError>> tryEvalArrayLiteral(String s, Env env) {
		if (!(s.startsWith("[") && s.endsWith("]")))
			return Optional.empty();
		String inner = s.substring(1, s.length() - 1).trim();
		List<String> elems = new ArrayList<>();
		if (!inner.isEmpty()) {
			for (String p : inner.split(",")) {
				elems.add(p.trim());
			}
		}
		List<String> evaluated = new ArrayList<>();
		for (String e : elems) {
			Result<String, InterpretError> r = evaluateExpression(e, env);
			if (r instanceof Result.Err)
				return Optional.of(r);
			evaluated.add(((Result.Ok<String, InterpretError>) r).value());
		}
		String join = String.join("|", evaluated);
		return Optional.of(new Result.Ok<>(ARR_PREFIX + join));
	}

	// Strip leading transparent unary prefixes '*' and '&' from the expression
	// and return the trimmed remainder. Extracted into a helper to keep
	// evaluateExpression under the cyclomatic complexity threshold.

	// Try to evaluate expressions that start with reference/dereference
	// prefixes. Returns Optional.empty() if the expression does not start with
	// a handled prefix.
	private Optional<Result<String, InterpretError>> tryEvalPrefix(String s, Env env) {
		if (s.isEmpty())
			return Optional.empty();
		// &mut <ident>
		if (s.startsWith("&mut")) {
			String rest = s.substring(4).trim();
			if (isSimpleIdentifier(rest)) {
				return Optional.of(new Result.Ok<>(REFMUT_PREFIX + rest));
			}
			return Optional.of(new Result.Err<>(new InterpretError("invalid reference", s)));
		}
		// &<ident>
		if (s.startsWith("&")) {
			String rest = s.substring(1).trim();
			if (isSimpleIdentifier(rest)) {
				return Optional.of(new Result.Ok<>(REF_PREFIX + rest));
			}
			return Optional.of(new Result.Err<>(new InterpretError("invalid reference", s)));
		}
		// *<expr> (dereference)
		if (s.startsWith("*")) {
			String inner = s.substring(1).trim();
			Result<String, InterpretError> innerRes = evaluateExpression(inner, env);
			if (innerRes instanceof Result.Err)
				return Optional.of(innerRes);
			String val = ((Result.Ok<String, InterpretError>) innerRes).value();
			if (val.startsWith(REF_PREFIX)) {
				return derefRefHolder(val, env, REF_PREFIX);
			}
			if (val.startsWith(REFMUT_PREFIX)) {
				return derefRefHolder(val, env, REFMUT_PREFIX);
			}
			return Optional.of(new Result.Err<>(new InterpretError("invalid dereference", s)));
		}
		return Optional.empty();
	}

	// Helper to dereference a reference-holder value like "@REF:target" and
	// return the referenced variable's value or an Err if unknown.
	private Optional<Result<String, InterpretError>> derefRefHolder(String holderVal, Env env, String prefix) {
		String target = holderVal.substring(prefix.length());
		if (!env.valEnv.containsKey(target))
			return Optional.of(new Result.Err<>(new InterpretError("unknown identifier", env.source)));
		return Optional.of(new Result.Ok<>(env.valEnv.get(target)));
	}

	// Try to evaluate a simple comparison of the form "<expr> < <expr>" where
	// each side is an expression that evaluates to an integer. Returns an
	// Ok("true"/"false")
	// or an empty Optional if not applicable.
	private Optional<Result<String, InterpretError>> tryEvalComp(String s, Env env) {
		int ltIdx = s.indexOf('<');
		if (ltIdx <= 0)
			return Optional.empty();
		String leftRaw = s.substring(0, ltIdx).trim();
		String rightRaw = s.substring(ltIdx + 1).trim();

		// Evaluate both sides as expressions (they may be additions or identifiers)
		Result<String, InterpretError> leftRes = evaluateExpression(leftRaw, env);
		if (leftRes instanceof Result.Err)
			return Optional.of(leftRes);
		Result<String, InterpretError> rightRes = evaluateExpression(rightRaw, env);
		if (rightRes instanceof Result.Err)
			return Optional.of(rightRes);
		String leftVal = ((Result.Ok<String, InterpretError>) leftRes).value();
		String rightVal = ((Result.Ok<String, InterpretError>) rightRes).value();
		try {
			BigInteger a = new BigInteger(leftVal);
			BigInteger b = new BigInteger(rightVal);
			return Optional.of(new Result.Ok<>(a.compareTo(b) < 0 ? "true" : "false"));
		} catch (NumberFormatException ex) {
			return Optional.of(new Result.Err<>(new InterpretError("invalid integer in comparison", s)));
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

	private boolean isInvalidSuffix(String suffix) {
		if (suffix.length() < 2)
			return true;
		if (!Character.isLetter(suffix.charAt(0)))
			return true;
		String widthStr = suffix.substring(1);
		for (int j = 0; j < widthStr.length(); j++) {
			if (!Character.isDigit(widthStr.charAt(j)))
				return true;
		}
		return false;
	}

	private boolean fitsUnsigned(BigInteger val, int width) {
		BigInteger max = BigInteger.ONE.shiftLeft(width).subtract(BigInteger.ONE);
		return val.compareTo(max) <= 0;
	}

	private boolean fitsSigned(BigInteger val, int width) {
		BigInteger min = BigInteger.ONE.shiftLeft(width - 1).negate();
		BigInteger max = BigInteger.ONE.shiftLeft(width - 1).subtract(BigInteger.ONE);
		return val.compareTo(min) >= 0 && val.compareTo(max) <= 0;
	}

	/**
	 * Try to evaluate a simple addition expression of the form "<int> + <int>".
	 * Returns an Ok Result on success or an empty Optional if not applicable.
	 */
	private Optional<Result<String, InterpretError>> tryEvaluateAddition(String s) {
		int plusIdx = s.indexOf('+');
		if (plusIdx <= 0)
			return Optional.empty();
		String leftRaw = s.substring(0, plusIdx).trim();
		String rightRaw = s.substring(plusIdx + 1).trim();

		// Parse each side for optional sign/digits and optional suffix
		ParseResult leftPr = parseSignAndDigits(leftRaw);
		ParseResult rightPr = parseSignAndDigits(rightRaw);
		if (!leftPr.valid || !rightPr.valid)
			return Optional.empty();

		// If either side has a suffix, we allow three cases:
		// 1) both have suffixes and they match exactly (same kind and width)
		// 2) one has a suffix and the other doesn't: the untyped side is accepted
		// only if its value fits into the typed width/kind of the typed side
		// 3) mixed kinds/widths (e.g., U vs I or different widths) are invalid
		// Validate typed/untyped suffixes; if invalid, return Err wrapped in Optional
		Optional<Result<String, InterpretError>> suffixCheck = checkTypedOperands(leftPr, rightPr, s);
		if (suffixCheck.isPresent())
			return suffixCheck;

		try {
			BigInteger a = new BigInteger(leftPr.integerPart);
			BigInteger b = new BigInteger(rightPr.integerPart);
			return Optional.of(new Result.Ok<>(a.add(b).toString()));
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}
	}

	// Helper to validate typed/untyped operands for addition.
	private Optional<Result<String, InterpretError>> checkTypedOperands(ParseResult leftPr,
			ParseResult rightPr,
			String source) {
		boolean leftHas = !leftPr.suffix.isEmpty();
		boolean rightHas = !rightPr.suffix.isEmpty();
		if (!leftHas && !rightHas)
			return Optional.empty();
		if (leftHas && rightHas)
			return validateBothTyped(leftPr, rightPr, source);
		return validateOneTyped(leftPr, rightPr, source);
	}

	private Optional<Result<String, InterpretError>> validateBothTyped(ParseResult leftPr,
			ParseResult rightPr,
			String source) {
		if (isInvalidSuffix(leftPr.suffix) || isInvalidSuffix(rightPr.suffix))
			return Optional.of(new Result.Err<>(new InterpretError("invalid typed suffix on operand", source)));
		String l = leftPr.suffix.toUpperCase();
		String r = rightPr.suffix.toUpperCase();
		if (!l.equals(r))
			return Optional.of(new Result.Err<>(new InterpretError("mismatched typed operand suffixes", source)));
		return Optional.empty();
	}

	private Optional<Result<String, InterpretError>> validateOneTyped(ParseResult leftPr,
			ParseResult rightPr,
			String source) {
		boolean leftHas = !leftPr.suffix.isEmpty();
		String typedSuffix = leftHas ? leftPr.suffix : rightPr.suffix;
		String untypedInteger = leftHas ? rightPr.integerPart : leftPr.integerPart;
		return vTyped(typedSuffix, untypedInteger, source);
	}

	private Optional<Result<String, InterpretError>> vTyped(String typedSuffix, String untypedInteger, String source) {
		if (isInvalidSuffix(typedSuffix))
			return Optional.of(new Result.Err<>(new InterpretError("invalid typed suffix", source)));
		char kind = Character.toUpperCase(typedSuffix.charAt(0));
		int width;
		try {
			width = Integer.parseInt(typedSuffix.substring(1));
		} catch (NumberFormatException ex) {
			return Optional.of(new Result.Err<>(new InterpretError("invalid type width", source)));
		}
		if (!(width == 8 || width == 16 || width == 32 || width == 64))
			return Optional.of(new Result.Err<>(new InterpretError("unsupported type width", source)));
		BigInteger untypedVal;
		try {
			untypedVal = parseBigInteger(untypedInteger);
		} catch (NumberFormatException ex) {
			return Optional.of(new Result.Err<>(new InterpretError("invalid integer in operand", source)));
		}
		if (kind == 'U') {
			if (untypedVal.signum() < 0 || !fitsUnsigned(untypedVal, width))
				return Optional.of(new Result.Err<>(new InterpretError("untyped value does not fit unsigned type", source)));
			return Optional.empty();
		}
		if (kind == 'I') {
			if (!fitsSigned(untypedVal, width))
				return Optional.of(new Result.Err<>(new InterpretError("untyped value does not fit signed type", source)));
			return Optional.empty();
		}
		return Optional.of(new Result.Err<>(new InterpretError("invalid typed operand combination", source)));
	}

	private BigInteger parseBigInteger(String s) {
		return new BigInteger(s);
	}

	private Result<String, InterpretError> evaluateTypedSuffix(ParseResult pr, String source) {
		if (isInvalidSuffix(pr.suffix))
			return new Result.Err<>(new InterpretError("invalid typed literal suffix", source));

		char kind = Character.toUpperCase(pr.suffix.charAt(0));
		String widthStr = pr.suffix.substring(1);
		try {
			int width = Integer.parseInt(widthStr);
			if (!(width == 8 || width == 16 || width == 32 || width == 64))
				return new Result.Err<>(new InterpretError("unsupported type width", source));

			BigInteger val = new BigInteger(pr.integerPart);
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
		return trimmed.indexOf(" else ");
	}

	// Try to parse an inline struct declaration/literal and return its Result if
	// applicable. This keeps evaluateExpression under the cyclomatic limit.
	private Optional<Result<String, InterpretError>> tryEvalStruct(String s, Env env) {
		if (!s.startsWith("struct "))
			return Optional.empty();
		int nameStart = 7;
		int braceOpen = s.indexOf('{', nameStart);
		if (braceOpen <= 0)
			return Optional.empty();
		String structName = s.substring(nameStart, braceOpen).trim();
		int braceClose = findMatchingBrace(s, braceOpen);
		if (braceClose < 0)
			return Optional.of(new Result.Err<>(new InterpretError("unterminated struct declaration", s)));
		String fieldsBlock = s.substring(braceOpen + 1, braceClose).trim();
		Optional<Result<String, InterpretError>> declErr = declareStruct(structName, fieldsBlock, env);
		if (declErr.isPresent())
			return declErr;
		String rest = s.substring(braceClose + 1);
		String restTrim = rest.trim();
		if (restTrim.isEmpty())
			return Optional.of(new Result.Ok<>(""));
		if (looksLikeLitAfter(rest))
			return tryEvalStructLiteral(restTrim, env);
		// If there's trailing text that is not a literal, attempt to parse it as
		// a simple statement (e.g., another struct declaration or a let).
		// This ensures duplicate struct declarations that appear adjacent
		// without semicolons are still detected and reported.
		Optional<Result<String, InterpretError>> trailingRes = handleSimpleStmt(restTrim, env);
		if (trailingRes.isPresent())
			return trailingRes;
		return Optional.of(new Result.Ok<>(""));
	}

	// (parseStructFields removed; use parseStructFieldSpecs instead)

	// Parse struct fields into a list of FieldSpec (name + optional type).
	private List<FieldSpec> parseStructFieldSpec(String fieldsBlock) {
		List<FieldSpec> specs = new ArrayList<>();
		if (Objects.isNull(fieldsBlock) || fieldsBlock.trim().isEmpty())
			return specs;
		for (String part : fieldsBlock.split(";|,")) {
			String p = part.trim();
			if (p.isEmpty())
				continue;
			int colon = p.indexOf(':');
			String fname = colon > 0 ? p.substring(0, colon).trim() : p;
			String ftype = colon > 0 ? p.substring(colon + 1).trim() : "";
			specs.add(new FieldSpec(fname, ftype));
		}
		return specs;
	}

	// Parse fieldsBlock and return a map with two lists: 'names' and 'types'.
	private java.util.Map<String, java.util.List<String>> parseFieldNamesTypes(String fieldsBlock) {
		List<FieldSpec> specs = parseStructFieldSpec(fieldsBlock);
		java.util.List<String> names = new ArrayList<>();
		java.util.List<String> types = new ArrayList<>();
		for (FieldSpec fs : specs) {
			names.add(fs.name());
			types.add(fs.type());
		}
		java.util.Map<String, java.util.List<String>> m = new java.util.HashMap<>();
		m.put("names", names);
		m.put("types", types);
		return m;
	}

	// Centralized helper for declaring a struct: parse fields, check duplicates,
	// and record names/types in the environment.
	private Optional<Result<String, InterpretError>> declareStruct(String structName, String fieldsBlock, Env env) {
		String context = env.source;
		java.util.Map<String, java.util.List<String>> nameAndTypes = parseFieldNamesTypes(fieldsBlock);
		List<String> fieldNames = nameAndTypes.get("names");
		List<String> fieldTypes = nameAndTypes.get("types");
		// Validate duplicate field names within the struct
		Optional<Result<String, InterpretError>> dupErr = checkDuplicateFields(fieldNames, context);
		if (dupErr.isPresent())
			return dupErr;
		// Reject duplicate struct declarations: a struct name must be unique.
		if (env.structEnv.containsKey(structName))
			return Optional.of(new Result.Err<>(new InterpretError("duplicate struct declaration", context)));
		env.structEnv.put(structName, fieldNames);
		env.structFieldTypes.put(structName, fieldTypes);
		return Optional.empty();
	}

	// Return an Optional containing an Err Result if duplicate field names are
	// present in the provided list. The caller can return the Result when
	// present to surface the error to the interpreter user.
	private Optional<Result<String, InterpretError>> checkDuplicateFields(List<String> fields, String context) {
		Set<String> seen = new HashSet<>();
		for (String f : fields) {
			if (seen.contains(f))
				return Optional.of(new Result.Err<>(new InterpretError("duplicate struct field", context)));
			seen.add(f);
		}
		return Optional.empty();
	}

	// Heuristic: does the trailing text after a struct declaration look like a
	// literal, i.e., an identifier optionally followed by whitespace and a '{'?
	private boolean looksLikeLitAfter(String rest) {
		if (Objects.isNull(rest) || rest.isEmpty())
			return false;
		int i = 0;
		while (i < rest.length() && Character.isWhitespace(rest.charAt(i)))
			i++;
		int start = i;
		while (i < rest.length() && Character.isJavaIdentifierPart(rest.charAt(i)))
			i++;
		if (start < i) {
			int j = i;
			while (j < rest.length() && Character.isWhitespace(rest.charAt(j)))
				j++;
			return j < rest.length() && rest.charAt(j) == '{';
		}
		return false;
	}

	// Try to evaluate a standalone struct literal like `Type { ... }` where the
	// type has been declared earlier. Returns Optional.empty() if not applicable.
	private Optional<Result<String, InterpretError>> tryEvalStructLitSta(String s, Env env) {
		// look for an identifier followed by a '{'
		int i = 0;
		int len = s.length();
		while (i < len && Character.isWhitespace(s.charAt(i)))
			i++;
		int start = i;
		while (i < len && Character.isJavaIdentifierPart(s.charAt(i)))
			i++;
		if (start == i)
			return Optional.empty();
		String typeName = s.substring(start, i);
		if (!env.structEnv.containsKey(typeName))
			return Optional.empty();
		String rest = s.substring(i).trim();
		if (!rest.startsWith("{"))
			return Optional.empty();
		// Delegate to the existing literal evaluator but provide the trimmed
		// trailing text starting at the type name so it can parse the braces.
		// NOTE: previously this accidentally called itself causing infinite
		// recursion and a StackOverflowError. Call tryEvalStructLiteral which
		// performs the actual parsing of "Type { ... }" literals.
		return tryEvalStructLiteral(typeName + " " + rest, env);
	}

	// (removed helper to reduce unused code)

	private Optional<Result<String, InterpretError>> tryEvalStructLiteral(String rest, Env env) {
		// Delegate the bulk of struct-literal handling to a helper to keep this
		// method's cyclomatic complexity low.
		int i = 0;
		int len = rest.length();
		while (i < len && Character.isWhitespace(rest.charAt(i)))
			i++;
		int start = i;
		while (i < len && Character.isJavaIdentifierPart(rest.charAt(i)))
			i++;
		if (start == i)
			return Optional.of(new Result.Err<>(new InterpretError("invalid struct literal", rest)));
		String structName = rest.substring(start, i);
		if (!env.structEnv.containsKey(structName))
			return Optional.of(new Result.Err<>(new InterpretError("unknown struct type", rest)));
		String afterName = rest.substring(i).trim();
		return evalStructLit(structName, afterName, env);
	}

	// Helper extracted from tryEvalStructLiteral that performs the detailed
	// parsing and evaluation of the literal fields and optional trailing
	// '.field' access. Keeping this logic separate reduces cyclomatic complexity
	// in the caller.
	private Optional<Result<String, InterpretError>> evalStructLit(String structName,
			String afterName,
			Env env) {
		String restContext = structName + " " + afterName;
		if (!afterName.startsWith("{"))
			return Optional.of(new Result.Err<>(new InterpretError("invalid struct literal", restContext)));
		int valClose = findMatchingBrace(afterName, 0);
		if (valClose < 0)
			return Optional.of(new Result.Err<>(new InterpretError("unterminated struct literal", restContext)));
		String vals = afterName.substring(1, valClose).trim();
		List<String> valExprs = new ArrayList<>();
		if (!vals.isEmpty()) {
			for (String p : vals.split(","))
				valExprs.add(p.trim());
		}
		List<String> fieldNames = env.structEnv.get(structName);
		if (valExprs.size() != fieldNames.size())
			return Optional.of(new Result.Err<>(new InterpretError("struct literal field count mismatch", restContext)));
		List<String> evaluated = new ArrayList<>();
		for (String e : valExprs) {
			Result<String, InterpretError> r = evaluateExpression(e, env);
			if (r instanceof Result.Err)
				return Optional.of(r);
			evaluated.add(((Result.Ok<String, InterpretError>) r).value());
		}
		Optional<Result<String, InterpretError>> typeChk = validateFieldTypes(structName, evaluated, env);
		if (typeChk.isPresent())
			return Optional.of(typeChk.get());
		StringBuilder sb = new StringBuilder();
		sb.append(structName);
		for (int j = 0; j < fieldNames.size(); j++) {
			sb.append('|').append(fieldNames.get(j)).append('=').append(evaluated.get(j));
		}
		String trailing = afterName.substring(valClose + 1).trim();
		Optional<Result<String, InterpretError>> trailRes = resolveFieldAccess(sb, trailing, restContext);
		if (trailRes.isPresent())
			return Optional.of(trailRes.get());
		return Optional.of(new Result.Err<>(new InterpretError("invalid struct literal", restContext)));
	}

	// Validate evaluated struct literal values against declared per-field types.
	private Optional<Result<String, InterpretError>> validateFieldTypes(String structName, List<String> evaluated,
			Env env) {
		List<String> ftypes = env.structFieldTypes.getOrDefault(structName, java.util.Collections.emptyList());
		if (ftypes.isEmpty())
			return Optional.empty();
		for (int j = 0; j < ftypes.size(); j++) {
			String ann = ftypes.get(j);
			if (!ann.isEmpty()) {
				Optional<Result<String, InterpretError>> chk = checkAnnotatedSuffix(ann, evaluated.get(j), env);
				if (chk.isPresent())
					return chk;
			}
		}
		return Optional.empty();
	}

	// Resolve optional trailing '.field' access on an encoded struct StringBuilder.
	private Optional<Result<String, InterpretError>> resolveFieldAccess(StringBuilder sb, String trailing,
			String restContext) {
		if (trailing.isEmpty())
			return Optional.of(new Result.Ok<>(STR_PREFIX + sb.toString()));
		if (trailing.startsWith(".")) {
			String fieldReq = trailing.substring(1).trim();
			if (!isSimpleIdentifier(fieldReq))
				return Optional.of(new Result.Err<>(new InterpretError("invalid struct field access", restContext)));
			for (String p : sb.toString().substring(restContext.split(" ")[0].length() + 1).split("\\|")) {
				int eq = p.indexOf('=');
				if (eq <= 0)
					continue;
				String fname = p.substring(0, eq);
				String fval = p.substring(eq + 1);
				if (fname.equals(fieldReq))
					return Optional.of(new Result.Ok<>(fval));
			}
			return Optional.of(new Result.Err<>(new InterpretError("unknown field", restContext)));
		}
		return Optional.empty();
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
		// Initialize child.localDecls: copy parent's tracked locals if present
		// so that `this` captures parameters and other inherited locals; then
		// add a fresh set for block-local declarations to be merged.
		java.util.Set<String> initialLocals = new java.util.HashSet<>();
		if (env.localDecls.isPresent())
			initialLocals.addAll(env.localDecls.get());
		child.localDecls = java.util.Optional.of(initialLocals);
		for (String part : inner) {
			String t = part.trim();
			if (t.isEmpty())
				continue;
			if (isStmtLike(t)) {
				Optional<Result<String, InterpretError>> stmtRes = handleStatement(t, child);
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

	// Build a runtime struct-like encoding for 'this' based on the current block's
	// local declarations tracked in env.localDecls. Outside a block, it's invalid.
	private Result<String, InterpretError> evalThisExpr(Env env, String source) {
		if (env.localDecls.isEmpty())
			return new Result.Err<>(new InterpretError("invalid this usage", source));
		java.util.Set<String> locals = env.localDecls.get();
		StringBuilder sb = new StringBuilder();
		sb.append("This");
		for (String name : locals) {
			String val = env.valEnv.getOrDefault(name, "");
			sb.append('|').append(name).append('=').append(val);
		}
		return new Result.Ok<>(STR_PREFIX + sb.toString());
	}

	// Create a child Env for block evaluation: shallow-copy val/type maps so
	// that assignments and let-declarations inside the block do not mutate the
	// outer environment.
	private Env makeChildEnv(Env parent) {
		Map<String, String> newVals = new HashMap<>(parent.valEnv);
		Map<String, String> newTypes = new HashMap<>(parent.typeEnv);
		Env child = new Env(newVals, newTypes, parent.source);
		child.fnEnv.putAll(parent.fnEnv);
		child.fnParamNames.putAll(parent.fnParamNames);
		child.fnParamTypes.putAll(parent.fnParamTypes);
		child.mutEnv.putAll(parent.mutEnv);
		child.structEnv.putAll(parent.structEnv);
		child.structFieldTypes.putAll(parent.structFieldTypes);
		return child;
	}

	private boolean isStmtLike(String t) {
		return t.startsWith("{") || t.startsWith("while ") || t.startsWith("while(") || t.startsWith("fn ") ||
				t.startsWith("fn(") || t.startsWith("if ") || t.startsWith("if(") || t.startsWith("let ") ||
				t.contains("+=") || (t.indexOf('=') > 0 && !t.startsWith("let "));
	}

	// Process an if-statement part starting at parts[idx]. Returns an IfOutcome
	// containing either a Result to surface to the caller, or the index of the
	// consumed part when the if completed normally.
	private IfOutcome processIfPart(String[] parts, int idx, Env env) {
		String part = parts[idx].trim();
		int open = part.indexOf('(');
		int close = part.indexOf(')', open + 1);
		if (open < 0 || close < 0)
			return new IfOutcome(Optional.of(new Result.Err<>(new InterpretError("invalid if syntax", env.source))), idx);
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
			return new IfOutcome(Optional.of(condRes), consumedAltIdx);
		String condVal = ((Result.Ok<String, InterpretError>) condRes).value();
		boolean condTrue = "true".equals(condVal);

		Optional<Result<String, InterpretError>> stmtRes = condTrue ? handleStatement(cons, env)
				: handleStatement(alt, env);
		if (stmtRes.isPresent())
			return new IfOutcome(stmtRes, consumedAltIdx);

		return new IfOutcome(Optional.empty(), consumedAltIdx);
	}
}
