package magma.interpret;

import magma.Err;
import magma.Ok;
import magma.Result;
import java.util.Optional;

public class Interpreter {

	/**
	 * Small generic tuple used instead of AbstractMap.SimpleEntry to avoid
	 * duplication and make code more expressive.
	 */
	private static record Tuple2<A, B>(A first, B second) {
	}

	private Optional<Result<String, InterpretError>> tryFinalLiteralInDeclarations(String finalPart) {
		String finalTrim = finalPart.trim();
		int leadDigits = (finalTrim.isEmpty() ? 0 : leadingDigits(finalTrim));
		if (leadDigits > 0)
			return Optional.of(new Ok<>(finalTrim.substring(0, leadDigits)));
		return Optional.empty();
	}

	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		String trimmed = input.trim();
		// block literal: { ... }
		if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
			String inner = trimmed.substring(1, trimmed.length() - 1).trim();
			return interpret(inner);
		}

		Optional<Result<String, InterpretError>> ifRes = handleIfExpression(trimmed);
		if (ifRes.isPresent())
			return ifRes.get();
		// try let-binding like "let x : I32 = 10; x"
		Optional<Result<String, InterpretError>> declRes = tryParseDeclarations(trimmed);
		if (declRes.isPresent())
			return declRes.get();
		// try simple addition like "2 + 3" (with optional spaces)
		Optional<Result<String, InterpretError>> addRes = tryParseBinary(input, '+');
		if (addRes.isPresent())
			return addRes.get();
		Optional<Result<String, InterpretError>> subRes = tryParseBinary(input, '-');
		if (subRes.isPresent())
			return subRes.get();
		Optional<Result<String, InterpretError>> mulRes = tryParseBinary(input, '*');
		if (mulRes.isPresent())
			return mulRes.get();

		// integer literal (decimal)
		// boolean literals
		if (trimmed.equals("true") || trimmed.equals("false"))
			return new Ok<>(trimmed);

		// accept a leading decimal integer even if followed by other characters,
		// e.g. "5I32" should be interpreted as the integer literal "5".
		int i = 0;
		while (i < trimmed.length() && Character.isDigit(trimmed.charAt(i)))
			i++;
		if (i > 0)
			return new Ok<>(trimmed.substring(0, i));
		return new Err<>(new InterpretError("Unbound identifier: " + trimmed));
	}

	private Optional<Result<String, InterpretError>> tryParseBinary(String input, char op) {
		String trimmed = input.trim();
		java.util.List<String> parts = splitByOp(trimmed, op);

		if (parts.size() > 1) {
			if (op == '+')
				return handleAdditionChain(parts, input);
			// if there are exactly two parts (single binary occurrence), handle it
			if (parts.size() != 2)
				return Optional.empty();
			// else fall through to single-binary handling
		}

		return handleSingleBinary(trimmed, op, input);
	}

	private static java.util.List<String> splitByOp(String s, char op) {
		return splitRaw(s, op);
	}

	private Optional<Result<String, InterpretError>> handleAdditionChain(java.util.List<String> parts, String input) {
		long sum = 0L;
		Optional<Character> firstSuffixKind = Optional.empty();
		for (String rawPart : parts) {
			String part = rawPart.trim();
			if (part.isEmpty())
				return Optional.empty();
			int ld = leadingDigits(part);
			if (ld == 0)
				return Optional.empty();
			try {
				long v = Long.parseLong(part.substring(0, ld));
				String suffix = part.substring(ld).trim();
				if (hasSuffix(suffix)) {
					char kind = Character.toUpperCase(suffix.charAt(0));
					if (firstSuffixKind.isEmpty())
						firstSuffixKind = Optional.of(kind);
					else if (isSignednessChar(firstSuffixKind.get()) && isSignednessChar(kind) && firstSuffixKind.get() != kind)
						return Optional.of(new Err<>(new InterpretError("Signedness mismatch in addition: " + input)));
				}
				sum = Math.addExact(sum, v);
			} catch (NumberFormatException | ArithmeticException e) {
				return Optional.empty();
			}
		}
		return Optional.of(new Ok<>(Long.toString(sum)));
	}

	private Optional<Result<String, InterpretError>> handleSingleBinary(String trimmed, char op, String input) {
		int idx = trimmed.indexOf(op);
		if (idx < 0)
			return Optional.empty();
		String left = trimmed.substring(0, idx).trim();
		String right = trimmed.substring(idx + 1).trim();
		if (left.isEmpty() || right.isEmpty())
			return Optional.empty();

		int li = leadingDigits(left);
		if (li == 0)
			return Optional.empty();
		int ri = leadingDigits(right);
		if (ri == 0)
			return Optional.empty();

		try {
			long a = Long.parseLong(left.substring(0, li));
			long b = Long.parseLong(right.substring(0, ri));
			String leftSuffix = left.substring(li).trim();
			String rightSuffix = right.substring(ri).trim();
			if (hasSuffix(leftSuffix) && hasSuffix(rightSuffix) && isMixedSignedness(leftSuffix, rightSuffix))
				return Optional.of(new Err<>(new InterpretError("Signedness mismatch in operation: " + input)));
			long res;
			switch (op) {
				case '+':
					res = a + b;
					break;
				case '-':
					res = a - b;
					break;
				case '*':
					res = a * b;
					break;
				default:
					return Optional.empty();
			}
			return Optional.of(new Ok<>(Long.toString(res)));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	private static boolean isSignednessChar(char c) {
		char u = Character.toUpperCase(c);
		return u == 'U' || u == 'I';
	}

	private static int leadingDigits(String s) {
		int i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i)))
			i++;
		return i;
	}

	private static boolean hasSuffix(String s) {
		return !s.isEmpty();
	}

	private static boolean isMixedSignedness(String a, String b) {
		char la = Character.toUpperCase(a.charAt(0));
		char lb = Character.toUpperCase(b.charAt(0));
		return (la == 'U' && lb == 'I') || (la == 'I' && lb == 'U');
	}

	private Optional<Result<String, InterpretError>> tryParseLet(String input) {
		String s = input.trim();
		if (!s.startsWith("let "))
			return Optional.empty();
		java.util.List<String> stmts = splitStatements(s);
		if (stmts.isEmpty())
			return Optional.empty();
		if (stmts.size() == 1)
			return handleSingleLet(stmts.get(0));
		return handleMultipleLets(stmts);
	}

	private Optional<Result<String, InterpretError>> tryParseDeclarations(String input) {
		String s = input.trim();
		if (!s.startsWith("let ") && !s.startsWith("fn ") && !s.startsWith("class ") && !s.startsWith("struct "))
			return Optional.empty();
		java.util.List<String> stmts = splitStatements(s);
		// Try to split a leading declaration and remainder when present. This
		// handles both the single-statement case and cases where the first
		// semicolon-separated part still contains a declaration + trailing
		// expression (for example: "struct S { ... } let x = ...; x").
		Optional<java.util.List<String>> maybe = attemptSplitDeclarationAndRemainder(s, stmts);
		if (maybe.isPresent()) {
			java.util.List<String> parts = maybe.get();
			// parts is expected to be [declaration, remainder]. Split the remainder
			// into top-level statements and reconstruct the statements list so
			// declarations come first and the final expression(s) follow.
			java.util.List<String> rebuilt = new java.util.ArrayList<>();
			rebuilt.add(parts.get(0));
			java.util.List<String> tail = splitStatements(parts.get(1));
			rebuilt.addAll(tail);
			stmts = rebuilt;
		}
		if (stmts.isEmpty())
			return Optional.empty();
		// If there is any fn or class declaration present, handle as declarations;
		// else fall back to let
		boolean hasDecl = stmts.stream()
				.anyMatch(p -> p.startsWith("fn ") || p.startsWith("class ") || p.startsWith("struct "));
		if (!hasDecl)
			return tryParseLet(input);
		return handleFunctionDeclarations(stmts);
	}

	/**
	 * Helper that centralizes the declaration + trailing-expression splitting
	 * logic. Returns optionally a new statements list when a split was found.
	 */
	private Optional<java.util.List<String>> attemptSplitDeclarationAndRemainder(String s,
			java.util.List<String> original) {
		Optional<java.util.List<String>> maybe = splitFirstDeclarationAndRemainder(s);
		if (maybe.isPresent())
			return maybe;
		// try to split after a top-level brace
		if (original.size() == 1) {
			int braceIdx = findTopLevelIndex(s, 0, i -> s.charAt(i) == '{');
			if (braceIdx >= 0) {
				Optional<java.util.List<String>> alt = splitAfterTopLevelBrace(s, braceIdx);
				if (alt.isPresent())
					return alt;
			}
		}
		return Optional.empty();
	}

	private Optional<Result<String, InterpretError>> handleFunctionDeclarations(java.util.List<String> stmts) {
		String finalPart = stmts.get(stmts.size() - 1);
		Optional<Result<String, InterpretError>> maybeLit = tryFinalLiteralInDeclarations(finalPart);
		if (maybeLit.isPresent())
			return maybeLit;
		// final part may be a numeric literal (handled), a single identifier
		// (return that binding), a function call like name(arg), or a
		// constructor.field like Name(arg).field. If it's a single identifier,
		// proceed to collect functions/lets so it can be resolved later.
		Optional<FinalParse> finalParseOpt = parseFinalPart(finalPart);
		if (finalParseOpt.isEmpty())
			return Optional.empty();
	FinalParse finalParse = finalParseOpt.get();
	Optional<Tuple2<String, String>> callOpt = finalParse.callOpt();
	Optional<Tuple2<Tuple2<String, String>, String>> ctorOpt = finalParse.ctorOpt();

		Optional<FnCollectResult> coll = collectFunctions(stmts);
		if (coll.isEmpty())
			return Optional.empty();
		FnCollectResult collRes = coll.get();
		if (collRes.error().isPresent())
			return Optional.of(new Err<>(collRes.error().get()));
		java.util.Map<String, Tuple2<String, String>> fns = collRes.fns();

		// If there are any let statements among the leading statements, handle
		// them as a let-block so that a trailing identifier (like `x`) can be
		// resolved from the let environment. Build a list of the let stmts plus
		// the final part and delegate to existing let handling.
		boolean hasLet = stmts.subList(0, stmts.size() - 1).stream().anyMatch(p -> p.startsWith("let "));
		if (hasLet) {
			// collect only the leading let statements
			java.util.List<String> lets = new java.util.ArrayList<>();
			for (int i = 0; i < stmts.size() - 1; i++) {
				if (stmts.get(i).startsWith("let "))
					lets.add(stmts.get(i));
			}
			// If the final part is an identifier or a block, we can reuse existing
			// multiple-let handling which reconstructs a let environment and
			// evaluates the final expression.
			if (isSingleIdentifier(finalPart) || (finalPart.startsWith("{") && finalPart.endsWith("}"))) {
				lets.add(finalPart);
				return handleMultipleLets(lets);
			}
			// Otherwise (final part is a call or constructor.field), validate the
			// let statements for correctness, but then dispatch to the normal
			// function/constructor handling using the previously collected fns.
			java.util.Map<String, Tuple2<Boolean, String>> env = new java.util.LinkedHashMap<>();
			for (String l : lets) {
				java.util.Optional<java.util.Optional<InterpretError>> perr = processLetStmt(env, l);
				if (perr.isEmpty())
					return Optional.empty();
				if (perr.get().isPresent())
					return Optional.of(new Err<>(perr.get().get()));
			}
			// validated lets; fall through to function/constructor dispatch below
		}

		// Delegate final dispatch (constructor.field vs normal call) to helper
		return dispatchAfterCollection(new DispatchContext(ctorOpt, callOpt, fns, finalPart));
	}

	private static record DispatchContext(
			Optional<Tuple2<Tuple2<String, String>, String>> ctorOpt,
			Optional<Tuple2<String, String>> callOpt,
			java.util.Map<String, Tuple2<String, String>> fns,
			String finalPart) {
	}

	private static record FinalParse(Optional<Tuple2<String, String>> callOpt,
			Optional<Tuple2<Tuple2<String, String>, String>> ctorOpt, boolean finalIsIdentifier) {
	}

	private Optional<FinalParse> parseFinalPart(String finalPart) {
		Optional<Tuple2<String, String>> callOpt = Optional.empty();
		Optional<Tuple2<Tuple2<String, String>, String>> ctorOpt = Optional.empty();
		boolean finalIsIdentifier = isSingleIdentifier(finalPart);
		if (!finalIsIdentifier) {
			callOpt = parseFunctionCall(finalPart);
			if (callOpt.isEmpty()) {
				ctorOpt = parseConstructorFieldPattern(finalPart);
				if (ctorOpt.isEmpty())
					return Optional.empty();
			}
		}
		return Optional.of(new FinalParse(callOpt, ctorOpt, finalIsIdentifier));
	}

	private Optional<Result<String, InterpretError>> dispatchAfterCollection(DispatchContext ctx) {
		Optional<Tuple2<Tuple2<String, String>, String>> ctorOpt = ctx.ctorOpt();
		Optional<Tuple2<String, String>> callOpt = ctx.callOpt();
		java.util.Map<String, Tuple2<String, String>> fns = ctx.fns();
		String finalPart = ctx.finalPart();
		if (ctorOpt.isPresent())
			return handleConstructorOrFieldCall(ctorOpt.get(), fns);
		if (callOpt.isPresent())
			return handleNormalFunctionCall(callOpt.get(), fns);
		return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + finalPart)));
	}

	private Optional<Result<String, InterpretError>> handleConstructorOrFieldCall(
			Tuple2<Tuple2<String, String>, String> ctorCall, java.util.Map<String, Tuple2<String, String>> fns) {
		Tuple2<String, String> starter = extractCtorCallParts(ctorCall);
		String callName = starter.first();
		String callArg = starter.second();
		String methodOrField = ctorCall.second();

		boolean hasClass = fns.containsKey("CLASS:" + callName);
		boolean hasStruct = fns.containsKey("STRUCT:" + callName);
		if (!hasClass && !hasStruct)
			return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + callName)));

		// try method resolution first
		Optional<Result<String, InterpretError>> maybeMethod = tryResolveInnerMethod(methodOrField, fns);
		if (maybeMethod.isPresent())
			return maybeMethod;

		if (hasStruct)
			return handleStructConstructorField(new Tuple2<>(new Tuple2<>(callName, callArg), methodOrField), fns);

		if (hasClass)
			return handleClassConstructorField(callArg, methodOrField, fns);

		return Optional.of(new Err<>(new InterpretError("Unsupported constructor arg: " + callArg)));
	}

	private Tuple2<String, String> extractCtorCallParts(Tuple2<Tuple2<String, String>, String> ctorCall) {
		Tuple2<String, String> call = ctorCall.first();
		return new Tuple2<>(call.first(), call.second());
	}

	private Optional<Result<String, InterpretError>> tryResolveInnerMethod(String methodOrField,
			java.util.Map<String, Tuple2<String, String>> fns) {
		if (!fns.containsKey(methodOrField))
			return Optional.empty();
		Tuple2<String, String> innerFn = fns.get(methodOrField);
		String innerParam = innerFn.first();
		String innerRet = innerFn.second();
		if (innerParam.isEmpty() && !innerRet.isEmpty() && Character.isDigit(innerRet.charAt(0)))
			return Optional.of(new Ok<>(innerRet));
		return Optional.empty();
	}

	private Optional<Result<String, InterpretError>> handleStructConstructorField(
			Tuple2<Tuple2<String, String>, String> ctorCall, java.util.Map<String, Tuple2<String, String>> fns) {
		Tuple2<String, String> call = ctorCall.first();
		String callName = call.first();
		String callArg = call.second();
		String methodOrField = ctorCall.second();
		Tuple2<String, String> st = fns.get("STRUCT:" + callName);
		String fieldName = st.first();
		if (callArg.isEmpty())
			return Optional.of(new Err<>(new InterpretError("Unsupported constructor arg: " + callArg)));
		if (methodOrField.equals(fieldName))
			return Optional.of(new Ok<>(callArg));
		return Optional.of(new Err<>(new InterpretError("Unknown field: " + methodOrField)));
	}

	private Optional<Result<String, InterpretError>> handleClassConstructorField(String callArg, String methodOrField,
			java.util.Map<String, Tuple2<String, String>> fns) {
		// For classes: fall back to field access (allow empty arg)
		if (!callArg.isEmpty())
			return Optional.of(new Ok<>(callArg));
		if (callArg.isEmpty())
			return Optional.of(new Ok<>(""));
		return Optional.of(new Err<>(new InterpretError("Unsupported constructor arg: " + callArg)));
	}

	private Optional<Result<String, InterpretError>> handleNormalFunctionCall(Tuple2<String, String> call,
			java.util.Map<String, Tuple2<String, String>> fns) {
		String callName = call.first();
		String callArg = call.second();
		Tuple2<String, String> fn = fns.get(callName);
		String paramName = fn.first();
		String retExpr = fn.second();
		// delegate evaluation cases
		if (paramName.isEmpty())
			return evaluateZeroArgFunctionBody(fns, retExpr);
		if (retExpr.equals(paramName))
			return callArg.isEmpty() ? Optional.of(new Err<>(new InterpretError("Missing argument for call: " + callName)))
					: Optional.of(new Ok<>(callArg));
		return evaluateReturnExprAsOptional(fns, retExpr);
	}

	// Handlers extracted to reduce cyclomatic complexity
	private java.util.Optional<java.util.Optional<InterpretError>> handleFnStmt(String stmt,
			java.util.Map<String, Tuple2<String, String>> fns) {
		Optional<Tuple2<String, Tuple2<String, String>>> fnOpt = parseFnPart(stmt);
		if (fnOpt.isEmpty())
			return java.util.Optional.empty();
		Tuple2<String, Tuple2<String, String>> fn = fnOpt.get();
		String name = fn.first();
		if (fns.containsKey(name))
			return java.util.Optional
					.of(java.util.Optional.of(new InterpretError("Duplicate function declaration: " + name)));
		fns.put(name, fn.second());
		return java.util.Optional.of(java.util.Optional.empty());
	}

	private java.util.Optional<java.util.Optional<InterpretError>> handleClassStmt(String stmt,
			java.util.Map<String, Tuple2<String, String>> fns,
			java.util.Map<String, Tuple2<String, String>> classes) {
		Optional<Tuple2<String, Tuple2<String, String>>> clsOpt = parseClassPart(stmt);
		if (clsOpt.isEmpty())
			return java.util.Optional.empty();
		Tuple2<String, Tuple2<String, String>> cls = clsOpt.get();
		String name = cls.first();
		if (classes.containsKey(name))
			return java.util.Optional.of(java.util.Optional.of(new InterpretError("Duplicate class declaration: " + name)));
		classes.put(name, cls.second());

		// Parse inner functions from class body if present
		String classBody = cls.second().second();
		if (classBody.startsWith("fn ")) {
			// Remove trailing semicolon if present before parsing
			String fnDecl = classBody.endsWith(";") ? classBody.substring(0, classBody.length() - 1) : classBody;
			Optional<Tuple2<String, Tuple2<String, String>>> innerFnOpt = parseFnPart(fnDecl);
			if (innerFnOpt.isPresent()) {
				Tuple2<String, Tuple2<String, String>> innerFn = innerFnOpt.get();
				String innerName = innerFn.first();
				if (!fns.containsKey(innerName))
					fns.put(innerName, innerFn.second());
			}
		}
		return java.util.Optional.of(java.util.Optional.empty());
	}

	private java.util.Optional<java.util.Optional<InterpretError>> handleStructStmt(String stmt,
			java.util.Map<String, Tuple2<String, String>> structs) {
		Optional<Tuple2<String, Tuple2<String, String>>> stOpt = parseStructPart(stmt);
		if (stOpt.isEmpty())
			return java.util.Optional.empty();
		Tuple2<String, Tuple2<String, String>> st = stOpt.get();
		String name = st.first();
		if (structs.containsKey(name))
			return java.util.Optional.of(java.util.Optional.of(new InterpretError("Duplicate struct declaration: " + name)));
		structs.put(name, st.second());
		return java.util.Optional.of(java.util.Optional.empty());
	}

	private boolean handleLetStmt(String stmt) {
		Optional<Tuple2<Tuple2<Boolean, String>, String>> kv = parseLetPart(stmt);
		return kv.isPresent();
	}

	/**
	 * Parse a constructor.field pattern like Name(arg).field and return
	 * Optional.of(( (Name,arg), fieldName )) or Optional.empty() if malformed.
	 * Also handles constructor.method() pattern like Empty().get().
	 */
	private Optional<Tuple2<Tuple2<String, String>, String>> parseConstructorFieldPattern(String finalPart) {
		int dot = finalPart.indexOf('.');
		if (dot <= 0)
			return Optional.empty();
		String left = finalPart.substring(0, dot).trim();
		int p = parseIdentifierEnd(left, 0);
		if (p <= 0)
			return Optional.empty();
		Optional<Tuple2<String, String>> leftCall = parseCallOrStructLeft(left, p);
		if (leftCall.isEmpty())
			return Optional.empty();
		String rightPart = finalPart.substring(dot + 1).trim();
		// Handle method call pattern like get()
		if (rightPart.endsWith(")")) {
			int parenIdx = rightPart.indexOf('(');
			if (parenIdx > 0) {
				String methodName = rightPart.substring(0, parenIdx).trim();
				return Optional.of(new Tuple2<>(leftCall.get(), methodName));
			}
		}
		String ctorField = rightPart;
		return Optional.of(new Tuple2<>(leftCall.get(), ctorField));
	}

	private Optional<Tuple2<String, String>> parseCallOrStructLeft(String left, int idEnd) {
		int ws = skipWhitespace(left, idEnd);
		if (ws < left.length() && left.charAt(ws) == '(') {
			return parseFunctionCall(left);
		}
		if (ws < left.length() && left.charAt(ws) == '{') {
			int start = skipWhitespace(left, ws + 1);
			if (start >= left.length())
				return Optional.empty();
			Optional<Tuple2<String, Integer>> arg = parseTokenArgAndExpectClosing(left, start, '}');
			if (arg.isEmpty())
				return Optional.empty();
			return Optional.of(new Tuple2<>(left.substring(0, idEnd), arg.get().first()));
		}
		return Optional.empty();
	}

	private Optional<Tuple2<String, String>> parseFunctionCall(String finalPart) {
		int p = parseIdentifierEnd(finalPart, 0);
		if (p < 0)
			return Optional.empty();
		String callName = finalPart.substring(0, p);
		p = skipWhitespace(finalPart, p);
		if (p >= finalPart.length() || finalPart.charAt(p) != '(')
			return Optional.empty();
		p = skipWhitespace(finalPart, p + 1);
		String callArg = "";
		if (p < finalPart.length() && finalPart.charAt(p) != ')') {
			Optional<Tuple2<String, Integer>> arg = parseTokenArgAndExpectClosing(finalPart, p, ')');
			if (arg.isEmpty())
				return Optional.empty();
			callArg = arg.get().first();
			p = skipWhitespace(finalPart, arg.get().second() + 1);
		} else {
			// no-arg form: advance past ')'
			p = skipWhitespace(finalPart, p + 1);
		}
		if (p != finalPart.length())
			return Optional.empty();
		return Optional.of(new Tuple2<>(callName, callArg));
	}

	/**
	 * Helper result for collectFunctions to avoid exposing wildcard generics.
	 */
	private static record FnCollectResult(java.util.Map<String, Tuple2<String, String>> fns,
			java.util.Optional<InterpretError> error) {
	}

	private Optional<FnCollectResult> collectFunctions(java.util.List<String> stmts) {
		java.util.Map<String, Tuple2<String, String>> fns = new java.util.HashMap<>();
		java.util.Map<String, Tuple2<String, String>> classes = new java.util.HashMap<>();
		java.util.Map<String, Tuple2<String, String>> structs = new java.util.HashMap<>();
		for (int i = 0; i < stmts.size() - 1; i++) {
			String stmt = stmts.get(i);
			// Map of statement prefix -> handler function. Handlers capture
			// local fns/classes/structs maps as needed.
			java.util.Map<String, java.util.function.Function<String, java.util.Optional<java.util.Optional<InterpretError>>>> handlers = new java.util.LinkedHashMap<>();
			handlers.put("fn ", s -> handleFnStmt(s, fns));
			handlers.put("class ", s -> handleClassStmt(s, fns, classes));
			handlers.put("struct ", s -> handleStructStmt(s, structs));

			boolean matched = false;
			for (java.util.Map.Entry<String, java.util.function.Function<String, java.util.Optional<java.util.Optional<InterpretError>>>> e : handlers
					.entrySet()) {
				String prefix = e.getKey();
				if (stmt.startsWith(prefix)) {
					java.util.Optional<java.util.Optional<InterpretError>> perr = e.getValue().apply(stmt);
					if (perr.isEmpty())
						return Optional.empty();
					if (perr.get().isPresent())
						return Optional.of(new FnCollectResult(java.util.Collections.emptyMap(), perr.get()));
					matched = true;
					break;
				}
			}
			if (matched)
				continue;
			if (stmt.startsWith("let ")) {
				if (!handleLetStmt(stmt))
					return Optional.empty();
				continue;
			}
			return Optional.empty();
		}
		// encode collected classes and structs into function map for later lookup
		// prefix class entries with "CLASS:" and struct entries with "STRUCT:"
		for (var e : classes.entrySet())
			fns.put("CLASS:" + e.getKey(), e.getValue());
		for (var e : structs.entrySet())
			fns.put("STRUCT:" + e.getKey(), e.getValue());
		return Optional.of(new FnCollectResult(fns, java.util.Optional.empty()));
	}

	private Optional<Result<String, InterpretError>> evaluateZeroArgFunctionBody(
			java.util.Map<String, Tuple2<String, String>> fns,
			String retExpr) {
		return evaluateReturnExprAsOptional(fns, retExpr);
	}

	private Optional<Result<String, InterpretError>> evaluateReturnExprAsOptional(
			java.util.Map<String, Tuple2<String, String>> fns, String retExpr) {
		if (retExpr.startsWith("CALL:")) {
			String payload = retExpr.substring(5);
			int comma = payload.indexOf(',');
			if (comma < 0) {
				return Optional.of(resolveFunctionValue(fns, payload, new java.util.HashSet<>()));
			} else {
				// pass the raw payload (name,arg) to helper to avoid too many params
				return Optional.of(resolveFunctionWithArg(fns, payload, new java.util.HashSet<>()));
			}
		}
		if (!retExpr.isEmpty() && Character.isDigit(retExpr.charAt(0)))
			return Optional.of(new Ok<>(retExpr));
		return Optional.of(new Err<>(new InterpretError("Unsupported function body: " + retExpr)));
	}

	private Result<String, InterpretError> resolveFunctionWithArg(java.util.Map<String, Tuple2<String, String>> fns,
			String payload, java.util.Set<String> visited) {
		Tuple2<String, String> nameArg = splitNameArg(payload);
		String name = nameArg.first();
		String arg = nameArg.second();
		Result<Tuple2<String, String>, InterpretError> fetched = precheckAndGetFn(
				new PrecheckContext(fns, name, visited, true, arg));
		if (fetched instanceof Err<Tuple2<String, String>, InterpretError> e)
			return new Err<>(e.error());
		if (!(fetched instanceof Ok<Tuple2<String, String>, InterpretError> ok))
			return new Err<>(new InterpretError("Unexpected result while resolving function: " + name));
		Tuple2<String, String> fn = ok.value();
		String param = fn.first();
		String ret = fn.second();
		// If function expects a parameter and returns that parameter, return the passed
		// arg
		if (!param.isEmpty() && ret.equals(param))
			return new Ok<>(arg);
		// If return is another CALL, propagate with substitution if needed
		if (ret.startsWith("CALL:")) {
			String innerPayload = ret.substring(5);
			Tuple2<String, String> inner = splitNameArg(innerPayload);
			String innerName = inner.first();
			String innerArg = inner.second();
			if (!innerArg.isEmpty() && innerArg.equals(param))
				innerArg = arg;
			String toPass = innerName + (innerArg.isEmpty() ? "" : "," + innerArg);
			return innerArg.isEmpty() ? resolveFunctionValue(fns, innerName, visited)
					: resolveFunctionWithArg(fns, toPass, visited);
		}
		if (!ret.isEmpty() && Character.isDigit(ret.charAt(0)))
			return new Ok<>(ret);
		return new Err<>(new InterpretError("Unsupported function body: " + ret));
	}

	private Tuple2<String, String> splitNameArg(String payload) {
		int comma = payload.indexOf(',');
		if (comma < 0)
			return new Tuple2<>(payload, "");
		return new Tuple2<>(payload.substring(0, comma), payload.substring(comma + 1));
	}

	/**
	 * Consolidated pre-check for resolve flows. If isWithArg is true, the
	 * provided arg is considered; this helper returns Optional.of(Err) when an
	 * early error should be returned, otherwise Optional.empty() to continue.
	 */
	private static record PrecheckContext(java.util.Map<String, Tuple2<String, String>> fns, String name,
			java.util.Set<String> visited, boolean isWithArg, String arg) {
	}

	private Optional<Result<String, InterpretError>> precheckForResolve(PrecheckContext ctx) {
		var fns = ctx.fns();
		var name = ctx.name();
		var visited = ctx.visited();
		var isWithArg = ctx.isWithArg();
		if (!fns.containsKey(name))
			return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + name)));
		if (!visited.add(name))
			return Optional.of(new Err<>(new InterpretError("Recursive function call: " + name)));
		if (!isWithArg) {
			Tuple2<String, String> fn = fns.get(name);
			String param = fn.first();
			if (!param.isEmpty())
				return Optional.of(new Err<>(new InterpretError("Missing argument for call: " + name)));
		}
		return Optional.empty();
	}

	private Result<String, InterpretError> resolveFunctionValue(java.util.Map<String, Tuple2<String, String>> fns,
			String name, java.util.Set<String> visited) {
		Result<Tuple2<String, String>, InterpretError> fetched = precheckAndGetFn(
				new PrecheckContext(fns, name, visited, false, ""));
		if (fetched instanceof Ok<Tuple2<String, String>, InterpretError> ok) {
			Tuple2<String, String> fn = ok.value();
			String param = fn.first();
			String ret = fn.second();
			if (!param.isEmpty())
				return new Err<>(new InterpretError("Missing argument for call: " + name));
			if (ret.startsWith("CALL:"))
				return resolveFunctionValue(fns, ret.substring(5), visited);
			if (!ret.isEmpty() && Character.isDigit(ret.charAt(0)))
				return new Ok<>(ret);
			return new Err<>(new InterpretError("Unsupported function body: " + ret));
		} else if (fetched instanceof Err<Tuple2<String, String>, InterpretError> e) {
			return new Err<>(e.error());
		} else {
			return new Err<>(new InterpretError("Unexpected result while resolving function: " + name));
		}
	}

	/**
	 * Perform precheck and fetch function tuple in a single helper to avoid
	 * duplicated code. Returns Ok(tuple) on success or Err(InterpretError) on
	 * failure. Uses Optional.ofNullable internally instead of null checks.
	 */
	private Result<Tuple2<String, String>, InterpretError> precheckAndGetFn(PrecheckContext ctx) {
		Optional<Result<String, InterpretError>> pre = precheckForResolve(ctx);
		if (pre.isPresent())
			return new Err<>(new InterpretError(pre.get().toString()));
		Optional<Tuple2<String, String>> fnOpt = Optional.ofNullable(ctx.fns().get(ctx.name()));
		if (fnOpt.isEmpty())
			return new Err<>(new InterpretError("Unbound identifier: " + ctx.name()));
		return new Ok<>(fnOpt.get());
	}

	/**
	 * Parse function declaration like: fn name() : Type => <numeric-literal>
	 * Returns (name, returnValue)
	 */
	private Optional<Tuple2<String, Tuple2<String, String>>> parseFnPart(String stmt) {
		var header = parseFnHeader(stmt);
		if (header.isEmpty())
			return Optional.empty();
		String name = header.get().first();
		int pos = header.get().second().second();
		String paramName = header.get().second().first();

		var tail = parseFnTail(stmt, pos);
		if (tail.isEmpty())
			return Optional.empty();
		String typeTok = tail.get().first();
		String retExpr = tail.get().second().first();
		// if declared return type is Bool and retExpr is numeric, that's a mismatch;
		// return empty body to signal later
		if (!typeTok.isEmpty() && Character.toUpperCase(typeTok.charAt(0)) == 'B' && !retExpr.isEmpty()
				&& Character.isDigit(retExpr.charAt(0))) {
			return Optional.of(new Tuple2<>(name, new Tuple2<>(paramName, "")));
		}
		return Optional.of(new Tuple2<>(name, new Tuple2<>(paramName, retExpr)));
	}

	/**
	 * Parse class declaration like: class Name(field : Type) => {}
	 * Returns (name, (fieldName, typeToken))
	 */
	private Optional<Tuple2<String, Tuple2<String, String>>> parseClassPart(String stmt) {
		// accept either "class fn Name(...)" or "class Name(...)"
		Optional<Tuple2<String, Integer>> h = parseNameAndOpenParen(stmt, "class fn ");
		if (h.isEmpty())
			h = parseNameAndOpenParen(stmt, "class ");
		if (h.isEmpty())
			return Optional.empty();
		String cname = h.get().first();
		int p = h.get().second();
		Optional<FieldInfo> fiOpt = parseFieldInfo(stmt, p, ')');
		if (fiOpt.isEmpty())
			return Optional.empty();
		FieldInfo fi = fiOpt.get();
		String fieldName = fi.name();
		String typeToken = fi.type();
		int npos = fi.pos();
		if (npos >= stmt.length() || stmt.charAt(npos) != ')')
			return Optional.empty();
		npos = skipWhitespace(stmt, npos + 1);
		int afterArrow = consumeArrowAndSkip(stmt, npos);
		if (afterArrow < 0)
			return Optional.empty();
		npos = afterArrow;
		// allow body braces (may contain inner functions)
		if (npos >= stmt.length() || stmt.charAt(npos) != '{')
			return Optional.empty();
		int end = findMatchingBrace(stmt, npos);
		if (end < 0)
			return Optional.empty();

		// Parse any inner function declarations in the class body
		String classBody = stmt.substring(npos + 1, end).trim();
		if (!classBody.isEmpty()) {
			// For now, store the body content in the type token for later parsing
			typeToken = classBody;
		}

		npos = skipWhitespace(stmt, end + 1);
		if (npos != stmt.length())
			return Optional.empty();
		return Optional.of(new Tuple2<>(cname, new Tuple2<>(fieldName, typeToken)));
	}

	/**
	 * Parse struct declaration like: struct Name { field : Type }
	 * Returns (name, (fieldName, typeToken))
	 */
	private Optional<Tuple2<String, Tuple2<String, String>>> parseStructPart(String stmt) {
		if (!stmt.startsWith("struct "))
			return Optional.empty();
		int pos = 7;
		int idEnd = parseIdentifierEnd(stmt, pos);
		if (idEnd < 0)
			return Optional.empty();
		String name = stmt.substring(pos, idEnd);
		pos = skipWhitespace(stmt, idEnd);
		if (pos >= stmt.length() || stmt.charAt(pos) != '{')
			return Optional.empty();
		int fieldStart = skipWhitespace(stmt, pos + 1);
		Optional<FieldInfo> fiOpt = parseFieldInfo(stmt, fieldStart, '}');
		if (fiOpt.isEmpty())
			return Optional.empty();
		FieldInfo fi = fiOpt.get();
		String fieldName = fi.name();
		String typeToken = fi.type();
		int npos = fi.pos();
		npos = skipWhitespace(stmt, npos + 1);
		if (npos != stmt.length())
			return Optional.empty();
		return Optional.of(new Tuple2<>(name, new Tuple2<>(fieldName, typeToken)));
	}

	private Optional<Tuple2<String, Tuple2<String, Integer>>> parseFnTail(String stmt, int pos) {
		java.util.Optional<Tuple2<String, Integer>> typeOpt = parseOptionalType(stmt, pos);
		if (typeOpt.isEmpty())
			return Optional.empty();
		String typeTok = typeOpt.get().first();
		int npos = typeOpt.get().second();
		int afterArrow = consumeArrowAndSkip(stmt, npos);
		if (afterArrow < 0)
			return Optional.empty();
		npos = afterArrow;
		if (npos >= stmt.length())
			return Optional.empty();
		var rhsRes = parseFnRhs(stmt, npos);
		if (rhsRes.isEmpty())
			return Optional.empty();
		String retExpr = rhsRes.get().first();
		int end = rhsRes.get().second();
		if (end != stmt.length())
			return Optional.empty();
		return Optional.of(new Tuple2<>(typeTok, new Tuple2<>(retExpr, end)));
	}

	/**
	 * Split the string after a top-level brace starting at pos: returns
	 * Optional.of([prefix, remainder])
	 */
	private Optional<java.util.List<String>> splitAfterTopLevelBrace(String s, int pos) {
		int end = findMatchingBrace(s, pos);
		if (end < 0)
			return Optional.empty();
		int after = skipWhitespace(s, end + 1);
		if (after >= s.length())
			return Optional.empty();
		String decl = s.substring(0, after).trim();
		String rest = s.substring(after).trim();
		java.util.List<String> parts = new java.util.ArrayList<>();
		parts.add(decl);
		parts.add(rest);
		return Optional.of(parts);
	}

	/**
	 * Parse an argument token (either digits or identifier) starting at pos and
	 * ensure
	 * the following char after the token (after whitespace) equals expectedClose.
	 * Returns Optional.of(tokenEndPosTuple(token, posAfterClose)) or empty.
	 */
	private Optional<Tuple2<String, Integer>> parseTokenArgAndExpectClosing(String s, int pos, char expectedClose) {
		if (pos >= s.length())
			return Optional.empty();
		int ae = Character.isDigit(s.charAt(pos)) ? parseDigitsEnd(s, pos) : parseIdentifierEnd(s, pos);
		if (ae < 0)
			return Optional.empty();
		String arg = s.substring(pos, ae);
		int end = skipWhitespace(s, ae);
		if (end < s.length() && s.charAt(end) == expectedClose)
			return Optional.of(new Tuple2<>(arg, end));
		return Optional.empty();
	}

	private static record FieldInfo(String name, String type, int pos) {
	}

	private Optional<FieldInfo> parseFieldInfo(String stmt, int pos, char expectedClose) {
		// Handle empty parameter list case (e.g., "Empty()")
		if (pos < stmt.length() && stmt.charAt(pos) == expectedClose) {
			return Optional.of(new FieldInfo("", "", pos));
		}

		int fldEnd = parseIdentifierEnd(stmt, pos);
		if (fldEnd < 0)
			return Optional.empty();
		String fieldName = stmt.substring(pos, fldEnd);
		int npos = skipWhitespace(stmt, fldEnd);
		if (npos >= stmt.length() || stmt.charAt(npos) != ':')
			return Optional.empty();
		npos = skipWhitespace(stmt, npos + 1);
		int typeEnd = parseAlnumEnd(stmt, npos);
		if (typeEnd < 0)
			return Optional.empty();
		String typeToken = stmt.substring(npos, typeEnd).trim();
		npos = skipWhitespace(stmt, typeEnd);
		if (npos >= stmt.length() || stmt.charAt(npos) != expectedClose)
			return Optional.empty();
		return Optional.of(new FieldInfo(fieldName, typeToken, npos));
	}

	/**
	 * Parse a field declaration (name : Type) starting at pos and ensure the next
	 * character equals expectedClose after skipping whitespace. Returns
	 * Optional.of((fieldName,type), posAfterClose)
	 */

	/**
	 * Apply a let declaration into the env map that stores (isMut, value).
	 */
	private java.util.Optional<InterpretError> applyLetToEnv(java.util.Map<String, Tuple2<Boolean, String>> env,
			Tuple2<Boolean, String> nm, String rhs) {
		if (rhs.isEmpty())
			return java.util.Optional.of(new InterpretError("Empty RHS"));
		return bindResolved(env, nm, rhs);
	}

	private java.util.Optional<InterpretError> applyAssignmentToEnv(java.util.Map<String, Tuple2<Boolean, String>> env,
			String name,
			String rhs) {
		if (!env.containsKey(name))
			return java.util.Optional.of(new InterpretError("Unbound identifier: " + name));
		Tuple2<Boolean, String> entry = env.get(name);
		if (!entry.first())
			return java.util.Optional.of(new InterpretError("Cannot assign to immutable binding: " + name));
		if (rhs.isEmpty())
			return java.util.Optional.of(new InterpretError("Empty RHS"));
		return bindResolved(env, new Tuple2<>(entry.first(), name), rhs);
	}

	private java.util.Optional<InterpretError> bindResolved(java.util.Map<String, Tuple2<Boolean, String>> env,
			Tuple2<Boolean, String> nm, String rhs) {
		String name = nm.second();
		Boolean isMut = nm.first();
		java.util.Optional<String> resolved = resolveRhsForEnv(env, rhs);
		if (resolved.isEmpty())
			return java.util.Optional.of(new InterpretError("Unbound identifier: " + rhs));
		insertBinding(env, name, new Tuple2<>(isMut, resolved.get()));
		return java.util.Optional.empty();
	}

	private void insertBinding(java.util.Map<String, Tuple2<Boolean, String>> env, String name,
			Tuple2<Boolean, String> binding) {
		env.put(name, binding);
	}

	/**
	 * Resolve rhs string into a stored value using env. If rhs is digits or
	 * boolean token it returns that token, else looks up in env. Returns
	 * Optional.empty() when lookup fails (unbound identifier).
	 */
	private java.util.Optional<String> resolveRhsForEnv(java.util.Map<String, Tuple2<Boolean, String>> env, String rhs) {
		if (rhs.isEmpty())
			return java.util.Optional.empty();
		if (Character.isDigit(rhs.charAt(0)))
			return java.util.Optional.of(rhs);
		if (rhs.equals("true") || rhs.equals("false"))
			return java.util.Optional.of(rhs);
		if (!env.containsKey(rhs))
			return java.util.Optional.empty();
		return java.util.Optional.of(env.get(rhs).second());
	}

	/**
	 * Parse function header "fn name(param : Type)" and return
	 * (name,(paramName,posAfterParen))
	 */
	private Optional<Tuple2<String, Tuple2<String, Integer>>> parseFnHeader(String stmt) {
		Optional<Tuple2<String, Integer>> header = parseNameAndOpenParen(stmt, "fn ");
		if (header.isEmpty())
			return Optional.empty();
		String name = header.get().first();
		int pos = header.get().second();
		String paramName = "";
		if (pos < stmt.length() && stmt.charAt(pos) != ')') {
			Optional<Tuple2<String, Integer>> p = parseFnParam(stmt, pos);
			if (p.isEmpty())
				return Optional.empty();
			paramName = p.get().first();
			pos = p.get().second();
		}
		if (pos >= stmt.length() || stmt.charAt(pos) != ')')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 1);
		return Optional.of(new Tuple2<>(name, new Tuple2<>(paramName, pos)));
	}

	private Optional<Tuple2<String, Integer>> parseNameAndOpenParen(String stmt, String prefix) {
		if (!stmt.startsWith(prefix))
			return Optional.empty();
		int pos = prefix.length();
		int idEnd = parseIdentifierEnd(stmt, pos);
		if (idEnd < 0)
			return Optional.empty();
		String name = stmt.substring(pos, idEnd);
		pos = skipWhitespace(stmt, idEnd);
		// accept optional generic parameters like <T> after the name
		if (pos < stmt.length() && stmt.charAt(pos) == '<') {
			int i = pos + 1;
			while (i < stmt.length() && stmt.charAt(i) != '>')
				i++;
			if (i >= stmt.length() || stmt.charAt(i) != '>')
				return Optional.empty();
			pos = skipWhitespace(stmt, i + 1);
		}
		if (pos >= stmt.length() || stmt.charAt(pos) != '(')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 1);
		return Optional.of(new Tuple2<>(name, pos));
	}

	/**
	 * Parse optional fn parameter name and return pair (name, posAfter)
	 */
	private Optional<Tuple2<String, Integer>> parseFnParam(String stmt, int pos) {
		if (pos >= stmt.length() || stmt.charAt(pos) == ')')
			return Optional.of(new Tuple2<>("", pos));
		int pend = parseIdentifierEnd(stmt, pos);
		if (pend < 0)
			return Optional.empty();
		String name = stmt.substring(pos, pend);
		int npos = skipWhitespace(stmt, pend);
		java.util.Optional<Tuple2<String, Integer>> pTypeOpt = parseOptionalType(stmt, npos);
		if (pTypeOpt.isEmpty())
			return Optional.empty();
		return Optional.of(new Tuple2<>(name, pTypeOpt.get().second()));
	}

	/**
	 * Parse fn RHS: numeric literal or identifier/call. Returns (expr, posAfter)
	 */
	private Optional<Tuple2<String, Integer>> parseFnRhs(String stmt, int pos) {
		if (pos >= stmt.length())
			return Optional.empty();
		if (Character.isDigit(stmt.charAt(pos))) {
			int vEnd = parseDigitsEnd(stmt, pos);
			if (vEnd < 0)
				return Optional.empty();
			String ret = stmt.substring(pos, vEnd);
			return Optional.of(new Tuple2<>(ret, skipWhitespace(stmt, vEnd)));
		}
		int rEnd = parseIdentifierEnd(stmt, pos);
		if (rEnd < 0)
			return Optional.empty();
		String ident = stmt.substring(pos, rEnd);
		int npos = skipWhitespace(stmt, rEnd);
		if (npos < stmt.length() && stmt.charAt(npos) == '(') {
			int pp = skipWhitespace(stmt, npos + 1);
			// no-arg call form: ident()
			if (pp < stmt.length() && stmt.charAt(pp) == ')') {
				return Optional.of(new Tuple2<>("CALL:" + ident, skipWhitespace(stmt, pp + 1)));
			}
			// call with single token argument (digits or identifier)
			int argEnd = -1;
			if (pp < stmt.length() && Character.isDigit(stmt.charAt(pp))) {
				argEnd = parseDigitsEnd(stmt, pp);
			} else {
				argEnd = parseIdentifierEnd(stmt, pp);
			}
			if (argEnd < 0)
				return Optional.empty();
			String arg = stmt.substring(pp, argEnd);
			int afterArg = skipWhitespace(stmt, argEnd);
			if (afterArg >= stmt.length() || stmt.charAt(afterArg) != ')')
				return Optional.empty();
			// encode call with argument as CALL:name,arg
			return Optional.of(new Tuple2<>("CALL:" + ident + "," + arg, skipWhitespace(stmt, afterArg + 1)));
		}
		return Optional.of(new Tuple2<>(ident, npos));
	}

	private static java.util.List<String> splitStatements(String s) {
		java.util.List<String> raw = splitRaw(s, ';');
		java.util.List<String> stmts = new java.util.ArrayList<>();
		for (String p : raw) {
			String t = p.trim();
			if (!t.isEmpty())
				stmts.add(t);
		}
		return stmts;
	}

	private static java.util.List<String> splitRaw(String s, char delim) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		int start = 0;
		while (true) {
			int idx = findTopLevelIndex(s, start, ch -> s.charAt(ch) == delim);
			if (idx < 0)
				break;
			parts.add(s.substring(start, idx));
			start = idx + 1;
		}
		parts.add(s.substring(start));
		return parts;
	}

	/**
	 * If input contains a single top-level declaration followed by an expression
	 * without a separating semicolon, split into [declaration, remainder]. For
	 * example: "class fn X(...) => {} X(1).f" -> ["class fn X(...) => {}",
	 * "X(1).f"]
	 */
	private Optional<java.util.List<String>> splitFirstDeclarationAndRemainder(String s) {
		// try to parse a leading 'fn ' or 'class ' declaration and find the end
		if (s.startsWith("fn ") || s.startsWith("class ") || s.startsWith("class fn ")) {
			// find the top-level position after the declaration body: look for '=>' then
			// body
			int arrow = s.indexOf("=>");
			if (arrow < 0)
				return Optional.empty();
			int pos = skipWhitespace(s, arrow + 2);
			if (pos >= s.length())
				return Optional.empty();
			if (s.charAt(pos) == '{') {
				return splitAfterTopLevelBrace(s, pos);
			}
		}
		// try to parse a leading struct declaration like: struct Name { ... }
		if (s.startsWith("struct ")) {
			int pos = 7;
			int idEnd = parseIdentifierEnd(s, pos);
			if (idEnd < 0)
				return Optional.empty();
			int bracePos = skipWhitespace(s, idEnd);
			if (bracePos >= s.length() || s.charAt(bracePos) != '{')
				return Optional.empty();
			return splitAfterTopLevelBrace(s, bracePos);
		}

		// Fallback: if we couldn't match with stricter parsing, try a permissive
		// split based on the first '{' and its first '}' thereafter. This handles
		// simple declarations followed by an expression.
		int fb = s.indexOf('{');
		if (fb >= 0) {
			int fc = findMatchingBrace(s, fb);
			if (fc > fb) {
				int after = skipWhitespace(s, fc + 1);
				if (after < s.length()) {
					java.util.List<String> parts = new java.util.ArrayList<>();
					parts.add(s.substring(0, after).trim());
					parts.add(s.substring(after).trim());
					return Optional.of(parts);
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Find index at or after 'from' where the provided matcher holds and the
	 * location is at top-level (not nested inside parentheses or braces). The
	 * matcher receives the index to test. Returns -1 if none found.
	 */
	private static int findTopLevelIndex(String s, int from, java.util.function.IntPredicate matcher) {
		int paren = 0;
		int brace = 0;
		for (int i = from; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '(')
				paren++;
			else if (c == ')')
				paren = Math.max(0, paren - 1);
			else if (c == '{')
				brace++;
			else if (c == '}')
				brace = Math.max(0, brace - 1);
			if (paren == 0 && brace == 0 && matcher.test(i))
				return i;
		}
		return -1;
	}

	/**
	 * Find index of matching '}' starting at pos (where stmt.charAt(pos) == '{')
	 */
	private int findMatchingBrace(String stmt, int pos) {
		int depth = 1;
		int i = pos + 1;
		for (; i < stmt.length(); i++) {
			char cc = stmt.charAt(i);
			if (cc == '{')
				depth++;
			else if (cc == '}') {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
	}

	private Optional<String> parseBlockRhs(String stmt, int pos) {
		int end = findMatchingBrace(stmt, pos);
		if (end < 0)
			return Optional.empty();
		String inner = stmt.substring(pos + 1, end).trim();
		Result<String, InterpretError> r = interpret(inner);
		if (r instanceof Ok<String, InterpretError> ok) {
			return Optional.of(ok.value());
		}
		if (r instanceof Err<String, InterpretError> er) {
			return Optional.of("ERR:" + er.error().display());
		}
		return Optional.empty();
	}

	/**
	 * Find matching ')' starting at pos (where stmt.charAt(pos) == '(')
	 */
	private int findMatchingParen(String stmt, int pos) {
		int depth = 1;
		int i = pos + 1;
		for (; i < stmt.length(); i++) {
			char c = stmt.charAt(i);
			if (c == '(')
				depth++;
			else if (c == ')') {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
	}

	/**
	 * Find the index of the 'else' keyword that is not nested inside parens or
	 * braces, starting search from pos. Returns -1 if not found.
	 */
	private int findElseIndex(String s, int pos) {
		return findTopLevelIndex(s, pos, i -> i + 4 <= s.length() && s.startsWith("else", i));
	}

	private Optional<Result<String, InterpretError>> handleIfExpression(String trimmed) {
		if (!trimmed.startsWith("if "))
			return Optional.empty();
		int p = skipWhitespace(trimmed, 2);
		if (p >= trimmed.length() || trimmed.charAt(p) != '(')
			return Optional.empty();
		int close = findMatchingParen(trimmed, p);
		if (close <= p)
			return Optional.empty();
		String cond = trimmed.substring(p + 1, close).trim();
		int after = skipWhitespace(trimmed, close + 1);
		int elseIdx = findElseIndex(trimmed, after);
		if (elseIdx <= after)
			return Optional.empty();
		String thenPart = trimmed.substring(after, elseIdx).trim();
		int afterElse = skipWhitespace(trimmed, elseIdx + 4);
		String elsePart = trimmed.substring(afterElse).trim();
		if (cond.equals("true"))
			return Optional.of(interpret(thenPart));
		if (cond.equals("false"))
			return Optional.of(interpret(elsePart));
		return Optional.of(new Err<>(new InterpretError("Unsupported condition: " + cond)));
	}

	private Optional<Result<String, InterpretError>> handleSingleLet(String stmt) {
		Optional<String> rhsOpt = parseLetRhs(stmt);
		if (rhsOpt.isEmpty())
			return Optional.empty();
		String rhs = rhsOpt.get();
		if (rhs.isEmpty())
			return Optional.empty();
		// If rhs is a numeric literal, check declared type (if any) against the
		// literal's suffix
		if (Character.isDigit(rhs.charAt(0))) {
			Optional<Character> declared = parseLetDeclaredSignedness(stmt);
			// If the declared type is Bool (leading 'B'), numeric RHS is invalid
			if (declared.isPresent() && Character.toUpperCase(declared.get()) == 'B')
				return Optional.of(new Err<>(new InterpretError("Type mismatch in let: " + stmt)));
			Optional<Character> rhsSuffix = parseLetRhsSignedness(stmt);
			if (declared.isPresent() && rhsSuffix.isPresent()) {
				char d = declared.get();
				char r = rhsSuffix.get();
				if (isSignednessChar(d) && isSignednessChar(r) && d != r)
					return Optional.of(new Err<>(new InterpretError("Signedness mismatch in let: " + stmt)));
			}
			return Optional.of(new Ok<>(""));
		}
		// boolean literal
		if (rhs.equals("true") || rhs.equals("false")) {
			Optional<Character> declared = parseLetDeclaredSignedness(stmt);
			if (declared.isPresent()) {
				char d = declared.get();
				// treat types starting with 'B' as Bool
				if (Character.toUpperCase(d) != 'B')
					return Optional.of(new Err<>(new InterpretError("Type mismatch in let: " + stmt)));
			}
			return Optional.of(new Ok<>(""));
		}
		return Optional.of(new Err<>(new InterpretError("Undefined identifier: " + rhs)));
	}

	private Optional<Result<String, InterpretError>> handleMultipleLets(java.util.List<String> stmts) {
		String finalPart = stmts.get(stmts.size() - 1);
		// final part may be either a single identifier (to return) or a block
		// literal (to evaluate with the current let environment). Reject other
		// forms.
		if (!(isSingleIdentifier(finalPart) || (finalPart.startsWith("{") && finalPart.endsWith("}"))))
			return Optional.empty();
		java.util.Map<String, Tuple2<Boolean, String>> env = new java.util.LinkedHashMap<>();
		for (int i = 0; i < stmts.size() - 1; i++) {
			java.util.Optional<java.util.Optional<InterpretError>> perr = processLetStmt(env, stmts.get(i));
			if (perr.isEmpty())
				return Optional.empty();
			if (perr.get().isPresent())
				return Optional.of(new Err<>(perr.get().get()));
		}
		// If the final part is a block literal, evaluate the inner expression with
		// the current let environment by reconstructing let declarations in order
		// and delegating to interpret. This allows blocks to reference earlier
		// let-bound identifiers, e.g. `let x = 10; {x}`.
		if (finalPart.startsWith("{") && finalPart.endsWith("}")) {
			String inner = finalPart.substring(1, finalPart.length() - 1).trim();
			StringBuilder sb = new StringBuilder();
			for (java.util.Map.Entry<String, Tuple2<Boolean, String>> e : env.entrySet()) {
				Tuple2<Boolean, String> val = e.getValue();
				if (val.first())
					sb.append("let mut ").append(e.getKey()).append(" = ").append(val.second()).append("; ");
				else
					sb.append("let ").append(e.getKey()).append(" = ").append(val.second()).append("; ");
			}
			sb.append(inner);
			return Optional.of(interpret(sb.toString()));
		}

		String finalName = finalPart;
		if (!env.containsKey(finalName))
			return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + finalName)));
		return Optional.of(new Ok<>(env.get(finalName).second()));
	}

	private static boolean isSingleIdentifier(String s) {
		int fend = parseIdentifierEnd(s, 0);
		return fend > 0 && skipWhitespace(s, fend) == s.length();
	}

	private java.util.Optional<java.util.Optional<InterpretError>> processLetStmt(
			java.util.Map<String, Tuple2<Boolean, String>> env,
			String stmt) {
		// stmt may be a let declaration or an assignment like `x = 100`.
		if (stmt.startsWith("let ")) {
			Optional<Tuple2<Tuple2<Boolean, String>, String>> kvOpt = parseLetPart(stmt);
			if (kvOpt.isEmpty())
				return java.util.Optional.empty();
			Tuple2<Boolean, String> nm = kvOpt.get().first();
			String rhs = kvOpt.get().second();
			return java.util.Optional.of(applyLetToEnv(env, nm, rhs));
		}
		// assignment statement
		int eq = stmt.indexOf('=');
		if (eq <= 0)
			return java.util.Optional.empty();
		int idEnd = parseIdentifierEnd(stmt, 0);
		if (idEnd <= 0 || skipWhitespace(stmt, idEnd) != eq)
			return java.util.Optional.empty();
		String name = stmt.substring(0, idEnd).trim();
		int pos = skipWhitespace(stmt, eq + 1);
		Optional<String> rhsOpt = extractLetRhsValue(stmt, pos);
		if (rhsOpt.isEmpty())
			return java.util.Optional.empty();
		String rhs = rhsOpt.get();
		return java.util.Optional.of(applyAssignmentToEnv(env, name, rhs));
	}

	/**
	 * Parse let part and return ((isMut,name), rhs)
	 */
	private Optional<Tuple2<Tuple2<Boolean, String>, String>> parseLetPart(String stmt) {
		// stmt should start with "let " or "let mut "
		if (!stmt.startsWith("let "))
			return Optional.empty();
		int pos = 4;
		boolean isMut = false;
		if (stmt.startsWith("mut", pos) && pos + 3 <= stmt.length()
				&& (pos + 3 == stmt.length() || Character.isWhitespace(stmt.charAt(pos + 3)))) {
			isMut = true;
			pos = skipWhitespace(stmt, pos + 3);
		}
		java.util.Optional<Tuple2<String, Tuple2<String, Integer>>> nameTypeOpt = parseLetNameAndOptionalType(stmt, pos);
		if (nameTypeOpt.isEmpty())
			return Optional.empty();
		Tuple2<String, Tuple2<String, Integer>> nameType = nameTypeOpt.get();
		String name = nameType.first();
		pos = nameType.second().second();
		if (pos >= stmt.length() || stmt.charAt(pos) != '=')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 1);
		if (pos >= stmt.length())
			return Optional.empty();
		Optional<String> valOpt = extractLetRhsValue(stmt, pos);
		if (valOpt.isEmpty())
			return Optional.empty();
		return Optional.of(new Tuple2<>(new Tuple2<>(isMut, name), valOpt.get()));
	}

	private Optional<String> extractLetRhsValue(String stmt, int pos) {
		if (pos >= stmt.length())
			return Optional.empty();
		// rhs is either digits, a block, boolean, identifier, or an if-expression
		Optional<String> ifVal = parseIfRhsValue(stmt, pos);
		if (ifVal.isPresent())
			return ifVal;
		return parseSimpleRhsValue(stmt, pos);
	}

	private Optional<String> parseSimpleRhsValue(String stmt, int pos) {
		if (pos >= stmt.length())
			return Optional.empty();
		// Delegate constructor.field parsing to helper
		Optional<String> ctorField = parseConstructorFieldRhsValue(stmt, pos);
		if (ctorField.isPresent())
			return ctorField;
		Optional<String> block = parseBlockRhsValue(stmt, pos);
		if (block.isPresent())
			return block;
		Optional<String> num = parseNumericRhsValue(stmt, pos);
		if (num.isPresent())
			return num;
		Optional<String> bool = parseBooleanRhsValue(stmt, pos);
		if (bool.isPresent())
			return bool;
		return parseIdentifierRhsValue(stmt, pos);
	}

	private Optional<String> parseBlockRhsValue(String stmt, int pos) {
		if (pos >= stmt.length() || stmt.charAt(pos) != '{')
			return Optional.empty();
		Optional<String> blockVal = parseBlockRhs(stmt, pos);
		if (blockVal.isEmpty())
			return Optional.empty();
		String val = blockVal.get();
		int end = findMatchingBrace(stmt, pos);
		if (end < 0)
			return Optional.empty();
		int npos = skipWhitespace(stmt, end + 1);
		if (npos != stmt.length())
			return Optional.empty();
		return Optional.of(val);
	}

	private Optional<String> parseNumericRhsValue(String stmt, int pos) {
		if (pos >= stmt.length() || !Character.isDigit(stmt.charAt(pos)))
			return Optional.empty();
		int vEnd = parseDigitsEnd(stmt, pos);
		if (vEnd < 0)
			return Optional.empty();
		int npos = skipWhitespace(stmt, vEnd);
		if (npos != stmt.length())
			return Optional.empty();
		return Optional.of(stmt.substring(pos, vEnd));
	}

	private Optional<String> parseBooleanRhsValue(String stmt, int pos) {
		if (!(stmt.startsWith("true", pos) || stmt.startsWith("false", pos)))
			return Optional.empty();
		int len = stmt.startsWith("true", pos) ? 4 : 5;
		int vEnd = pos + len;
		int npos = skipWhitespace(stmt, vEnd);
		if (npos != stmt.length())
			return Optional.empty();
		return Optional.of(stmt.substring(pos, vEnd));
	}

	private Optional<String> parseIdentifierRhsValue(String stmt, int pos) {
		int rEnd = parseIdentifierEnd(stmt, pos);
		if (rEnd < 0)
			return Optional.empty();
		int npos = skipWhitespace(stmt, rEnd);
		if (npos != stmt.length())
			return Optional.empty();
		return Optional.of(stmt.substring(pos, rEnd));
	}

	/**
	 * Extracts a constructor.field RHS when present at pos. Returns the
	 * inner constructor arg string (the token inside braces) on success.
	 */
	private Optional<String> parseConstructorFieldRhsValue(String stmt, int pos) {
		int idEndStart = parseIdentifierEnd(stmt, pos);
		if (idEndStart <= 0)
			return Optional.empty();
		int wsAfterId = skipWhitespace(stmt, idEndStart);
		if (wsAfterId >= stmt.length() || stmt.charAt(wsAfterId) != '{')
			return Optional.empty();
		int startArg = skipWhitespace(stmt, wsAfterId + 1);
		Optional<Tuple2<String, Integer>> argOpt = parseTokenArgAndExpectClosing(stmt, startArg, '}');
		if (argOpt.isEmpty())
			return Optional.empty();
		String arg = argOpt.get().first();
		int afterBrace = skipWhitespace(stmt, argOpt.get().second() + 1);
		// expect .field following
		if (afterBrace < stmt.length() && stmt.charAt(afterBrace) == '.') {
			int fend = parseIdentifierEnd(stmt, afterBrace + 1);
			if (fend > 0) {
				int npos = skipWhitespace(stmt, fend);
				if (npos == stmt.length())
					return Optional.of(arg);
			}
		}
		return Optional.empty();
	}

	private Optional<String> parseIfRhsValue(String stmt, int pos) {
		if (!(stmt.startsWith("if", pos)
				&& (pos + 2 == stmt.length() || Character.isWhitespace(stmt.charAt(pos + 2)) || stmt.charAt(pos + 2) == '(')))
			return Optional.empty();
		int p = skipWhitespace(stmt, pos + 2);
		if (p >= stmt.length() || stmt.charAt(p) != '(')
			return Optional.empty();
		int close = findMatchingParen(stmt, p);
		if (close <= p)
			return Optional.empty();
		String cond = stmt.substring(p + 1, close).trim();
		int after = skipWhitespace(stmt, close + 1);
		int elseIdx = findElseIndex(stmt, after);
		if (elseIdx <= after)
			return Optional.empty();
		String thenPart = stmt.substring(after, elseIdx).trim();
		int afterElse = skipWhitespace(stmt, elseIdx + 4);
		int endNext = exprEndNextPos(stmt, afterElse);
		if (endNext != stmt.length())
			return Optional.empty();
		String elsePart = stmt.substring(afterElse).trim();
		// evaluate condition (only simple true/false supported here)
		Optional<String> chosen = Optional.empty();
		if (cond.equals("true"))
			chosen = Optional.of(thenPart);
		else if (cond.equals("false"))
			chosen = Optional.of(elsePart);
		else
			return Optional.empty();
		Result<String, InterpretError> r = interpret(chosen.get());
		if (r instanceof Ok<String, InterpretError> ok)
			return Optional.of(ok.value());
		if (r instanceof Err<String, InterpretError> er)
			return Optional.of("ERR:" + er.error().display());
		return Optional.empty();
	}

	/**
	 * Parse identifier name at startPos and any optional type token. Returns
	 * pair (name, (typeToken, posAfterType)). typeToken is empty string if no
	 * type was present.
	 */
	private java.util.Optional<Tuple2<String, Tuple2<String, Integer>>> parseLetNameAndOptionalType(String stmt,
			int startPos) {
		int idEnd = parseIdentifierEnd(stmt, startPos);
		if (idEnd < 0)
			return java.util.Optional.empty();
		String name = stmt.substring(startPos, idEnd);
		int pos = skipWhitespace(stmt, idEnd);
		java.util.Optional<Tuple2<String, Integer>> typeOpt = parseOptionalType(stmt, pos);
		if (typeOpt.isEmpty())
			return java.util.Optional.empty();
		Tuple2<String, Integer> te = typeOpt.get();
		return java.util.Optional.of(new Tuple2<>(name, te));
	}

	/**
	 * Convenience helper that returns only the RHS string for a let-part if
	 * present.
	 */
	private Optional<String> parseLetRhs(String stmt) {
		Optional<Tuple2<Tuple2<Boolean, String>, String>> kvOpt = parseLetPart(stmt);
		if (kvOpt.isEmpty())
			return Optional.empty();
		return Optional.of(kvOpt.get().second());
	}

	/**
	 * Returns the signedness character (first letter) of the declared type if
	 * present,
	 * for example for "let x : U8 = ..." returns Optional.of('U').
	 */
	private Optional<Character> parseLetDeclaredSignedness(String stmt) {
		if (!stmt.startsWith("let "))
			return Optional.empty();
		int pos = 4;
		int idEnd = parseIdentifierEnd(stmt, pos);
		if (idEnd < 0)
			return Optional.empty();
		pos = skipWhitespace(stmt, idEnd);
		java.util.Optional<Tuple2<String, Integer>> typeOpt = parseOptionalType(stmt, pos);
		if (typeOpt.isEmpty())
			return Optional.empty();
		String typeToken = typeOpt.get().first();
		if (!typeToken.isEmpty())
			return Optional.of(Character.toUpperCase(typeToken.charAt(0)));
		return Optional.empty();
	}

	/**
	 * Parse an optional type token following whitespace at pos. If there's no
	 * type (no ':' present) returns a present entry with null key and the
	 * returned pos. If ':' is present but malformed returns empty.
	 */
	private java.util.Optional<Tuple2<String, Integer>> parseOptionalType(String stmt, int pos) {
		pos = skipWhitespace(stmt, pos);
		if (pos < stmt.length() && stmt.charAt(pos) == ':') {
			pos = skipWhitespace(stmt, pos + 1);
			int typeEnd = parseAlnumEnd(stmt, pos);
			if (typeEnd < 0)
				return java.util.Optional.empty();
			String typeToken = stmt.substring(pos, typeEnd).trim();
			pos = skipWhitespace(stmt, typeEnd);
			return java.util.Optional.of(new Tuple2<>(typeToken, pos));
		}
		return java.util.Optional.of(new Tuple2<>("", pos));
	}

	/**
	 * If the RHS is a numeric literal with a suffix (e.g. "10I32"), return the
	 * leading char of the suffix (e.g. 'I'), otherwise empty.
	 */
	private Optional<Character> parseLetRhsSignedness(String stmt) {
		int eq = stmt.indexOf('=');
		if (eq < 0)
			return Optional.empty();
		int pos = skipWhitespace(stmt, eq + 1);
		if (pos >= stmt.length())
			return Optional.empty();
		if (!Character.isDigit(stmt.charAt(pos)))
			return Optional.empty();
		int vEnd = parseDigitsEnd(stmt, pos);
		if (vEnd < 0)
			return Optional.empty();
		if (vEnd >= stmt.length())
			return Optional.empty();
		String suffix = stmt.substring(vEnd).trim();
		if (suffix.isEmpty())
			return Optional.empty();
		return Optional.of(Character.toUpperCase(suffix.charAt(0)));
	}

	private static int skipWhitespace(String s, int pos) {
		while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
			pos++;
		return pos;
	}

	private static int parseIdentifierEnd(String s, int pos) {
		int start = pos;
		while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'))
			pos++;
		return pos == start ? -1 : pos;
	}

	private static int parseAlnumEnd(String s, int pos) {
		int start = pos;
		while (pos < s.length() && Character.isLetterOrDigit(s.charAt(pos)))
			pos++;
		return pos == start ? -1 : pos;
	}

	private static int parseDigitsEnd(String s, int pos) {
		int start = pos;
		while (pos < s.length() && Character.isDigit(s.charAt(pos)))
			pos++;
		return pos == start ? -1 : pos;
	}

	/**
	 * Consume a '=>' arrow starting at or after pos (skip whitespace first).
	 * Returns the position after the arrow, skipping any whitespace, or -1 if
	 * the arrow is not present.
	 */
	private int consumeArrowAndSkip(String stmt, int pos) {
		int p = skipWhitespace(stmt, pos);
		if (p + 1 >= stmt.length() || stmt.charAt(p) != '=' || stmt.charAt(p + 1) != '>')
			return -1;
		return skipWhitespace(stmt, p + 2);
	}

	/**
	 * Return the position after the expression that starts at pos, or -1 if
	 * malformed. This is a lightweight check used by extractLetRhsValue to
	 * ensure the RHS consumes the remainder of the statement.
	 */
	private int exprEndNextPos(String s, int pos) {
		if (pos >= s.length())
			return -1;
		if (s.charAt(pos) == '{') {
			int end = findMatchingBrace(s, pos);
			return end < 0 ? -1 : skipWhitespace(s, end + 1);
		}
		if (s.startsWith("if", pos)) {
			int p = skipWhitespace(s, pos + 2);
			if (p >= s.length() || s.charAt(p) != '(')
				return -1;
			int close = findMatchingParen(s, p);
			if (close <= p)
				return -1;
			int after = skipWhitespace(s, close + 1);
			int elseIdx = findElseIndex(s, after);
			if (elseIdx <= after)
				return -1;
			int afterElse = skipWhitespace(s, elseIdx + 4);
			return exprEndNextPos(s, afterElse);
		}
		if (Character.isDigit(s.charAt(pos))) {
			int vEnd = parseDigitsEnd(s, pos);
			return vEnd < 0 ? -1 : skipWhitespace(s, vEnd);
		}
		if (s.startsWith("true", pos) || s.startsWith("false", pos)) {
			int len = s.startsWith("true", pos) ? 4 : 5;
			return skipWhitespace(s, pos + len);
		}
		int rEnd = parseIdentifierEnd(s, pos);
		if (rEnd < 0)
			return -1;
		return skipWhitespace(s, rEnd);
	}
}
