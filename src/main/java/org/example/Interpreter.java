package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Minimal Intrepreter class.
 */
public class Interpreter {
	// Simple per-run function registry using ThreadLocal so static helpers can
	// access it
	public static final ThreadLocal<Map<String, FunctionInfo>> FUNC_REG = new ThreadLocal<>();
	// Per-run struct registry: just track struct names declared to prevent
	// duplicates
	public static final ThreadLocal<Set<String>> STRUCT_REG = new ThreadLocal<>();
	// Per-run variable environment for simple values, to support block scoping
	public static final ThreadLocal<Map<String, String>> VAR_ENV = new ThreadLocal<>();
	// Track which variables were declared mutable for this run (names present =>
	// mutable)
	public static final ThreadLocal<Set<String>> MUTABLE_VARS = new ThreadLocal<>();
	private static final Combiner BOOL_COMBINER = (l, r, op, src) -> {
		boolean a = parseBoolStrict(l.value, src);
		boolean b = parseBoolStrict(r.value, src);
		boolean c = (op == '&') ? (a && b) : (a || b);
		return new ValueParseResult(c ? "true" : "false", r.nextIndex);
	};

	// Core evaluator with optional registry reset; used to preserve env/registries
	// in blocks
	private static String internalEval(String input, boolean resetRegistries) throws InterpretingException {
		if (input == null || input.isEmpty()) {
			throw new InterpretingException("No input provided", input);
		}

		if (resetRegistries) {
			// Reset registries for this run
			FUNC_REG.set(new HashMap<>());
			STRUCT_REG.set(new HashSet<>());
			VAR_ENV.set(new HashMap<>());
			MUTABLE_VARS.set(new HashSet<>());
		} else {
			// Ensure there is some env present when evaluating nested blocks
			if (VAR_ENV.get() == null) {
				VAR_ENV.set(new HashMap<>());
			}
		}

		// Prepare trimmed view and fast paths
		int start = skipSpaces(input, 0);
		int end = input.length() - 1;
		while (end >= start && isSpace(input.charAt(end)))
			end--;
		String trimmed = (start <= end) ? input.substring(start, end + 1) : "";

		// 1) Fast path: plain decimal integer (allow surrounding spaces)
		if (!trimmed.isEmpty() && isAllDigits(trimmed)) {
			return trimmed;
		}

		// 1a) Fast path: boolean literals (allow surrounding spaces)
		if ("true".equals(trimmed) || "false".equals(trimmed)) {
			return trimmed;
		}

		// 1b) Block: "{ ... }" => evaluate inner content as a program with inherited
		// env
		if (start < input.length() && end >= start && input.charAt(start) == '{' && input.charAt(end) == '}') {
			String inner = input.substring(start + 1, end);
			return internalEval(inner, false);
		}

		// 1c) Bare identifier program: look up from env, but treat the keyword
		// 'this' as a special token handled by the parser. If a bare identifier is
		// provided and it's not 'this', return the env binding or error.
		if (!trimmed.isEmpty() && isIdentStart(trimmed.charAt(0))) {
			String maybeId = parseIdentifier(trimmed, 0);
			if (maybeId != null && maybeId.length() == trimmed.length()) {
				if ("this".equals(maybeId)) {
					// fall through to full parsing so 'this' is handled as a keyword
				} else {
					String val = VAR_ENV.get() != null ? VAR_ENV.get().get(maybeId) : null;
					if (val != null) {
						return val;
					}
					throw new InterpretingException("Undefined variable '" + maybeId + "'", input);
				}
			}
		}

		// 2) Minimal language:
		// - let [mut] <id> = <int>; <expr>
		// - let [mut] <id> = <int>; <id> = <int>; <expr>
		// Where <expr> is either <id> or <int>. If reassignment occurs, it must be
		// the same identifier and only allowed when declared with 'mut'.
		int i = 0;
		final int n = input.length();

		i = skipSpaces(input, i);
		if (startsWithWord(input, i, "let")) {
			i = consumeKeywordWithSpace(input, i, "let");
			// optional 'mut' followed by at least one space
			boolean isMutable = false;
			if (startsWithWord(input, i, "mut")) {
				i = consumeKeywordWithSpace(input, i, "mut");
				isMutable = true;
			}

			// identifier
			int idStart = i;
			String ident = parseIdentifier(input, i);
			if (ident == null) {
				throw new InterpretingException("Expected identifier after 'let'", input);
			}
			i = idStart + ident.length();

			// spaces and either '=' initializer or ':' typed declaration
			i = skipSpaces(input, i);
			if (i < n && input.charAt(i) == '=') {
				// initializer: int | bool | block
				ValueParseResult init = parseValueAfterEquals(input, i);
				if (init == null) {
					throw new InterpretingException("Expected initializer value after '='", input);
				}
				String intLit = init.value;
				i = init.nextIndex;
				// record into environment
				Map<String, String> env = VAR_ENV.get();
				recordBinding(env, ident, intLit, isMutable);

				// spaces
				i = skipSpaces(input, i);

				// ';' and spaces
				i = consumeSemicolonAndSpaces(input, i);

				// Next can be statements (blocks/loops/fn/struct) and then an expression.
				String currentVal = intLit;

				if (i < n && isIdentStart(input.charAt(i))) {
					String ref = parseIdentifier(input, i);
					if (ref == null) {
						throw new InterpretingException("Expected identifier", input);
					}
					i += ref.length();
					int afterRef = skipSpaces(input, i);
					if (afterRef < n && input.charAt(afterRef) == '=') {
						// It's a reassignment statement: <id> = <int>;
						if (!ref.equals(ident)) {
							throw new InterpretingException("Mismatched assignment target: expected '" + ident + "'", ref);
						}
						if (!isMutable) {
							throw new InterpretingException("Cannot reassign immutable variable '" + ref + "'", input);
						}
						i = afterRef + 1; // consume '='
						i = skipSpaces(input, i);
						ValueParseResult re = parseValue(input, i);
						if (re == null) {
							throw new InterpretingException("Expected value after '=' in assignment to '" + ident + "'", input);
						}
						String reassigned = re.value;
						i = re.nextIndex;
						i = skipSpaces(input, i);
						i = consumeSemicolonAndSpaces(input, i);
						currentVal = reassigned;
						// update env
						updateEnvIfPresent(env, ident, reassigned);

						// After reassignment, we expect the final expression
						if (i >= n) {
							throw new InterpretingException("Expected final expression after reassignment to '" + ident + "'", input);
						}
					} else {
						// Not an assignment; treat the identifier we already parsed as the final
						// expression.
						// If it's the declared identifier, prefer the declared/current value (which may
						// be
						// the initializer or an updated env value). Otherwise, look up the referenced
						// identifier in the environment and return its value.
						i = afterRef;
						i = skipSpaces(input, i);
						// If final identifier is same as declared, prefer env value (if set) else
						// currentVal
						if (ref.equals(ident)) {
							String envVal = (VAR_ENV.get() != null) ? VAR_ENV.get().get(ident) : null;
							ensureNoTrailing(input, i);
							return (envVal != null) ? envVal : currentVal;
						} else {
							// Special-case 'this' keyword: return encoded current env object
							if ("this".equals(ref)) {
								String obj = makeObjectFromEnv(VAR_ENV.get());
								ensureNoTrailing(input, i);
								return obj;
							}
							String envVal = (VAR_ENV.get() != null) ? VAR_ENV.get().get(ref) : null;
							if (envVal == null) {
								throw new InterpretingException("Undefined variable '" + ref + "'", input);
							}
							ensureNoTrailing(input, i);
							return envVal;
						}
					}
				}

				// Allow zero or more statements before the final expression (mirrors typed-let
				// path)
				i = consumeZeroOrMoreStatements(input, i);
				return finishWithExpressionOrValue(input, i, ident, currentVal, false);
			} else if (i < n && input.charAt(i) == ':') {
				// typed declaration: let <id> : <Type> ;
				i++; // consume ':'
				i = skipSpaces(input, i);
				String typeId = null;
				// support array type syntax: [I32; 3]
				if (i < n && input.charAt(i) == '[') {
					int p = skipSpaces(input, i + 1);
					// parse element type identifier
					String elemType = parseIdentifier(input, p);
					if (elemType == null) {
						throw new InterpretingException("Expected element type in array type", input);
					}
					p += elemType.length();
					p = skipSpaces(input, p);
					p = expectCharOrThrow(input, p, ';');
					p = skipSpaces(input, p);
					// parse length integer
					String lenLit = parseInteger(input, p);
					if (lenLit == null) {
						throw new InterpretingException("Expected array length in type", input);
					}
					p += lenLit.length();
					p = skipSpaces(input, p);
					p = expectCharOrThrow(input, p, ']');
					typeId = "[" + elemType + ";" + lenLit + "]";
					i = p;
				} else {
					typeId = parseIdentifier(input, i);
					if (typeId == null) {
						throw new InterpretingException("Expected type identifier after ':'", input);
					}
					i += typeId.length();
				}

				// spaces: if an '=' follows we will parse an immediate assignment inline
				i = skipSpaces(input, i);
				String currentVal = null;
				if (i < n && input.charAt(i) == '=') {
					// inline assignment: let id : Type = <value> ;
					ValueParseResult v = parseValueAfterEquals(input, i);
					if (v == null) {
						throw new InterpretingException("Expected value", input);
					}
					currentVal = v.value;
					i = v.nextIndex;
					i = skipSpaces(input, i);
					i = consumeSemicolonAndSpaces(input, i);
					recordBinding(VAR_ENV.get(), ident, currentVal, isMutable);
					// After inline assignment, allow zero-or-more statements then finish
					return finishAfterInlineAssign(input, i, ident, currentVal);
				} else {
					i = consumeSemicolonAndSpaces(input, i);
				}

				// After typed declaration, support either:
				// - if (cond) <id> = <value> else <id> = <value>; <expr>
				// - <id> = <value>; <expr>
				// currentVal may be set by later assignments
				currentVal = null;

				if (startsWithWord(input, i, "if")) {
					// if-statement with assignments
					int j = i;
					j = consumeKeywordWithSpace(input, j, "if");
					j = skipSpaces(input, j);
					j = expectCharOrThrow(input, j, '(');
					j = skipSpaces(input, j);
					ValueParseResult cond = parseBooleanConditionOrThrow(input, j);
					j = cond.nextIndex;
					j = skipSpaces(input, j);
					j = expectCharOrThrow(input, j, ')');
					j = skipSpaces(input, j);

					AssignmentParseResult thenAsg = parseAssignmentTo(input, j, ident);
					if (thenAsg == null) {
						throw new InterpretingException("Expected assignment to '" + ident + "' in then-branch", input);
					}
					j = thenAsg.nextIndex;
					j = skipSpaces(input, j);
					j = consumeKeywordWithSpace(input, j, "else");
					j = skipSpaces(input, j);
					AssignmentParseResult elseAsg = parseAssignmentTo(input, j, ident);
					if (elseAsg == null) {
						throw new InterpretingException("Expected assignment to '" + ident + "' in else-branch", input);
					}
					j = elseAsg.nextIndex;
					j = skipSpaces(input, j);
					j = consumeSemicolonAndSpaces(input, j);

					// Apply assignments (handle indexed assignment mutation and mutability checks)
					currentVal = "true".equals(cond.value) ? applyAssignmentResult(input, ident, thenAsg)
							: applyAssignmentResult(input, ident, elseAsg);
					updateEnv(ident, currentVal);
					// assigned
					i = j; // advance
				} else {
					// direct assignment
					AssignmentParseResult asg = parseAssignmentTo(input, i, ident);
					if (asg != null) {
						i = asg.nextIndex;
						i = skipSpaces(input, i);
						i = consumeSemicolonAndSpaces(input, i);
						currentVal = applyAssignmentResult(input, ident, asg);
						updateEnv(ident, currentVal);
						// assigned
					}
				}

				// Allow zero or more statements (block/while/for/fn/struct) before the final
				// expression
				i = consumeZeroOrMoreStatements(input, i);

				return finishWithExpressionOrValue(input, i, ident, currentVal, true);
			} else {
				throw new InterpretingException("Expected '=' or ':' after identifier '" + ident + "'", input);
			}
		}

		// Fallback: allow a standalone expression/value (supports arithmetic, booleans,
		// blocks, function calls)
		// Allow zero-or-more leading statements (fn/struct/for/while/blocks) before the
		// final expression so programs like "fn f() => 1; f()" parse correctly.
		i = consumeZeroOrMoreStatements(input, i);
		ValueParseResult expr = parseValue(input, i);
		if (expr != null) {
			int after = skipSpaces(input, expr.nextIndex);
			ensureNoTrailing(input, after);
			return expr.value;
		}

		// Anything else is currently undefined.
		throw new InterpretingException("Could not parse input", input);
	}

	// Execute a function body (or closure represented by a FunctionInfo) in a
	// child context with parameters bound. Returns the result string.
	private static String executeFunction(FunctionInfo fi, List<String> argValues) {
		ChildContext cc = ChildContext.enter();
		try {
			Map<String, String> env = VAR_ENV.get();
			for (int k = 0; k < fi.paramNames.size(); k++) {
				env.put(fi.paramNames.get(k), argValues.get(k));
			}
			System.err.println("[DEBUG] executeFunction args=" + argValues);
			System.err.println("[DEBUG] env after bind=" + env);
			String body = fi.bodyValue == null ? "" : fi.bodyValue;
			String result;
			if (body.startsWith("{") && body.endsWith("}")) {
				String inner = body.substring(1, body.length() - 1);
				result = internalEval(inner, false);
			} else {
				result = evalInChildEnv(body);
			}
			System.err.println("[DEBUG] executeFunction result=[" + result + "]");
			return result;
		} finally {
			cc.restore(false);
		}
	}

	// Small helper to return parsed argument values and next index after ')'
	private static final class ArgParseResult {
		final List<String> args;
		final int nextIndex;

		ArgParseResult(List<String> a, int n) {
			this.args = a;
			this.nextIndex = n;
		}
	}

	private static ArgParseResult parseArgumentList(String s, int pos) {
		ArrayList<String> argValues = new ArrayList<>();
		if (pos < s.length() && s.charAt(pos) != ')') {
			while (true) {
				ValueParseResult a = requireValue(s, pos);
				argValues.add(a.value);
				int next = consumeCommaAndSpaces(s, a.nextIndex);
				if (next != a.nextIndex) {
					pos = next;
					continue;
				}
				pos = a.nextIndex;
				break;
			}
		}
		pos = expectCloseParenAndSkip(s, pos);
		return new ArgParseResult(argValues, pos);
	}

	// Consume optional semicolon at pos (skipping spaces). Returns next index.
	private static int consumeOptionalSemicolon(String s, int pos) {
		int p = skipSpacesIndexOfChar(s, pos, ';');
		if (p >= 0) {
			return expectAndSkip(s, p, ';');
		}
		return skipSpaces(s, pos);
	}

	private static FunctionInfo lookupFunction(String name) {
		Map<String, FunctionInfo> reg = FUNC_REG.get();
		return (reg != null) ? reg.get(name) : null;
	}

	// Skip spaces and if the next non-space char equals c return its index, else -1
	private static int skipSpacesIndexOfChar(String s, int pos, char c) {
		int p = skipSpaces(s, pos);
		if (p < s.length() && s.charAt(p) == c)
			return p;
		return -1;
	}

	// Consume zero or more leading statements and return the next index (spaces
	// skipped)
	private static int consumeZeroOrMoreStatements(String s, int i) {
		i = skipSpaces(s, i);
		while (true) {
			int next = parseBlockStatement(s, i);
			if (next < 0 || next == i) {
				next = parseWhileStatement(s, i);
			}
			if (next < 0 || next == i) {
				next = parseForStatement(s, i);
				if (next < 0 || next == i) {
					next = parseFunctionDeclStatement(s, i);
					if (next < 0 || next == i) {
						next = parseStructDeclStatement(s, i);
					}
					if (next < 0 || next == i)
						break;
				}
			}
			i = next;
			i = skipSpaces(s, i);
		}
		return i;
	}

	private static boolean isAllDigits(String s) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		return true;
	}

	private static boolean startsWithWord(String s, int i, String word) {
		int n = s.length();
		int w = word.length();
		if (i + w - 1 >= n)
			return false;
		for (int k = 0; k < w; k++) {
			if (s.charAt(i + k) != word.charAt(k))
				return false;
		}
		return true;
	}

	private static boolean isSpace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
	}

	private static int skipSpaces(String s, int i) {
		final int n = s.length();
		while (i < n && isSpace(s.charAt(i)))
			i++;
		return i;
	}

	private static int consumeKeywordWithSpace(String s, int i, String word) {
		if (!startsWithWord(s, i, word)) {
			throw new InterpretingException("Expected keyword '" + word + "'", s);
		}
		i += word.length();
		if (i >= s.length() || !isSpace(s.charAt(i))) {
			throw new InterpretingException("Expected space after keyword '" + word + "'", s);
		}
		return skipSpaces(s, i);
	}

	private static boolean isIdentStart(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private static boolean isIdentPart(char c) {
		return isIdentStart(c) || (c >= '0' && c <= '9');
	}

	// Evaluate input string using a child environment that inherits current VAR_ENV
	private static String evalInChildEnv(String input) {
		ChildContext cc = ChildContext.enter();
		try {
			return internalEval(input, false);
		} finally {
			cc.restore(false);
		}
	}

	// Execute simple assignment statements inside a block in a child environment,
	// then merge updates for pre-existing variables back to the parent env.
	private static void execBlockStatements(String code) {
		ChildContext cc = ChildContext.enter();
		try {
			int pos = 0;
			final int n = code.length();
			while (true) {
				pos = skipSpaces(code, pos);
				if (pos >= n)
					break;
				// Skip nested blocks entirely as statements
				if (code.charAt(pos) == '{') {
					int close = findMatchingBrace(code, pos);
					if (close < 0)
						throw new InterpretingException("Unmatched '{' in block", code);
					pos = skipSpaces(code, close + 1);
					// optional semicolon
					if (pos < n && code.charAt(pos) == ';') {
						pos = skipSpaces(code, pos + 1);
					}
					continue;
				}
				String id = parseIdentifier(code, pos);
				if (id == null) {
					// tolerate empty statements
					break;
				}
				pos += id.length();
				pos = skipSpaces(code, pos);
				// Support indexed assignment like id[expr] = value
				boolean isIndexed = false;
				int idxStart = -1;
				int idxEnd = -1;
				if (pos < n && code.charAt(pos) == '[') {
					isIndexed = true;
					idxStart = skipSpaces(code, pos + 1);
					ValueParseResult idxVal = parseValue(code, idxStart);
					if (idxVal == null)
						throw new InterpretingException("Expected index expression inside []", code);
					idxEnd = skipSpaces(code, idxVal.nextIndex);
					idxEnd = expectCharOrThrow(code, idxEnd, ']');
					pos = skipSpaces(code, idxEnd + 0);
				}
				if (pos >= n || code.charAt(pos) != '=') {
					// Not an assignment; skip until next semicolon (tolerate expression statements)
					while (pos < n && code.charAt(pos) != ';')
						pos++;
					pos = skipSpaces(code, pos < n ? pos + 1 : pos);
					continue;
				}
				pos++;
				pos = skipSpaces(code, pos);
				ValueParseResult v = parseValue(code, pos);
				if (v == null)
					throw new InterpretingException("Expected value in block assignment to '" + id + "'", code);
				pos = skipSpaces(code, v.nextIndex);
				pos = consumeSemicolonAndSpaces(code, pos);
				// apply assignment in child env
				Map<String, String> env = VAR_ENV.get();
				if (!isIndexed) {
					if (env != null)
						env.put(id, v.value);
				} else {
					// Indexed mutation: ensure variable exists and is an array and is mutable
					if (env == null || !env.containsKey(id)) {
						throw new InterpretingException("Undefined variable '" + id + "'", code);
					}
					Set<String> mv = MUTABLE_VARS.get();
					if (mv == null || !mv.contains(id)) {
						throw new InterpretingException("Cannot mutate immutable variable '" + id + "'", code);
					}
					String arrStr = env.get(id);
					if (arrStr == null || !arrStr.startsWith("__ARRAY__")) {
						throw new InterpretingException("Cannot index non-array value", code);
					}
					String inner = arrStr.substring("__ARRAY__".length());
					String[] elems = decodeArrayElemsFromInner(inner, code);
					ValueParseResult idxV = parseValue(code, idxStart);
					int idx = parseIntStrict(idxV.value, code);
					if (idx < 0 || idx >= elems.length) {
						throw new InterpretingException("Array index out of bounds: " + idx, code);
					}
					elems[idx] = v.value;
					putArrayEncoded(env, id, elems);
				}
			}
		} finally {
			cc.restore(true);
		}
	}

	// Primary (non-infix) values: if/else, match, block, boolean, function call,
	// integer
	private static ValueParseResult parsePrimaryValue(String s, int i) {
		final int n = s.length();
		if (i >= n)
			return null;
		i = skipSpaces(s, i);

		// if (cond) then else
		if (startsWithWord(s, i, "if")) {
			i = consumeKeywordWithSpace(s, i, "if");
			i = skipSpaces(s, i);
			i = expectCharOrThrow(s, i, '(');
			i = skipSpaces(s, i);
			ValueParseResult cond = parseValue(s, i);
			if (!isBooleanResult(cond)) {
				return null;
			}
			i = cond.nextIndex;
			i = skipSpaces(s, i);
			i = expectCharOrThrow(s, i, ')');
			i = skipSpaces(s, i);
			ValueParseResult thenV = parseValue(s, i);
			if (thenV == null)
				return null;
			i = thenV.nextIndex;
			i = skipSpaces(s, i);
			i = consumeKeywordWithSpace(s, i, "else");
			i = skipSpaces(s, i);
			ValueParseResult elseV = parseValue(s, i);
			if (elseV == null)
				return null;
			i = elseV.nextIndex;
			String picked = "true".equals(cond.value) ? thenV.value : elseV.value;
			return new ValueParseResult(picked, i);
		}
		// match expr
		if (startsWithWord(s, i, "match")) {
			return parseMatchExpression(s, i);
		}
		// block
		if (s.charAt(i) == '{') {
			int close = findMatchingBrace(s, i);
			if (close < 0)
				return null;
			String inner = s.substring(i + 1, close);
			String val = evalInChildEnv(inner);
			return new ValueParseResult(val, close + 1);
		}
		// boolean
		if (startsWithWord(s, i, "true")) {
			return new ValueParseResult("true", i + 4);
		}
		if (startsWithWord(s, i, "false")) {
			return new ValueParseResult("false", i + 5);
		}

		// 'this' keyword -> object representing current struct/fields (use env)
		if (startsWithWord(s, i, "this") && hasKeywordBoundary(s, i, 4)) {
			// encode current environment as an object
			String obj = makeObjectFromEnv(VAR_ENV.get());
			return new ValueParseResult(obj, i + 4);
		}
		// array literal: [ val, val, ... ]
		if (s.charAt(i) == '[') {
			int p = skipSpaces(s, i + 1);
			ArrayList<String> elems = new ArrayList<>();
			final int nn = s.length();
			// empty array
			if (p < nn && s.charAt(p) == ']') {
				int after = expectCharOrThrow(s, p, ']');
				// encode array as __ARRAY__<len>|<base64(elements joined by ';')>
				String body = "";
				String b64 = java.util.Base64.getEncoder().encodeToString(body.getBytes());
				String enc = "__ARRAY__0|" + b64;
				return new ValueParseResult(enc, after);
			}
			while (true) {
				ValueParseResult v = parseValue(s, p);
				if (v == null)
					return null;
				elems.add(v.value);
				p = skipSpaces(s, v.nextIndex);
				if (p < nn && s.charAt(p) == ',') {
					p = skipSpaces(s, p + 1);
					continue;
				}
				break;
			}
			p = skipSpaces(s, p);
			p = expectCharOrThrow(s, p, ']');
			int after = p;
			String body = String.join(";", elems);
			String b64 = java.util.Base64.getEncoder().encodeToString(body.getBytes());
			String enc = "__ARRAY__" + elems.size() + "|" + b64;
			return new ValueParseResult(enc, skipSpaces(s, after));
		}
		// function call: <ident> ( [value [, ...]] )
		if (isIdentStart(s.charAt(i))) {
			int idStart = i;
			String ident = parseIdentifier(s, i);
			if (ident != null) {
				i = idStart + ident.length();
				i = skipSpaces(s, i);
				if (i < n && s.charAt(i) == '(') {
					// ensure not a reserved keyword like 'if' or 'match'
					if (!"if".equals(ident) && !"match".equals(ident)) {
						return parseFunctionCallExpression(s, idStart);
					}
				}
			}
			// otherwise, fall through; bare identifiers are not values here
			i = idStart; // restore to avoid partial consumption
		}

		// Support bare identifiers as values by resolving them from the current
		// variable environment. This allows expressions like `a + b` to work when
		// `a` and `b` are parameters or local variables.
		if (isIdentStart(s.charAt(i))) {
			String id = parseIdentifier(s, i);
			if (id != null) {
				// Special-case 'this' is handled above; for other identifiers, look up env
				String envVal = (VAR_ENV.get() != null) ? VAR_ENV.get().get(id) : null;
				if (envVal == null) {
					throw new InterpretingException("Undefined variable '" + id + "'", s);
				}
				return new ValueParseResult(envVal, i + id.length());
			}
		}
		// integer
		String intLit = parseInteger(s, i);
		if (intLit != null) {
			return new ValueParseResult(intLit, i + intLit.length());
		}
		return null;
	}

	private static boolean isIntVal(String v) {
		if (v == null)
			return false;
		int len = v.length();
		int start = 0;
		if (len > 0 && v.charAt(0) == '-') {
			if (len == 1)
				return false; // just '-'
			start = 1;
		}
		return isAllDigits(v.substring(start));
	}

	private static int parseIntStrict(String v, String s) {
		if (!isIntVal(v))
			throw new InterpretingException("Expected integer value", s);
		return Integer.parseInt(v);
	}

	private static ValueParseResult applyBinOp(ValueParseResult left, ValueParseResult right, char op, String s) {
		int a = parseIntStrict(left.value, s);
		int b = parseIntStrict(right.value, s);
		int c;
		if (op == '*') {
			c = a * b;
		} else if (op == '+') {
			c = a + b;
		} else if (op == '-') {
			c = a - b;
		} else {
			throw new InterpretingException("Unsupported operator '" + op + "'", s);
		}
		return new ValueParseResult(String.valueOf(c), right.nextIndex);
	}

	private static boolean isBoolString(String v) {
		return "true".equals(v) || "false".equals(v);
	}

	private static boolean parseBoolStrict(String v, String s) {
		if (!isBoolString(v))
			throw new InterpretingException("Expected boolean value", s);
		return Boolean.parseBoolean(v);
	}

	// Generic left-associative chain parser with pluggable operator detection and
	// combination
	private static ValueParseResult parseChain(String s, int i, NextParser next, OpDetector det, Combiner comb) {
		ValueParseResult left = next.parse(s, i);
		if (left == null)
			return null;
		int pos = skipSpaces(s, left.nextIndex);
		for (OpHit hit; (hit = det.detect(s, pos)) != null;) {
			pos = skipSpaces(s, hit.nextPos);
			ValueParseResult right = next.parse(s, pos);
			if (right == null)
				throw new InterpretingException("Expected value after operator", s);
			left = comb.combine(left, right, hit.ch, s);
			pos = skipSpaces(s, left.nextIndex);
		}
		return left;
	}

	private static OpDetector fixedStringOp(String text, char tag) {
		return (str, p) -> (p + text.length() <= str.length() && str.startsWith(text, p)) ? new OpHit(p + text.length(),
				tag) : null;
	}

	// logicalAnd := addSub ( '&&' addSub )*
	private static ValueParseResult parseLogicalAnd(String s, int i) {
		OpDetector andDetector = fixedStringOp("&&", '&');
		return parseChain(s, i, Interpreter::parseExprAddSub, andDetector, BOOL_COMBINER);
	}

	// logicalOr := logicalAnd ( '||' logicalAnd )*
	private static ValueParseResult parseLogicalOr(String s, int i) {
		OpDetector orDetector = fixedStringOp("||", '|');
		return parseChain(s, i, Interpreter::parseLogicalAnd, orDetector, BOOL_COMBINER);
	}

	// Generic left-associative infix parser over a set of operator characters
	private static ValueParseResult parseInfix(String s, int i, NextParser next, char... ops) {
		HashSet<Character> allowed = new HashSet<>();
		for (char c : ops) {
			allowed.add(c);
		}
		OpDetector det = (str, p) -> (p < str.length() && allowed.contains(str.charAt(p))) ? new OpHit(p + 1, str.charAt(p))
				: null;
		Combiner comb = Interpreter::applyBinOp;
		return parseChain(s, i, next, det, comb);
	}

	// term := primary ( '*' primary )*
	private static ValueParseResult parseTerm(String s, int i) {
		return parseInfix(s, i, Interpreter::parseUnary, '*');
	}

	// expr := term ( ('+'|'-') term )*
	private static ValueParseResult parseExprAddSub(String s, int i) {
		return parseInfix(s, i, Interpreter::parseTerm, '+', '-');
	}

	// unary := ( '!' | '-' ) unary | primary
	private static ValueParseResult parseUnary(String s, int i) {
		int p = skipSpaces(s, i);
		if (p < s.length()) {
			char ch = s.charAt(p);
			if (ch == '!' || ch == '-') {
				int j = skipSpaces(s, p + 1);
				ValueParseResult inner = parseUnary(s, j);
				if (inner == null)
					return null;
				if (ch == '!') {
					boolean v = parseBoolStrict(inner.value, s);
					return new ValueParseResult(v ? "false" : "true", inner.nextIndex);
				} else { // '-'
					int v = parseIntStrict(inner.value, s);
					return new ValueParseResult(String.valueOf(-v), inner.nextIndex);
				}
			}
		}
		return parsePrimaryValue(s, p);
	}

	// Value now includes logical operators (||, &&) and arithmetic (+, -, *)
	private static ValueParseResult parseValue(String s, int i) {
		// Support postfix/member access (value.field)
		return parsePostfix(s, i);
	}

	// Helper to parse a value when the current position is at an '=' char.
	// Consumes the '=' and any following spaces, then returns the parsed value.
	private static ValueParseResult parseValueAfterEquals(String s, int pos) {
		pos++; // consume '='
		pos = skipSpaces(s, pos);
		return parseValue(s, pos);
	}

	// Small helper to decode a base64 string to UTF-8. Extracted to avoid
	// duplicated inline decoding code flagged by CPD.
	private static String decodeBase64(String b64) {
		return new String(java.util.Base64.getDecoder().decode(b64));
	}

	// Helper to extract and decode the body portion from an encoded string that
	// uses the form <meta>|<base64>. Throws if the '|' separator is missing.
	private static String extractBase64Body(String inner, String src) {
		int sep = inner.indexOf('|');
		if (sep < 0)
			throw new InterpretingException("Malformed array encoding", src);
		String b64 = inner.substring(sep + 1);
		return decodeBase64(b64);
	}

	// Helper: decode the body of an encoded array inner string and split into elems
	private static String[] decodeArrayElemsFromInner(String inner, String src) {
		String body = extractBase64Body(inner, src);
		return body.isEmpty() ? new String[0] : body.split(";", -1);
	}

	// Helper: encode elems into the __ARRAY__ form and store in env under id
	private static void putArrayEncoded(Map<String, String> env, String id, String[] elems) {
		String newBody = String.join(";", elems);
		String newB64 = java.util.Base64.getEncoder().encodeToString(newBody.getBytes());
		String newEnc = "__ARRAY__" + elems.length + "|" + newB64;
		env.put(id, newEnc);
	}

	// Helper: record a binding into an environment and register mutability when
	// requested
	private static void recordBinding(Map<String, String> env, String name, String value, boolean isMutable) {
		if (env != null)
			env.put(name, value);
		if (isMutable) {
			Set<String> mv = MUTABLE_VARS.get();
			if (mv != null)
				mv.add(name);
		}
	}

	// Helper used after inline assignment to consume optional statements and finish
	private static String finishAfterInlineAssign(String input, int i, String ident, String currentVal) {
		i = consumeZeroOrMoreStatements(input, i);
		return finishWithExpressionOrValue(input, i, ident, currentVal, true);
	}

	// Postfix parser to allow member access like <value>.<field>
	private static ValueParseResult parsePostfix(String s, int i) {
		// base value is the existing logical/or expression
		ValueParseResult base = parseLogicalOr(s, i);
		if (base == null)
			return null;
		int pos = skipSpaces(s, base.nextIndex);
		// allow chained accesses: a.b.c
		while (pos < s.length() && (s.charAt(pos) == '.' || s.charAt(pos) == '[')) {
			if (s.charAt(pos) == '[') {
				// indexing: parse index expression between [ and ]
				int open = expectCharOrThrow(s, pos, '[');
				int idxStart = skipSpaces(s, open);
				// debug: show what we will parse as index
				System.err.println("[DEBUG] parsePostfix: pos=" + pos + " open=" + open + " idxStart=" + idxStart
						+ " charAtIdxStart=" + (idxStart < s.length() ? s.charAt(idxStart) : '∅'));
				ValueParseResult idxVal = parseValue(s, idxStart);
				System.err.println("[DEBUG] parsePostfix: idxVal="
						+ (idxVal == null ? "<null>" : (idxVal.value + ", next=" + idxVal.nextIndex)));
				if (idxVal == null) {
					throw new InterpretingException("Expected index expression inside []", s);
				}
				int afterIdx = skipSpaces(s, idxVal.nextIndex);
				int close = expectCharOrThrow(s, afterIdx, ']');
				int after = close;
				// apply indexing on current base.value
				String arrStr = base.value;
				if (arrStr == null || !arrStr.startsWith("__ARRAY__")) {
					throw new InterpretingException("Cannot index non-array value", s);
				}
				String inner = arrStr.substring("__ARRAY__".length());
				int sep = inner.indexOf('|');
				if (sep < 0)
					throw new InterpretingException("Malformed array encoding", s);
				String[] elems = decodeArrayElemsFromInner(inner, s);
				int idx = parseIntStrict(idxVal.value, s);
				if (idx < 0 || idx >= elems.length) {
					throw new InterpretingException("Array index out of bounds: " + idx, s);
				}
				String elem = elems[idx];
				base = new ValueParseResult(elem, skipSpaces(s, after));
				pos = skipSpaces(s, base.nextIndex);
				continue;
			}
			pos = skipSpaces(s, pos + 1);
			String field = parseIdentifier(s, pos);
			if (field == null) {
				throw new InterpretingException("Expected field identifier after '.'", s);
			}
			pos += field.length();
			// debug: show base and requested field
			System.err.println("[DEBUG] parsePostfix base.value=[" + base.value + "] field=" + field);
			// preserve the parent object string before resolving the field
			String parentObjStr = base.value;
			// resolve field from object-encoded base.value
			String resolved = extractFieldFromObject(parentObjStr, field, s);
			base = new ValueParseResult(resolved, skipSpaces(s, pos));
			pos = skipSpaces(s, base.nextIndex);
			// If the caller writes something like obj.fnName(...), allow calling a
			// resolved field if it's followed by parentheses. The resolved value may
			// either be a function name bound in the registry or a serialized
			// closure encoded with '__CLOSURE__'.
			int callPos = skipSpaces(s, base.nextIndex);
			if (callPos < s.length() && s.charAt(callPos) == '(') {
				// parse arguments using helper
				callPos = expectOpenParenAndSkip(s, callPos);
				ArgParseResult apr = parseArgumentList(s, callPos);
				ArrayList<String> argValues = new ArrayList<>(apr.args);
				callPos = apr.nextIndex;
				// If the resolved value encodes a closure, decode it and execute.
				if (resolved != null && resolved.startsWith("__CLOSURE__")) {
					String inner = resolved.substring("__CLOSURE__".length());
					int sep = inner.indexOf('|');
					if (sep < 0) {
						throw new InterpretingException("Malformed closure encoding", s);
					}
					String paramsText = inner.substring(0, sep);
					String body = extractBase64Body(inner, s);
					List<String> pnames = new ArrayList<>();
					if (!paramsText.isEmpty()) {
						for (String p : paramsText.split(",")) {
							pnames.add(p);
						}
					}
					if (pnames.size() != argValues.size()) {
						throw new InterpretingException(
								"Wrong number of arguments for closure (expected " + pnames.size() + ", got " + argValues.size() + ")",
								s);
					}
					// Create a temporary FunctionInfo and execute via helper
					FunctionInfo tmp = new FunctionInfo(pnames, body);
					// If the closure was extracted from an object value, execute it with the
					// object's fields present in VAR_ENV so the closure can reference captured
					// locals/parameters via normal identifier lookup. Use the preserved
					// parent object string to build the env map.
					String result;
					if (isObjectString(parentObjStr)) {
						Map<String, String> objMap = parseObjectStringToMap(parentObjStr);
						Map<String, String> prev = VAR_ENV.get();
						try {
							VAR_ENV.set(objMap);
							result = executeFunction(tmp, argValues);
						} finally {
							VAR_ENV.set(prev);
						}
					} else {
						result = executeFunction(tmp, argValues);
					}
					base = new ValueParseResult(result, callPos);
					pos = skipSpaces(s, base.nextIndex);
				} else {
					// Perform call by looking up the resolved function name in registry
					FunctionInfo fi = lookupFunction(resolved);
					if (fi == null) {
						throw new InterpretingException("Undefined function '" + resolved + "'", s);
					}
					if (fi.paramNames.size() != argValues.size()) {
						throw new InterpretingException(
								"Wrong number of arguments for '" + resolved + "' (expected " + fi.paramNames.size() + ", got "
										+ argValues.size() + ")",
								s);
					}
					// Use shared helper to execute
					String result = executeFunction(fi, argValues);
					base = new ValueParseResult(result, callPos);
					pos = skipSpaces(s, base.nextIndex);
				}
			}
		}
		return base;
	}

	// Encode current environment as an object string. Deterministic ordering via
	// sorted keys.
	private static String makeObjectFromEnv(Map<String, String> env) {
		if (env == null || env.isEmpty())
			return "__OBJ__{}";
		List<String> keys = new ArrayList<>(env.keySet());
		java.util.Collections.sort(keys);
		StringBuilder sb = new StringBuilder();
		sb.append("__OBJ__{");
		boolean first = true;
		for (String k : keys) {
			if (!first)
				sb.append(';');
			first = false;
			String v = env.get(k);
			// If the value names a function present in the current function registry,
			// serialize the function body and parameter list into the object so
			// callers of returned 'this' can invoke nested functions.
			Map<String, FunctionInfo> reg = FUNC_REG.get();
			if (v != null && reg != null && reg.containsKey(v)) {
				FunctionInfo fi = reg.get(v);
				String params = String.join(",", fi.paramNames);
				String body = fi.bodyValue == null ? "" : fi.bodyValue;
				String b64 = java.util.Base64.getEncoder().encodeToString(body.getBytes());
				String closureEncoded = "__CLOSURE__" + params + "|" + b64;
				sb.append(k).append('=').append(closureEncoded.replace(";", "\\;"));
			} else {
				sb.append(k).append('=').append(v == null ? "" : v.replace(";", "\\;"));
			}
		}
		sb.append('}');
		return sb.toString();
	}

	private static boolean isObjectString(String s) {
		return s != null && s.startsWith("__OBJ__{") && s.endsWith("}");
	}

	// Extract field value from encoded object string. Throws on non-object or
	// missing field.
	private static String extractFieldFromObject(String obj, String field, String src) {
		// debug-inspect object encoding and its parsed parts
		System.err.println("[DEBUG] extractFieldFromObject obj=[" + obj + "] field=" + field);
		if (!isObjectString(obj)) {
			System.err.println("[DEBUG] extractFieldFromObject: not an object string");
			throw new InterpretingException("Cannot access field '" + field + "' on non-object", src);
		}
		String inner = obj.substring(8, obj.length() - 1); // between { }
		System.err.println("[DEBUG] extractFieldFromObject inner=[" + inner + "]");
		if (inner.isEmpty()) {
			System.err.println("[DEBUG] extractFieldFromObject: empty object");
			throw new InterpretingException("Field '" + field + "' not found on object", src);
		}
		String[] parts = inner.split("(?<!\\\\);", -1);
		System.err.println("[DEBUG] extractFieldFromObject parts.len=" + parts.length);
		for (int pi = 0; pi < parts.length; pi++) {
			String p = parts[pi];
			String[] kv = parsePartKeyValue(p, pi);
			if (kv == null)
				continue;
			String k = kv[0];
			String v = kv[1];
			System.err.println("[DEBUG] kv: '" + k + "' = '" + v + "'");
			if (k.equals(field))
				return v;
		}
		System.err.println("[DEBUG] extractFieldFromObject: field not found after scanning parts");
		throw new InterpretingException("Field '" + field + "' not found on object", src);
	}

	// Parse an object-encoded string like '__OBJ__{a=1;b=2;}' into a Map.
	private static Map<String, String> parseObjectStringToMap(String obj) {
		Map<String, String> m = new HashMap<>();
		if (!isObjectString(obj))
			return m;
		String inner = obj.substring(8, obj.length() - 1);
		if (inner.isEmpty())
			return m;
		String[] parts = inner.split("(?<!\\\\);", -1);
		for (String p : parts) {
			String[] kv = parsePartKeyValue(p, -1);
			if (kv == null)
				continue;
			m.put(kv[0], kv[1]);
		}
		return m;
	}

	// Helper to parse a single 'key=value' part. Returns {key,value} or null when
	// the part is malformed. `idx` is used for debug logging when >= 0.
	private static String[] parsePartKeyValue(String part, int idx) {
		int eq = part.indexOf('=');
		if (idx >= 0) {
			System.err.println("[DEBUG] part[" + idx + "]=['" + part + "'] eq=" + eq);
		}
		if (eq < 0)
			return null;
		String k = part.substring(0, eq);
		String v = part.substring(eq + 1).replace("\\;", ";");
		return new String[] { k, v };
	}

	private static String parseIdentifier(String s, int i) {
		int n = s.length();
		if (i >= n || !isIdentStart(s.charAt(i)))
			return null;
		int j = i + 1;
		while (j < n && isIdentPart(s.charAt(j)))
			j++;
		return s.substring(i, j);
	}

	// Parses: match <value> { <int> => <value> ; ... ; _ => <value> ; }
	// Returns the selected arm's value. Requires braces and semicolons between
	// arms.
	private static ValueParseResult parseMatchExpression(String s, int i) {
		i = consumeKeywordWithSpace(s, i, "match");
		i = skipSpaces(s, i);
		ValueParseResult subject = parseValue(s, i);
		if (subject == null) {
			throw new InterpretingException("Expected match subject value", s);
		}
		String subj = subject.value;
		i = subject.nextIndex;
		i = skipSpaces(s, i);
		i = expectCharOrThrow(s, i, '{');

		int pos = i + 1;
		String selected = null;
		boolean matched = false;
		boolean sawWildcard = false;
		String wildcardValue = null;

		while (true) {
			pos = skipSpaces(s, pos);
			if (pos >= s.length()) {
				throw new InterpretingException("Unterminated match expression", s);
			}
			if (s.charAt(pos) == '}') {
				pos++; // end of match arms
				break;
			}

			// pattern: integer literal or '_'
			boolean isWildcard = false;
			String pat = null;
			if (s.charAt(pos) == '_') {
				isWildcard = true;
				pos++;
			} else {
				String lit = parseInteger(s, pos);
				if (lit == null) {
					throw new InterpretingException("Expected integer pattern in match arm", s);
				}
				pat = lit;
				pos += lit.length();
			}

			ValueParseResult armVal = consumeArrowAndParseValue(s, pos);
			pos = armVal.nextIndex;
			pos = skipSpaces(s, pos);
			pos = expectCharOrThrow(s, pos, ';');
			pos = skipSpaces(s, pos);

			if (isWildcard) {
				sawWildcard = true;
				wildcardValue = armVal.value;
			} else if (!matched && pat.equals(subj)) {
				matched = true;
				selected = armVal.value;
			}
		}

		if (!matched) {
			if (sawWildcard) {
				selected = wildcardValue;
			} else {
				throw new InterpretingException("Non-exhaustive match and no matching arm", s);
			}
		}
		// pos points after '}'
		return new ValueParseResult(selected, pos);
	}

	private static String parseInteger(String s, int i) {
		int n = s.length();
		if (i >= n)
			return null;
		int j = i;
		while (j < n) {
			char c = s.charAt(j);
			if (c < '0' || c > '9')
				break;
			j++;
		}
		if (j == i)
			return null; // no digits
		return s.substring(i, j);
	}

	// Parses `<ident> = <value>` at position i, only if the identifier matches
	// expectedIdent.
	private static AssignmentParseResult parseAssignmentTo(String s, int i, String expectedIdent) {
		int n = s.length();
		int pos = skipSpaces(s, i);
		// support either `ident = value` or `ident[expr] = value`
		String id = parseIdentifier(s, pos);
		if (id == null || !id.equals(expectedIdent))
			return null;
		pos += id.length();
		pos = skipSpaces(s, pos);
		boolean isIndexed = false;
		int indexExprPos = -1;
		if (pos < n && s.charAt(pos) == '[') {
			isIndexed = true;
			indexExprPos = skipSpaces(s, pos + 1);
			ValueParseResult idxVal = parseValue(s, indexExprPos);
			if (idxVal == null)
				return null;
			int afterIdx = skipSpaces(s, idxVal.nextIndex);
			if (afterIdx >= n || s.charAt(afterIdx) != ']')
				return null;
			pos = afterIdx + 1;
			pos = skipSpaces(s, pos);
		}
		if (pos >= n || s.charAt(pos) != '=')
			return null;
		pos++;
		pos = skipSpaces(s, pos);
		ValueParseResult v = parseValue(s, pos);
		if (v == null)
			return null;
		// If indexed assignment, package the index into a special value format so
		// caller
		// can perform the mutation when applying the assignment. We'll encode as
		// '<ident>[<index>] = <value>' by returning nextIndex as v.nextIndex and
		// embedding the index for the caller to detect via the original input slice.
		if (isIndexed) {
			// Build a combined encoded string: __ASSIGN_INDEX__<ident>|<index>|<value>
			String idxText = s.substring(indexExprPos, idxEndIndex(s, indexExprPos));
			String combined = "__ASSIGN_INDEX__" + id + "|" + idxText + "|" + v.value;
			return new AssignmentParseResult(combined, v.nextIndex);
		}
		return new AssignmentParseResult(v.value, v.nextIndex);
	}

	// Helper to find the end index of a value expression starting at pos (used to
	// extract
	// the index text raw). This mirrors parseValue but returns the raw nextIndex.
	private static int idxEndIndex(String s, int pos) {
		ValueParseResult v = parseValue(s, pos);
		if (v == null)
			throw new InterpretingException("Expected index expression", s);
		return v.nextIndex;
	}

	// Apply an assignment parse result: supports normal assignments (asg.value is
	// the
	// assigned value) and indexed assignments encoded with the prefix
	// '__ASSIGN_INDEX__<ident>|<indexExpr>|<value>'. For indexed assignments this
	// method mutates the encoded array in VAR_ENV (only if the variable was
	// declared mutable in MUTABLE_VARS) and returns the new assigned value.
	private static String applyAssignmentResult(String fullInput, String ident, AssignmentParseResult asg) {
		if (asg == null)
			return null;
		String v = asg.value;
		if (v != null && v.startsWith("__ASSIGN_INDEX__")) {
			String rest = v.substring("__ASSIGN_INDEX__".length());
			int p1 = rest.indexOf('|');
			int p2 = (p1 >= 0) ? rest.indexOf('|', p1 + 1) : -1;
			if (p1 < 0 || p2 < 0)
				throw new InterpretingException("Malformed indexed assignment", fullInput);
			String target = rest.substring(0, p1);
			String idxText = rest.substring(p1 + 1, p2);
			String newVal = rest.substring(p2 + 1);
			if (!target.equals(ident))
				throw new InterpretingException("Assignment target mismatch: expected '" + ident + "'", fullInput);
			Map<String, String> env = VAR_ENV.get();
			if (env == null || !env.containsKey(ident)) {
				throw new InterpretingException("Undefined variable '" + ident + "'", fullInput);
			}
			Set<String> mv = MUTABLE_VARS.get();
			if (mv == null || !mv.contains(ident)) {
				throw new InterpretingException("Cannot mutate immutable variable '" + ident + "'", fullInput);
			}
			String arrStr = env.get(ident);
			if (arrStr == null || !arrStr.startsWith("__ARRAY__")) {
				throw new InterpretingException("Cannot index non-array value", fullInput);
			}
			String inner = arrStr.substring("__ARRAY__".length());
			String[] elems = decodeArrayElemsFromInner(inner, fullInput);
			ValueParseResult idxV = parseValue(idxText, 0);
			if (idxV == null)
				throw new InterpretingException("Expected index expression", fullInput);
			int idx = parseIntStrict(idxV.value, fullInput);
			if (idx < 0 || idx >= elems.length) {
				throw new InterpretingException("Array index out of bounds: " + idx, fullInput);
			}
			elems[idx] = newVal;
			putArrayEncoded(env, ident, elems);
			return newVal;
		} else {
			// simple assignment
			Map<String, String> env = VAR_ENV.get();
			if (env != null)
				env.put(ident, v);
			return v;
		}
	}

	private static int expectCharOrThrow(String input, int i, char expected) {
		if (i >= input.length() || input.charAt(i) != expected) {
			throw new InterpretingException("Expected '" + expected + "'", input);
		}
		return i + 1;
	}

	// Expect the given char at pos, advance past it and skip following spaces.
	private static int expectAndSkip(String s, int pos, char expected) {
		pos = expectCharOrThrow(s, pos, expected);
		return skipSpaces(s, pos);
	}

	// Expect a two-character sequence (like '=>' ), advance after them and skip
	// spaces.
	private static int expectSequenceAndSkip(String s, int pos) {
		pos = expectCharOrThrow(s, pos, '=');
		pos = expectCharOrThrow(s, pos, '>');
		return skipSpaces(s, pos);
	}

	private static ValueParseResult parseBooleanConditionOrThrow(String s, int i) {
		ValueParseResult cond = parseValue(s, i);
		if (cond == null || !("true".equals(cond.value) || "false".equals(cond.value))) {
			throw new InterpretingException("Condition must be boolean", s);
		}
		return cond;
	}

	// Helper: update variable value in current environment
	private static void updateEnv(String name, String value) {
		Map<String, String> env = VAR_ENV.get();
		if (env != null)
			env.put(name, value);
	}

	// Helper used when calling sites already have local name/value variables and
	// need to update the env if present.
	private static void updateEnvIfPresent(Map<String, String> env, String name, String value) {
		if (env != null)
			env.put(name, value);
	}

	private static boolean isBooleanResult(ValueParseResult v) {
		return v != null && ("true".equals(v.value) || "false".equals(v.value));
	}

	private static int consumeSemicolonAndSpaces(String s, int i) {
		i = expectCharOrThrow(s, i, ';');
		return skipSpaces(s, i);
	}

	// Helper: parse a value or throw a uniform error
	private static ValueParseResult requireValue(String s, int pos) {
		ValueParseResult v = parseValue(s, pos);
		if (v == null) {
			throw new InterpretingException("Expected value", s);
		}
		return v;
	}

	// Helper: if there's a comma at pos (after skipping spaces), consume it and
	// following spaces.
	// Returns the advanced index; if no comma, returns the original or
	// spaces-skipped index.
	private static int consumeCommaAndSpaces(String s, int pos) {
		int start = pos;
		pos = skipSpaces(s, pos);
		if (pos < s.length()) {
			char ch = s.charAt(pos);
			if (ch == ',') {
				pos = pos + 1;
				return skipSpaces(s, pos);
			}
		}
		return start;
	}

	private static void ensureNoTrailing(String s, int i) {
		if (i != s.length()) {
			String trailing = "";
			if (i < s.length()) {
				trailing = s.substring(i);
			}
			throw new InterpretingException("Unexpected trailing input", trailing);
		}
	}

	private static String finishWithExpressionOrValue(String input,
			int i,
			String ident,
			String currentVal,
			boolean requireAssigned) {
		System.err.println("[DEBUG] finishWithExpressionOrValue: startIndex=" + i + " remaining='"
				+ (i < input.length() ? input.substring(i) : "") + "'");
		final int n = input.length();
		String result;
		if (i < n && isIdentStart(input.charAt(i))) {
			// Peek: if this is a function call (identifier followed by '('), parse as
			// value.
			int idStart = i;
			String id = parseIdentifier(input, i);
			int afterId = idStart + (id != null ? id.length() : 0);
			afterId = skipSpaces(input, afterId);
			// Also check the direct next character after the identifier (no spaces)
			// to robustly detect postfix forms like `x[1]` when there are no spaces.
			int directPos = idStart + (id != null ? id.length() : 0);
			char directCh = (directPos < n) ? input.charAt(directPos) : '\0';
			// If identifier is followed by a call or any postfix/index/member access,
			// use the full value parser so forms like `x[1]` or `obj.field()` are
			// consumed correctly. Check both the space-skipped next char and the
			// direct next char to avoid missing cases with/without spaces.
			if (id != null && ((afterId < n
					&& (input.charAt(afterId) == '(' || input.charAt(afterId) == '[' || input.charAt(afterId) == '.'))
					|| (directPos < n && (directCh == '(' || directCh == '[' || directCh == '.')))) {
				// function call / postfix
				ValueParseResult v = requireValue(input, i);
				System.err.println("[DEBUG] finishWithExpressionOrValue: parsed id='" + id + "' requireValue.nextIndex="
						+ (v == null ? "<null>" : v.nextIndex));
				i = v.nextIndex;
				result = v.value;
			} else if (id != null && "this".equals(id)) {
				// 'this' should be parsed as a value when used standalone
				ValueParseResult v = requireValue(input, i);
				i = v.nextIndex;
				result = v.value;
			} else {
				String ref2 = parseIdentifier(input, i);
				if (ref2 == null) {
					throw new InterpretingException("Expected identifier or expression", input);
				}
				i += ref2.length();
				// If the final identifier is the same as the declared one, prefer the
				// declared/current value (which may be not yet in the env). Otherwise
				// return the value bound to the referenced identifier from the env.
				Map<String, String> env = VAR_ENV.get();
				if (ref2.equals(ident)) {
					String envVal = (env != null) ? env.get(ident) : null;
					if (requireAssigned && envVal == null && currentVal == null) {
						throw new InterpretingException("Variable '" + ident + "' used before assignment", input);
					}
					result = (envVal != null) ? envVal : currentVal;
				} else {
					String envVal = (env != null) ? env.get(ref2) : null;
					if (envVal == null) {
						throw new InterpretingException("Undefined variable '" + ref2 + "'", input);
					}
					result = envVal;
				}
			}
		} else {
			ValueParseResult v = requireValue(input, i);
			i = v.nextIndex;
			result = v.value;
		}

		// trailing spaces
		// trailing spaces
		// debug: show final index and input length to diagnose off-by-one
		i = skipSpaces(input, i);
		// debug: show final index and input length to diagnose off-by-one
		System.err.println("[DEBUG] finishWithExpressionOrValue: finalIndex=" + i + " inputLen=" + input.length()
				+ " tail='" + (i < input.length() ? input.substring(i) : "<eof>") + "'");
		ensureNoTrailing(input, i);
		return result;
	}

	private static int findMatchingBrace(String s, int openIndex) {
		if (openIndex < 0 || openIndex >= s.length() || s.charAt(openIndex) != '{')
			return -1;
		int depth = 0;
		for (int j = openIndex; j < s.length(); j++) {
			char c = s.charAt(j);
			if (c == '{')
				depth++;
			else if (c == '}') {
				depth--;
				if (depth == 0)
					return j;
			}
		}
		return -1; // no match
	}

	// Parses and consumes a required block: { body } ;? and returns next index
	private static int parseRequiredBlockAndOptionalSemicolon(String s, int pos) {
		int n = s.length();
		if (pos >= n || s.charAt(pos) != '{') {
			throw new InterpretingException("Expected '{' to start block", s);
		}
		int close = findMatchingBrace(s, pos);
		if (close < 0) {
			throw new InterpretingException("Unmatched '{' in block", s);
		}
		pos = close + 1;
		pos = skipSpaces(s, pos);
		// optional semicolon after block for statement separation
		if (pos < n && s.charAt(pos) == ';') {
			pos++;
			pos = skipSpaces(s, pos);
		}
		return skipSpaces(s, pos);
	}

	// Parses a standalone block statement: { ... } ;? and returns next index or -1
	// if not present
	private static int parseBlockStatement(String s, int i) {
		int pos = skipSpaces(s, i);
		if (pos >= s.length() || s.charAt(pos) != '{')
			return -1;
		int close = findMatchingBrace(s, pos);
		if (close < 0)
			return -1;
		// Execute inner content for side effects
		String inner = s.substring(pos + 1, close);
		if (!inner.trim().isEmpty()) {
			execBlockStatements(inner);
		}
		int after = skipSpaces(s, close + 1);
		// If there's a semicolon, always a statement
		if (after < s.length() && s.charAt(after) == ';') {
			return skipSpaces(s, after + 1);
		}
		// No semicolon: if there's more input after the block, treat as a statement;
		// if it's end-of-input, let caller treat it as an expression
		if (after < s.length()) {
			return after;
		}
		return -1;
	}

	// Consumes a boolean condition inside parentheses and returns its boolean value
	// and
	// the index after the closing ')', with spaces skipped.
	private static ValueParseResult consumeParenBooleanCondition(String s, int pos) {
		pos = expectOpenParenAndSkip(s, pos);
		ValueParseResult cond = parseBooleanConditionOrThrow(s, pos);
		pos = cond.nextIndex;
		pos = skipSpaces(s, pos);
		pos = expectCloseParenAndSkip(s, pos);
		return new ValueParseResult(cond.value, pos);
	}

	// Parses a while statement: while (cond) { body } ;? Returns next index or -1
	// if not present.
	private static int parseWhileStatement(String s, int i) {
		int pos = startKeywordPos(s, i, "while");
		if (pos < 0)
			return -1;
		pos = consumeKeywordWithSpace(s, pos, "while");
		pos = skipSpaces(s, pos);
		ValueParseResult condR = consumeParenBooleanCondition(s, pos);
		pos = condR.nextIndex;
		return parseRequiredBlockAndOptionalSemicolon(s, pos);
	}

	// Parses a for statement: for ( init ; cond ; incr ) { body } ;?
	// init: either "let [mut] <id> = <value>" or "<id> = <value>"
	// cond: boolean expression (must evaluate to true/false)
	// incr: "<id> = <value>"
	// Returns next index or -1 if not present.
	private static int parseForStatement(String s, int i) {
		int pos = startKeywordPos(s, i, "for");
		if (pos < 0)
			return -1;
		pos = consumeKeywordWithSpace(s, pos, "for");
		pos = skipSpaces(s, pos);
		pos = expectOpenParenAndSkip(s, pos);

		// init
		if (startsWithWord(s, pos, "let")) {
			pos = consumeKeywordWithSpace(s, pos, "let");
			pos = skipSpaces(s, pos);
			if (startsWithWord(s, pos, "mut")) {
				pos = consumeKeywordWithSpace(s, pos, "mut");
				pos = skipSpaces(s, pos);
			}
			String id = parseIdentifier(s, pos);
			if (id == null)
				throw new InterpretingException("Expected identifier after 'let'", s);
			pos += id.length();
			pos = parseAssignmentAfterKnownIdentifier(s, pos);
		} else {
			// assignment form: <id> = <value>
			int aPos = pos;
			String id = parseIdentifier(s, aPos);
			if (id == null)
				throw new InterpretingException("Expected identifier in for-init assignment", s);
			aPos += id.length();
			pos = parseAssignmentAfterKnownIdentifier(s, aPos);
		}

		pos = consumeOptionalSemicolon(s, pos);

		// condition
		ValueParseResult cond = parseBooleanConditionOrThrow(s, pos);
		pos = skipSpaces(s, cond.nextIndex);
		pos = expectAndSkip(s, pos, ';');

		// increment: <id> = <value>
		String incId = parseIdentifier(s, pos);
		if (incId == null)
			throw new InterpretingException("Expected identifier in for-increment", s);
		pos = parseAssignmentAfterKnownIdentifier(s, pos + incId.length());

		pos = expectCloseParenAndSkip(s, pos);
		return parseRequiredBlockAndOptionalSemicolon(s, pos);
	}

	private static boolean hasKeywordBoundary(String s, int pos, int keywordLen) {
		int after = pos + keywordLen;
		return !(after < s.length() && isIdentPart(s.charAt(after)));
	}

	// Skip spaces and verify the upcoming word matches 'word'. If requireBoundary
	// is
	// true, ensure the word is not a prefix of a longer identifier. Returns the
	// position after skipping spaces (where the word starts) or -1 if it doesn't
	// match.
	private static int startKeywordPos(String s, int i, String word) {
		int pos = skipSpaces(s, i);
		if (!startsWithWord(s, pos, word))
			return -1;
		if (!hasKeywordBoundary(s, pos, word.length()))
			return -1;
		return pos;
	}

	private static boolean isNextCharAfterSkip(String s, int pos, char ch) {
		pos = skipSpaces(s, pos);
		return (pos < s.length() && s.charAt(pos) == ch);
	}

	// (removed unused helper parseReturnTypeAfterParamList)

	private static int expectOpenParenAndSkip(String s, int pos) {
		return expectAndSkip(s, pos, '(');
	}

	private static int expectCloseParenAndSkip(String s, int pos) {
		return expectAndSkip(s, pos, ')');
	}

	// Parses a function declaration statement:
	// fn <id> ( [<id> : <Type> [, ...]] ) : <Type> => <value> ;
	// Returns next index or -1 if not present.
	private static int parseFunctionDeclStatement(String s, int i) {
		int pos = startKeywordPos(s, i, "fn");
		if (pos < 0)
			return -1;
		// consume keyword and function name, and capture name text for registry
		int nameStartPos = consumeKeywordWithSpace(s, pos, "fn");
		nameStartPos = skipSpaces(s, nameStartPos);
		String fnName = parseIdentifier(s, nameStartPos);
		if (fnName == null)
			throw new InterpretingException("Expected function name after 'fn'", s);
		pos = nameStartPos + fnName.length();
		pos = skipSpaces(s, pos);
		pos = expectCharOrThrow(s, pos, '(');
		ArrayList<String> paramNames = new ArrayList<>();
		pos = parseOptionalNameTypeList(s, pos, ')', paramNames);
		// ensure no duplicate parameter names
		HashSet<String> seen = new HashSet<>();
		for (String p : paramNames) {
			if (!seen.add(p)) {
				throw new InterpretingException("Duplicate parameter name '" + p + "' in function '" + fnName + "'", s);
			}
		}
		// After the parameter list we may have an optional return type `: Type`.
		// parseOptionalNameTypeList left us with the position of the closing
		// parenthesis (not consumed). Consume ')' first, then check for ':'
		pos = expectAndSkip(s, pos, ')');
		// optional return type
		if (pos < s.length() && s.charAt(pos) == ':') {
			pos = expectAndSkip(s, pos, ':');
			String retType = parseIdentifier(s, pos);
			if (retType == null) {
				throw new InterpretingException("Expected return type after ':'", s);
			}
			pos += retType.length();
			pos = skipSpaces(s, pos);
		}

		// Consume the '=>' arrow and then capture the function body source
		// without evaluating it. This avoids executing blocks at declaration
		// time (which would evaluate 'this' too early). The body is either a
		// brace-delimited block or a single value expression terminated by the
		// following ';'.
		pos = skipSpaces(s, pos);
		pos = expectSequenceAndSkip(s, pos);
		String bodySrc;
		if (pos < s.length() && s.charAt(pos) == '{') {
			int close = findMatchingBrace(s, pos);
			if (close < 0) {
				throw new InterpretingException("Unmatched '{' in function body", s);
			}
			bodySrc = s.substring(pos, close + 1);
			pos = close + 1;
		} else {
			// scan until the top-level semicolon (skip nested parens/braces)
			int depthPar = 0;
			int depthBr = 0;
			int j = pos;
			while (j < s.length()) {
				char c = s.charAt(j);
				if (c == '(')
					depthPar++;
				else if (c == ')')
					depthPar--;
				else if (c == '{')
					depthBr++;
				else if (c == '}')
					depthBr--;
				else if (c == ';' && depthPar == 0 && depthBr == 0) {
					break;
				}
				j++;
			}
			if (j >= s.length()) {
				throw new InterpretingException("Expected ';' after function body", s);
			}
			bodySrc = s.substring(pos, j);
			pos = j;
		}

		pos = consumeOptionalSemicolon(s, pos);
		// register function; error on duplicate name
		Map<String, FunctionInfo> reg = FUNC_REG.get();
		if (reg.containsKey(fnName)) {
			throw new InterpretingException("Function '" + fnName + "' already defined", s);
		}
		reg.put(fnName, new FunctionInfo(paramNames, bodySrc));
		// Also bind the function name into the current variable environment so nested
		// functions are visible via 'this' when returning the env object.
		Map<String, String> env = VAR_ENV.get();
		if (env != null) {
			// store the function name as its own string so it can be resolved from an
			// object produced by makeObjectFromEnv
			env.put(fnName, fnName);
		}
		// debug trace
		System.err.println("[DEBUG] declared fn '" + fnName + "' bodySrc=[" + bodySrc + "] params=" + paramNames);
		return skipSpaces(s, pos);
	}

	// Consumes '=>' followed by a value; returns the parsed value and next index.
	private static ValueParseResult consumeArrowAndParseValue(String s, int pos) {
		pos = skipSpaces(s, pos);
		pos = expectSequenceAndSkip(s, pos);
		return requireValue(s, pos);
	}

	// Overload with optional outNames: collects the names if provided.
	private static int parseOptionalNameTypeList(String s, int pos, char terminator, List<String> outNames) {
		pos = skipSpaces(s, pos);
		// empty list
		if (pos < s.length() && s.charAt(pos) == terminator) {
			return pos;
		}
		// non-empty list
		while (pos < s.length()) {
			String n = parseIdentifier(s, pos);
			if (n == null)
				throw new InterpretingException("Expected identifier", s);
			if (outNames != null)
				outNames.add(n);
			pos += n.length();
			pos = skipSpaces(s, pos);
			pos = expectCharOrThrow(s, pos, ':');
			pos = skipSpaces(s, pos);
			String t = parseIdentifier(s, pos);
			if (t == null)
				throw new InterpretingException("Expected type identifier after ':'", s);
			pos += t.length();
			int next = consumeCommaAndSpaces(s, pos);
			if (next != pos) {
				pos = next;
				// require another pair after comma; a terminator here means trailing comma =>
				// invalid
				if (isNextCharAfterSkip(s, pos, terminator)) {
					throw new InterpretingException("Trailing comma not allowed", s);
				}
				continue;
			}
			break;
		}
		return skipSpaces(s, pos);
	}

	// Parses a struct declaration statement:
	// struct <id> { [<field> : <Type> [, ...]] } ;
	// Returns next index or -1 if not present.
	private static int parseStructDeclStatement(String s, int i) {
		int pos = startKeywordPos(s, i, "struct");
		if (pos < 0)
			return -1;
		// consume 'struct' and capture the struct name for uniqueness checks
		pos = consumeKeywordWithSpace(s, pos, "struct");
		pos = skipSpaces(s, pos);
		String structName = parseIdentifier(s, pos);
		if (structName == null) {
			throw new InterpretingException("Expected struct name after 'struct'", s);
		}
		pos += structName.length();
		pos = skipSpaces(s, pos);
		// enforce unique struct names per run
		Set<String> sreg = STRUCT_REG.get();
		if (sreg.contains(structName)) {
			throw new InterpretingException("Struct '" + structName + "' already defined", s);
		}
		sreg.add(structName);
		pos = expectCharOrThrow(s, pos, '{');
		ArrayList<String> fieldNames = new ArrayList<>();
		pos = parseOptionalNameTypeList(s, pos, '}', fieldNames);
		// check for duplicate field names
		HashSet<String> seen = new HashSet<>();
		for (String f : fieldNames) {
			if (!seen.add(f)) {
				throw new InterpretingException("Duplicate field name '" + f + "' in struct '" + structName + "'", s);
			}
		}
		pos = expectCharOrThrow(s, pos, '}');
		pos = skipSpaces(s, pos);
		pos = expectAndSkip(s, pos, ';');
		return pos;
	}

	// (removed helper skipCommaAndSpaces)

	// After an identifier has been consumed, parse an assignment '=' <value> and
	// return next index
	private static int parseAssignmentAfterKnownIdentifier(String s, int pos) {
		pos = skipSpaces(s, pos);
		pos = expectCharOrThrow(s, pos, '=');
		pos = skipSpaces(s, pos);
		ValueParseResult v = parseValue(s, pos);
		if (v == null)
			throw new InterpretingException("Expected value after '='", s);
		return v.nextIndex;
	}

	// Parses a function call expression starting at the identifier of the name.
	// Returns value of the function body (currently ignoring params) and index
	// after ')'.
	private static ValueParseResult parseFunctionCallExpression(String s, int identStart) {
		String name = parseIdentifier(s, identStart);
		if (name == null) {
			throw new InterpretingException("Expected function name", s);
		}
		int pos = identStart + name.length();
		pos = skipSpaces(s, pos);
		pos = expectOpenParenAndSkip(s, pos);
		ArgParseResult apr = parseArgumentList(s, pos);
		int argc = apr.args.size();
		pos = apr.nextIndex;
		Map<String, FunctionInfo> reg = FUNC_REG.get();
		FunctionInfo fi = (reg != null) ? reg.get(name) : null;
		if (fi == null) {
			throw new InterpretingException("Undefined function '" + name + "'", s);
		}
		if (fi.paramNames.size() != argc) {
			throw new InterpretingException(
					"Wrong number of arguments for '" + name + "' (expected " + fi.paramNames.size() + ", got " + argc + ")", s);
		}
		// Evaluate function body in a child environment with parameters bound to
		// argument values.
		// Use the previously parsed argument list values
		String result = executeFunction(fi, new ArrayList<>(apr.args));
		return new ValueParseResult(result, pos);
	}

	public String interpret(String input) throws InterpretingException {
		return internalEval(input, true);
	}
}
