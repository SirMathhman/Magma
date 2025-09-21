package magma;

import magma.Option.None;
import magma.Option.Some;
import magma.Result.Err;
import magma.Result.Ok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Executor {
	private record PlusOperands(String sum, String leftSuffix, String rightSuffix) {
	}

	private static Option<Result<String, String>> handleCompoundPlusAssignment(String ident, String[] entry,
			String[] rhsPair, Map<String, String[]> env) {
		var lhsSuffix = entry[1];
		var mutFlag = entry[2];
		// Do not support pointer-target compound assignment here
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut")) {
			return createErr("Compound assignment not supported on pointer target");
		}
		// Left-hand current value
		var lhsVal = entry[0];
		if (Objects.isNull(lhsVal) || lhsVal.isEmpty())
			return createErr("Uninitialized variable '" + ident + "'");
		// Try numeric addition using the helper
		var sumOpt = parseAndSumStrings(lhsVal, rhsPair[0], lhsSuffix, rhsPair[1]);
		if (!(sumOpt instanceof Some<PlusOperands>(var sumOperands)))
			return createErr("Invalid compound assignment operands");
		env.put(ident, makeNewEntry(sumOperands.sum, sumOperands.leftSuffix, mutFlag));
		return new None<>();
	}

	public static Result<String, String> execute(String input) {
		Some<String> stringOption = new Some<>(input);
		var opt = !stringOption.value().isEmpty() ? stringOption : new None<String>();
		if (opt instanceof None<String>) {
			return new Ok<>("");
		}
		if (opt instanceof Some<String> someString) {
			var s = someString.value().trim();
			// If the whole input is a braced block (matching braces), evaluate the inner
			// sequence directly
			if (s.startsWith("{") && matchingClosingBraceIndex(s) == s.length() - 1) {
				var inner = s.substring(1, s.length() - 1).trim();
				return runSequence(inner);
			}
			return runSequence(s);
		}
		return new Ok<>(""); // This should never be reached but needed for compilation
	}

	private static Option<Result<String, String>> processNonFinalStatements(List<String> nonEmpty,
			Map<String, String[]> env) {
		if (nonEmpty.size() <= 1)
			return new None<>();
		var toProcess = nonEmpty.subList(0, nonEmpty.size() - 1);
		return processStatements(toProcess, env);
	}

	private static Option<PlusOperands> parseAndSumStrings(String aStr, String bStr, String aSuffix, String bSuffix) {
		try {
			var a = Integer.parseInt(aStr);
			var b = Integer.parseInt(bStr);
			return new Some<>(new PlusOperands(String.valueOf(a + b), aSuffix, bSuffix));
		} catch (NumberFormatException ex) {
			return new None<>();
		}
	}

	private static ArrayList<String> splitNonEmptyStatements(String s) {
		var nonEmpty = new ArrayList<String>();
		var start = 0;
		var depth = 0;

		for (var i = 0; i < s.length(); i++) {
			var c = s.charAt(i);
			depth = updateBraceDepth(depth, c);
			if (isSemicolonAtTopLevel(c, depth)) {
				var statement = s.substring(start, i).trim();
				if (!statement.isEmpty()) {
					nonEmpty.add(statement);
				}
				start = i + 1;
			} else if (c == '}' && depth == 0) {
				// Only split on a top-level closing brace if it's followed by a semicolon or
				// end-of-input
				var next = i + 1;
				while (next < s.length() && Character.isWhitespace(s.charAt(next))) {
					next++;
				}
				if (next >= s.length() || s.charAt(next) == ';' || Character.isLetter(s.charAt(next))) {
					var statement = s.substring(start, i + 1).trim();
					if (!statement.isEmpty()) {
						nonEmpty.add(statement);
					}
					start = i + 1;
				}
			}
		}

		// Add the remaining part after the last semicolon
		var lastStatement = s.substring(start).trim();
		if (!lastStatement.isEmpty()) {
			nonEmpty.add(lastStatement);
		}

		return nonEmpty;
	}

	private static boolean isSemicolonAtTopLevel(char c, int depth) {
		return c == ';' && depth == 0;
	}

	private static int updateBraceDepth(int depth, char c) {
		if (c == '{') {
			return depth + 1;
		} else if (c == '}') {
			return depth - 1;
		}
		return depth;
	}

	private static Option<Result<String, String>> handleAllBindingsCase(List<String> nonEmpty,
			Map<String, String[]> env) {
		if (nonEmpty.isEmpty())
			return new None<>();
		var last = nonEmpty.getLast();
		if (last.startsWith("let ") || isAssignmentStatement(last)) {
			var buildErr = processStatements(nonEmpty, env);
			if (buildErr instanceof Some<Result<String, String>>)
				return buildErr;
			return new Some<>(new Ok<>(""));
		}
		return new None<>();
	}

	private static Result<String, String> runSequence(String s) {
		var nonEmpty = splitNonEmptyStatements(s);
		var env = new HashMap<String, String[]>();
		if (nonEmpty.isEmpty())
			return new Ok<>("");
		var maybe = handleAllBindingsCase(nonEmpty, env);
		if (maybe instanceof Some<Result<String, String>>(var value)) {
			return value;
		}
		var buildErr = processNonFinalStatements(nonEmpty, env);
		return switch (buildErr) {
			case None<Result<String, String>> _ -> evaluateFinal(nonEmpty.getLast(), env);
			case Some<Result<String, String>>(Result<String, String> value) -> value;
		};
	}

	private static Option<Result<String, String>> processStatements(List<String> stmts, Map<String, String[]> env) {
		for (var stmt : stmts) {
			Option<Result<String, String>> err;
			if (stmt.startsWith("let ")) {
				err = processSingleLet(stmt, env);
			} else if (isAssignmentStatement(stmt)) {
				err = processAssignment(stmt, env);
			} else if (stmt.startsWith("struct ")) {
				err = processStructDef(stmt, env);
			} else if (stmt.startsWith("while ") || stmt.startsWith("while(")) {
				err = processWhile(stmt, env);
			} else if (stmt.startsWith("fn ")) {
				err = processFunctionDef(stmt, env);
			} else {
				err = new Some<>(new Err<>("Invalid statement: " + stmt));
			}
			if (err instanceof Some<Result<String, String>>)
				return err;
		}
		return new None<>();
	}

	private static Option<Result<String, String>> processStructDef(String stmt, Map<String, String[]> env) {
		// minimal parser: struct Name { field : Type, ... }
		var parsed = parseNameAndInner(stmt, 6);
		if (!(parsed instanceof Some<String[]>(var pr)))
			return createErr("Invalid struct syntax");
		var name = pr[0];
		var inner = pr[1];
		// normalize fields as comma-separated without whitespace
		var fieldDecl = inner.replaceAll("\\s+", "");
		// If name is already bound (either as a value or a struct), treat as duplicate
		if (env.containsKey(name) || structRegistry.containsKey(name))
			return createErr("Duplicate binding");
		// store struct in registry
		structRegistry.put(name, new String[] { fieldDecl, "struct" });
		return new None<>();
	}

	private static Option<String[]> parseNameAndInner(String stmt, int prefixLen) {
		if (stmt.length() <= prefixLen)
			return new None<>();
		var rest = stmt.substring(prefixLen).trim();
		var nameEnd = rest.indexOf('{');
		if (nameEnd <= 0)
			return new None<>();
		var name = rest.substring(0, nameEnd).trim();
		var close = matchingClosingBraceIndex(rest);
		if (close != rest.length() - 1)
			return new None<>();
		var inner = rest.substring(nameEnd + 1, close).trim();
		return new Some<>(new String[] { name, inner });
	}

	private static int matchingClosingParenIndex(String s, int openPos) {
		var depth = 0;
		for (var i = openPos; i < s.length(); i++) {
			var c = s.charAt(i);
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

	private static Option<Result<String, String>> processWhile(String stmt, Map<String, String[]> env) {
		// parse while (cond) body
		var p = stmt.indexOf('(');
		if (p < 0)
			return createErr("Invalid while syntax");
		var close = matchingClosingParenIndex(stmt, p);
		if (close < 0)
			return createErr("Invalid while syntax");
		var cond = stmt.substring(p + 1, close).trim();
		var body = stmt.substring(close + 1).trim();
		if (body.isEmpty())
			return createErr("Missing while body");
		List<String> bodyStmts;
		if (body.startsWith("{") && matchingClosingBraceIndex(body) == body.length() - 1) {
			var inner = body.substring(1, body.length() - 1).trim();
			bodyStmts = splitNonEmptyStatements(inner);
		} else {
			bodyStmts = List.of(body);
		}
		// loop
		while (true) {
			var condBoolOpt = evaluateCondition(cond, env);
			if (!(condBoolOpt instanceof Some<Boolean>(var condBool)))
				return createErr("Invalid condition expression: '" + cond + "'");
			if (!condBool)
				break;
			var err = processStatements(bodyStmts, env);
			if (err instanceof Some<Result<String, String>>)
				return err;
		}
		return new None<>();
	}

	private static Option<Boolean> evaluateCondition(String cond, Map<String, String[]> env) {
		// Support single '<' comparison where operands may be identifiers or
		// expressions
		var idx = cond.indexOf('<');
		if (idx >= 0 && cond.indexOf('<', idx + 1) < 0) {
			var cmp = compareAt(cond, idx, env);
			if (!(cmp instanceof Some<Integer>(var cmpResult)))
				return new None<>();
			return new Some<>(cmpResult < 0);
		}
		// otherwise expect boolean-like expression or identifier
		var pairOpt = evaluateRhsExpression(cond, env);
		if (!(pairOpt instanceof Some<String[]>(var pair)))
			return new None<>();
		if ("true".equals(pair[0]))
			return new Some<>(true);
		if ("false".equals(pair[0]))
			return new Some<>(false);
		return new None<>();
	}

	private static Option<Result<String, String>> processSingleLet(String stmt, Map<String, String[]> env) {
		if (!stmt.startsWith("let "))
			return createErr("Expected 'let' declaration");
		var eq = stmt.indexOf('=', 4);
		var afterLet = stmt.substring(4, (eq > 4 ? eq : stmt.length())).trim();
		var isMutable = false;
		var lhs = afterLet;
		if (afterLet.startsWith("mut ")) {
			isMutable = true;
			lhs = afterLet.substring(4).trim();
		}
		var ident = extractIdentFromLhs(lhs);
		if (env.containsKey(ident))
			return createErr("Duplicate binding");
		var colonPos = lhs.indexOf(':');
		var declared = "";
		if (colonPos > 0)
			declared = lhs.substring(colonPos + 1).trim();
		// Declared types must not start with '&' (use & only in RHS expressions)
		if (declared.startsWith("&"))
			return createErr("Invalid declared type: '" + declared + "'");
		// If there is no '=', this is a declaration without initializer
		if (eq <= 4) {
			// must have declared type
			if (declared.isEmpty())
				return createErr("Missing declared type for variable '" + ident + "'");
			// Declaration without initializer: mark as deferred (assign-once) unless 'mut'
			// was present
			var mut = isMutable ? "mutable" : "deferred";
			var entry = new String[] { "", declared, mut };
			env.put(ident, entry);
			return new None<>();
		}
		var rhs = stmt.substring(eq + 1).trim();
		var evalResult = evaluateAndValidateRhs(rhs, declared, env);
		if (evalResult instanceof Some<Result<String, String>>)
			return evalResult;
		final var strings = evaluateRhsExpression(rhs, env);
		if (!(strings instanceof Some<String[]>(var pair)))
			return new None<>();
		// Safe since we validated above
		// Store [value, suffix, mutability]
		var entry = new String[] { pair[0], pair[1], isMutable ? "mutable" : "immutable" };
		env.put(ident, entry);
		return new None<>();
	}

	private static Option<Result<String, String>> evaluateAndValidateRhs(String rhs,
			String declared,
			Map<String, String[]> env) {
		var rhsResult = evaluateRhsExpression(rhs, env);
		if (!(rhsResult instanceof Some<String[]>(var pair)))
			return rhsError(rhs);
		var suffix = pair[1];
		// If declared is present and RHS has no suffix (e.g. literal like true), accept
		// it
		if (!Objects.isNull(declared) && !declared.isEmpty()) {
			if (Objects.isNull(suffix) || suffix.isEmpty()) {
				// Accept boolean literals without suffix (e.g., 'true'/'false') for a
				// declared type. For other cases (including function calls that return
				// an empty suffix), require an explicit suffix so compatibility checks can
				// detect mismatches.
				var v = pair[0];
				if ("true".equals(v) || "false".equals(v)) {
					return new None<>();
				}
				// otherwise fall through so that validateDeclaredCompatibility will
				// produce an error when suffix is missing
			}
		}
		if (isNotDeclaredCompatible(declared, suffix))
			return createErr("Declared type does not match expression suffix");
		return new None<>();
	}

	private static Option<String[]> evaluateRhsExpression(String rhs, Map<String, String[]> env) {
		if (rhs.isEmpty())
			return new None<>();
		return parseRhsPair(rhs, env);
	}

	private static Option<String[]> parseRhsPair(String rhs, Map<String, String[]> env) {
		if (rhs.startsWith("&")) {
			// support &mut and & (immutable) references
			if (rhs.startsWith("&mut")) {
				var pointee = rhs.substring(4).trim();
				if (!env.containsKey(pointee))
					return new None<>();
				var pointeeSuffix = env.get(pointee)[1];
				return new Some<>(new String[] { pointee, "*mut " + pointeeSuffix });
			}
			var pointee = rhs.substring(1).trim();
			if (!env.containsKey(pointee))
				return new None<>();
			var pointeeSuffix = env.get(pointee)[1];
			return new Some<>(new String[] { pointee, "*" + pointeeSuffix });
		}
		var rhsOpt = evaluateSingleWithSuffix(rhs);
		if (rhsOpt instanceof Some<String[]>)
			return rhsOpt;
		// function call form: name(arg1, arg2, ...)
		var p = rhs.indexOf('(');
		if (p > 0) {
			var close = matchingClosingParenIndex(rhs, p);
			if (close == rhs.length() - 1) {
				var name = rhs.substring(0, p).trim();
				if (!name.contains(" ")) {
					var argsRaw = rhs.substring(p + 1, close).trim();
					if (argsRaw.isEmpty())
						return evaluateFunctionCall(name, env);
					var argPairs = parseAndEvalArgPairs(argsRaw, env);
					if (argPairs.isEmpty())
						return new None<>();
					return evaluateFunctionCall(name, argPairs, env);
				}
			}
		}
		if (!env.containsKey(rhs))
			return new None<>();
		// Return only [value, suffix] from environment entry
		var entry = env.get(rhs);
		return new Some<>(new String[] { entry[0], entry[1] });
	}

	private static String[] splitTopLevelArgs(String s) {
		if (Objects.isNull(s) || s.isEmpty())
			return new String[0];
		var segments = new java.util.ArrayList<String>();
		var pos = 0;
		var lvl = 0;
		for (var idx = 0; idx < s.length(); idx++) {
			var ch = s.charAt(idx);
			if (ch == '(')
				lvl++;
			else if (ch == ')')
				lvl--;
			else if (ch == ',' && lvl == 0) {
				segments.add(s.substring(pos, idx).trim());
				pos = idx + 1;
			}
		}
		segments.add(s.substring(pos).trim());
		return segments.toArray(new String[0]);
	}

	private static Option<String[]> normalizePairOpt(Option<String[]> opt) {
		if (opt instanceof Some<String[]>(var pair))
			return new Some<>(new String[] { pair[0], pair[1] });
		return new None<>();
	}

	private static Option<String[]> evaluateRhsExpressionWithLocal(String expr, Map<String, String[]> outerEnv,
			Map<String, String[]> local) {
		if (local.containsKey(expr))
			return new Some<>(new String[] { local.get(expr)[0], local.get(expr)[1] });
		// Merge local and outer env with locals taking precedence
		var merged = new HashMap<String, String[]>();
		if (!Objects.isNull(outerEnv))
			merged.putAll(outerEnv);
		merged.putAll(local);
		// Handle simple top-level plus expressions specially so that operands can be
		// resolved from the merged env (e.g., 'first + second')
		var plusIdx = expr.indexOf('+');
		if (plusIdx >= 0 && expr.indexOf('+', plusIdx + 1) < 0) {
			var left = expr.substring(0, plusIdx).trim();
			var right = expr.substring(plusIdx + 1).trim();
			var leftOpt = evaluateRhsExpression(left, merged);
			if (!(leftOpt instanceof Some<String[]>(var leftPair)))
				return new None<>();
			var rightOpt = evaluateRhsExpression(right, merged);
			if (!(rightOpt instanceof Some<String[]>(var rightPair)))
				return new None<>();
			var sumOpt = parseAndSumStrings(leftPair[0], rightPair[0], leftPair[1], rightPair[1]);
			if (sumOpt instanceof Some<PlusOperands>(var po))
				return new Some<>(new String[] { po.sum, po.leftSuffix });
			return new None<>();
		}
		return evaluateRhsExpression(expr, merged);
	}

	private static Option<Result<String, String>> processFunctionDef(String stmt, Map<String, String[]> env) {
		// minimal parser: fn name(params) : Type => { body }
		var rest = stmt.substring(2).trim();
		// allow optional generics on function name, e.g. name<T>
		var nameEnd = rest.indexOf('(');
		if (nameEnd <= 0)
			return createErr("Invalid function syntax");
		var rawName = rest.substring(0, nameEnd).trim();
		// strip any generic parameters like name<T> -> name
		var genStart = rawName.indexOf('<');
		var name = genStart > 0 ? rawName.substring(0, genStart).trim() : rawName;
		var close = matchingClosingParenIndex(rest, nameEnd);
		if (close < 0)
			return createErr("Invalid function syntax");
		var paramsRaw = rest.substring(nameEnd + 1, close).trim();
		var arrow = rest.indexOf("=>", close);
		if (arrow < 0)
			return createErr("Invalid function syntax");
		var between = rest.substring(close + 1, arrow).trim();
		var body = rest.substring(arrow + 2).trim();
		var paramDecl = paramsRaw.isEmpty() ? "" : paramsRaw.replaceAll("\\s+", "");
		// Validate duplicate parameter names at function-definition time
		if (!paramsRaw.isEmpty()) {
			var seen = new java.util.HashSet<String>();
			var parts = paramsRaw.split(",");
			for (var p : parts) {
				var colon = p.indexOf(':');
				var pname = colon > 0 ? p.substring(0, colon).trim() : p.trim();
				if (seen.contains(pname))
					return createErr("Duplicate parameter");
				seen.add(pname);
			}
		}
		// optional return type between params and =>, e.g. ": I32"
		var returnType = "";
		if (between.startsWith(":")) {
			returnType = between.substring(1).trim();
		}
		// Store the function body as a special entry in env with suffix "fn",
		// paramDecl and optional return type
		// If the name is already bound, behave like duplicate let-binding and return an
		// error
		if (env.containsKey(name))
			return createErr("Duplicate binding");
		env.put(name, new String[] { body, "fn", paramDecl, returnType });
		return new None<>();
	}

	private static Option<String[]> evaluateFunctionCall(String name, Map<String, String[]> env) {
		// Delegate to the argument-list overload with empty args to avoid duplication
		return evaluateFunctionCall(name, new java.util.ArrayList<String[]>(), env);
	}

	private static java.util.List<String[]> parseAndEvalArgPairs(String argsRaw, Map<String, String[]> env) {
		var argExprs = splitTopLevelArgs(argsRaw);
		var argPairs = new java.util.ArrayList<String[]>();
		for (var a : argExprs) {
			if (Objects.isNull(a) || a.isEmpty())
				continue;
			var ap = evaluateRhsExpression(a, env);
			if (!(ap instanceof Some<String[]>(var pair)))
				return java.util.Collections.emptyList();
			argPairs.add(pair);
		}
		return argPairs;
	}

	private static Option<String[]> evaluateFunctionCall(String name, java.util.List<String[]> argPairs,
			Map<String, String[]> env) {
		if (!env.containsKey(name))
			return new None<>();
		var entry = env.get(name);
		if (!"fn".equals(entry[1]))
			return new None<>();
		var body = entry[0];
		var paramDecl = entry.length > 2 ? entry[2] : "";
		// Bind parameters into a local environment; validation occurs in helper
		var bindOpt = bindFunctionParameters(paramDecl, argPairs);
		if (!(bindOpt instanceof Some<Map<String, String[]>>(var local)))
			return new None<>();
		var callRes = executeFunctionBody(body, env, local);
		// If the function declared a return type (stored at index 3), and the
		// evaluated body returned a value without a suffix, propagate the declared
		// return type as the call-site suffix so callers can perform type checks.
		var declaredRet = entry.length > 3 ? entry[3] : "";
		if (callRes instanceof Some<String[]>(var pair)) {
			if (!Objects.isNull(declaredRet) && !declaredRet.isEmpty()
					&& (Objects.isNull(pair[1]) || pair[1].isEmpty())) {
				return new Some<>(new String[] { pair[0], declaredRet });
			}
			return new Some<>(new String[] { pair[0], pair[1] });
		}
		return new None<>();
	}

	private static Option<Map<String, String[]>> bindFunctionParameters(String paramDecl,
			java.util.List<String[]> argPairs) {
		var local = new HashMap<String, String[]>();
		if (Objects.isNull(paramDecl) || paramDecl.isEmpty() || argPairs.isEmpty())
			return new Some<>(local);
		var params = paramDecl.split(",");
		for (var idx = 0; idx < params.length && idx < argPairs.size(); idx++) {
			var pd = params[idx];
			var colon = pd.indexOf(':');
			var paramName = colon > 0 ? pd.substring(0, colon).trim() : pd.trim();
			// reject duplicate parameter names
			if (local.containsKey(paramName))
				return new None<>();
			var declaredType = colon > 0 ? pd.substring(colon + 1).trim() : "";
			var firstArg = argPairs.get(idx);
			if (!isParamCompatible(declaredType, firstArg))
				return new None<>();
			local.put(paramName, new String[] { firstArg[0], firstArg[1], "immutable" });
		}
		return new Some<>(local);
	}

	private static boolean isParamCompatible(String declaredType, String[] arg) {
		if (Objects.isNull(declaredType) || declaredType.isEmpty())
			return true;
		var argSuffix = arg[1];
		if (!Objects.isNull(argSuffix) && !argSuffix.isEmpty()) {
			return !isNotDeclaredCompatible(declaredType, argSuffix);
		}
		var v = arg[0];
		if ("true".equals(v) || "false".equals(v))
			return declaredType.equals("Bool");
		return true;
	}

	private static Option<String[]> executeFunctionBody(String body, Map<String, String[]> env,
			Map<String, String[]> local) {
		if (body.startsWith("{") && body.endsWith("}")) {
			var inner = body.substring(1, body.length() - 1).trim();
			// Allow a single 'return' statement inside the braced body. Accept both
			// forms with and without a trailing semicolon (e.g. "return 100;" and
			// "return 100") but reject multi-statement bodies here.
			var stmts = splitNonEmptyStatements(inner);
			if (stmts.size() == 1 && stmts.get(0).startsWith("return ")) {
				var retStmt = stmts.get(0).substring(7).trim();
				// strip optional trailing semicolon if present
				if (retStmt.endsWith(";"))
					retStmt = retStmt.substring(0, retStmt.length() - 1).trim();
				var res = evaluateRhsExpressionWithLocal(retStmt, env, local);
				return normalizePairOpt(res);
			}
			var res = runSequence(inner);
			if (res instanceof Ok(var value))
				return new Some<>(new String[] { String.valueOf(value), "" });
			return new None<>();
		}
		// For expression-bodied functions, evaluate the expression with the local
		// parameter bindings so identifiers like parameter names resolve.
		var resOpt = evaluateRhsExpressionWithLocal(body, env, local);
		return normalizePairOpt(resOpt);
	}

	private static Option<Result<String, String>> rhsError(String rhs) {
		return new Some<>(new Err<>("Invalid RHS expression: '" + rhs + "'"));
	}

	private static Option<Result<String, String>> createErr(String msg) {
		return new Some<>(new Err<>(msg));
	}

	private static boolean isNotDeclaredCompatible(String declared, String suffix) {
		if (Objects.isNull(declared) || declared.isEmpty())
			return false;
		if (declared.startsWith("*")) {
			var baseDeclared = declared.substring(1).trim();
			var pointeeSuffix = suffix.startsWith("*") ? suffix.substring(1) : suffix;
			return !checkBasePointeeCompatibility(baseDeclared, pointeeSuffix);
		}
		return !declared.equals(suffix);
	}

	private static boolean checkBasePointeeCompatibility(String baseDeclared, String pointeeSuffix) {
		if (baseDeclared.isEmpty() || pointeeSuffix.isEmpty())
			return true;
		// Normalize and handle 'mut' prefix differences like "mut I32" vs "mut "
		var b = baseDeclared.trim();
		var p = pointeeSuffix.trim();
		if (b.startsWith("mut") && p.startsWith("mut")) {
			var bRest = b.substring(3).trim();
			var pRest = p.substring(3).trim();
			if (bRest.isEmpty() || pRest.isEmpty())
				return true;
			return bRest.equals(pRest);
		}
		return b.equals(p);
	}

	private static boolean isAssignmentStatement(String stmt) {
		// Assignment: ident = expr or compound forms like ident += expr
		if (stmt.startsWith("let "))
			return false;
		// detect compound assignment operator first (e.g., "+=")
		var plusEq = stmt.indexOf("+=");
		if (plusEq > 0) {
			var lhs = stmt.substring(0, plusEq).trim();
			return lhs.matches("[a-zA-Z_][a-zA-Z0-9_]*");
		}
		var eq = stmt.indexOf('=');
		if (eq <= 0)
			return false;
		var lhs = stmt.substring(0, eq).trim();
		// Simple identifier check - should not contain spaces or special chars except
		// ':'
		return lhs.matches("[a-zA-Z_][a-zA-Z0-9_]*");
	}

	private static Option<Result<String, String>> processAssignment(String stmt, Map<String, String[]> env) {
		// Support compound assignment like += in addition to simple '='
		var plusEq = stmt.indexOf("+=");
		boolean isCompoundPlus = plusEq > 0;
		var opPos = isCompoundPlus ? plusEq : stmt.indexOf('=');
		if (opPos <= 0)
			return createErr("Invalid assignment syntax");
		var ident = stmt.substring(0, opPos).trim();
		if (!env.containsKey(ident))
			return createErr("Unknown identifier: '" + ident + "'");
		var entry = env.get(ident);
		var mutFlag = entry[2];
		var lhsSuffix = entry[1];
		if (!isLhsAssignable(entry))
			return createErr("Assignment target is not assignable");
		var rhs = stmt.substring(opPos + (isCompoundPlus ? 2 : 1)).trim();
		var rhsEval = evaluateRhsExpression(rhs, env);
		if (!(rhsEval instanceof Some<String[]>(var pair)))
			return rhsError(rhs);
		var rhsSuffix = pair[1];
		var declaredSuffix = entry[1];
		var compatErr = validateDeclaredCompatibility(declaredSuffix, rhsSuffix);
		if (compatErr instanceof Some<Result<String, String>>)
			return compatErr;

		// Handle compound '+=' specially (extracted to helper to reduce complexity)
		if (isCompoundPlus) {
			return handleCompoundPlusAssignment(ident, entry, pair, env);
		}

		// If the LHS is a pointer variable with '*mut' suffix, update the pointee
		// instead
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut")) {
			return handleDerefAssignment(entry, pair, env);
		}
		// Determine new mutability: deferred -> immutable after first assignment;
		// mutable remains mutable
		env.put(ident, makeNewEntry(pair[0], pair[1], mutFlag));
		return new None<>();
	}

	private static String[] makeNewEntry(String value, String suffix, String mutFlag) {
		var newMut = "immutable".equals(mutFlag) ? "immutable" : ("deferred".equals(mutFlag) ? "immutable" : "mutable");
		return new String[] { value, suffix, newMut };
	}

	private static Option<Result<String, String>> handleDerefAssignment(String[] pointerEntry,
			String[] rhsPair,
			Map<String, String[]> env) {
		// pointerEntry[0] holds the pointee name
		var pointeeName = pointerEntry[0];
		if (Objects.isNull(pointeeName) || pointeeName.isEmpty())
			return createErr("Pointer target not specified");
		if (!env.containsKey(pointeeName))
			return createErr("Pointer target not found: '" + pointeeName + "'");
		var pointeeEntry = env.get(pointeeName);
		// update pointee value and set its suffix to RHS suffix, keep mutability
		var updated = new String[] { rhsPair[0], rhsPair[1], pointeeEntry[2] };
		env.put(pointeeName, updated);
		return new None<>();
	}

	private static boolean isLhsAssignable(String[] entry) {
		var mutFlag = entry[2];
		var lhsSuffix = entry[1];
		// If the LHS is a pointer variable with '*mut' suffix, allow deref-assignment
		if (!Objects.isNull(lhsSuffix) && lhsSuffix.startsWith("*mut"))
			return true;
		return "mutable".equals(mutFlag) || "deferred".equals(mutFlag);
	}

	private static Option<Result<String, String>> validateDeclaredCompatibility(String declaredSuffix, String rhsSuffix) {
		if (!Objects.isNull(declaredSuffix) && !declaredSuffix.isEmpty()) {
			// If RHS has a suffix, enforce compatibility; if RHS suffix is empty, accept
			// and rely on declared type
			if (!Objects.isNull(rhsSuffix) && !rhsSuffix.isEmpty()) {
				if (isNotDeclaredCompatible(declaredSuffix, rhsSuffix))
					return new Some<>(new Err<>("Declared type does not match expression suffix"));
			}
		}
		return new None<>();
	}

	private static Result<String, String> evaluateFinal(String stmt, Map<String, String[]> env) {
		var br = evaluateBracedFinal(stmt, env);
		if (br instanceof Some<Result<String, String>>(var value)) {
			return value;
		}
		// if it's an identifier, return its value from env
		if (env.containsKey(stmt)) {
			var entry = env.get(stmt);
			// If the binding exists but has empty value and a declared suffix, treat as
			// uninitialized
			var val = entry[0];
			var declaredSuffix = entry[1];
			if ((Objects.isNull(val) || val.isEmpty()) && !Objects.isNull(declaredSuffix) && !declaredSuffix.isEmpty()) {
				return new Err<>("Uninitialized variable '" + stmt + "'");
			}
			return new Ok<>(entry[0]);
		}
		// pointer dereference expression like *y
		if (stmt.startsWith("*")) {
			var ref = stmt.substring(1).trim();
			if (!env.containsKey(ref))
				return new Err<>("Unknown pointer variable: '" + ref + "'");
			var target = env.get(ref)[0];
			if (env.containsKey(target)) {
				var entry = env.get(target);
				return new Ok<>(entry[0]);
			}
			return new Err<>("Non-empty input not allowed");
		}
		// otherwise evaluate as single expression
		var funcTry = tryEvaluateFunctionCallFinal(stmt, env);
		if (funcTry instanceof Some<Result<String, String>>(var f))
			return f;
		return evaluateSingle(stmt);
	}

	private static Option<Result<String, String>> tryEvaluateFunctionCallFinal(String stmt, Map<String, String[]> env) {
		var p = stmt.indexOf('(');
		if (p <= 0)
			return new None<>();
		var close = matchingClosingParenIndex(stmt, p);
		if (close != stmt.length() - 1)
			return new None<>();
		var name = stmt.substring(0, p).trim();
		if (name.contains(" "))
			return new None<>();
		var argsRaw = stmt.substring(p + 1, close).trim();
		if (argsRaw.isEmpty()) {
			var call = evaluateFunctionCall(name, env);
			return makeFinalResultFromCallPair(call, stmt);
		}
		var argPairs = parseAndEvalArgPairs(argsRaw, env);
		if (argPairs.isEmpty())
			return new Some<>(new Err<>("Invalid expression: '" + stmt + "'"));
		var call = evaluateFunctionCall(name, argPairs, env);
		return makeFinalResultFromCallPair(call, stmt);
	}

	private static Option<Result<String, String>> makeFinalResultFromCallPair(Option<String[]> callOpt, String stmt) {
		if (callOpt instanceof Some<String[]>(var pair))
			return new Some<>(new Ok<>(pair[0]));
		return new Some<>(new Err<>("Invalid expression: '" + stmt + "'"));
	}

	private static Option<Result<String, String>> evaluateBracedFinal(String stmt, Map<String, String[]> env) {
		if (!stmt.startsWith("{") || matchingClosingBraceIndex(stmt) != stmt.length() - 1)
			return new None<>();
		var inner = stmt.substring(1, stmt.length() - 1).trim();
		var nonEmpty = splitNonEmptyStatements(inner);
		if (nonEmpty.isEmpty())
			return new Some<>(new Ok<>(""));
		var maybe = handleAllBindingsCase(nonEmpty, env);
		if (maybe instanceof Some<Result<String, String>>)
			return maybe;
		var buildErr = processNonFinalStatements(nonEmpty, env);
		if (buildErr instanceof Some<Result<String, String>>)
			return buildErr;
		return new Some<>(evaluateFinal(nonEmpty.getLast(), env));
	}

	private static Result<String, String> evaluateSingle(String s) {
		var resOpt = evaluateArithmeticOrLeading(s);
		if (resOpt instanceof Some<Result<String, String>>(var value)) {
			return value;
		}
		// try single-expression forms that also return a suffix (booleans, if-expr,
		// leading with suffix)
		var pairOpt = evaluateSingleWithSuffix(s);
		if (pairOpt instanceof Some<String[]>(var value)) {
			return new Ok<>(value[0]);
		}
		return new Err<>("Invalid expression: '" + s + "'");
	}

	/**
	 * Evaluate the single expression and also return the suffix of the resulting
	 * value if present. Returns Optional of String[2] where [0]=value and
	 * [1]=suffix
	 * (empty string if none). Returns empty Optional if evaluation failed.
	 */
	private static Option<String[]> evaluateSingleWithSuffix(String s) {
		// Check for comparison operators (e.g., '<') before numeric/leading parsing
		var cmpOpt = evaluateComparisonWithSuffix(s);
		if (cmpOpt instanceof Some<String[]>)
			return cmpOpt;

		// Try arithmetic or leading-digit parsing and also provide suffix
		var arithOpt = evaluateArithmeticWithSuffix(s);
		if (arithOpt instanceof Some<String[]>)
			return arithOpt;
		// leading digits case
		var leading = extractLeadingDigits(s);
		if (leading instanceof Some<String[]>(var value)) {
			return new Some<>(new String[] { value[0], value[1] });
		}

		// boolean literals
		if ("true".equals(s) || "false".equals(s)) {
			return new Some<>(new String[] { s, "" });
		}
		var ifOpt = evaluateIfWithSuffix(s);
		if (ifOpt instanceof Some<String[]>)
			return ifOpt;
		// function call as a bare final expression (name())
		if (s.endsWith("()") && s.indexOf(' ') < 0) {
			var name = s.substring(0, s.length() - 2).trim();
			return evaluateFunctionCall(name, Map.of());
		}

		// struct constructor with immediate field access: Name { ... }.field
		var constructorFieldOpt = evaluateConstructorFieldAccess(s);
		if (constructorFieldOpt instanceof Some<String[]>)
			return constructorFieldOpt;
		return evaluateBracedWithSuffix(s);
	}

	private static Option<String[]> evaluateIfWithSuffix(String s) {
		if (!s.startsWith("if ("))
			return new None<>();
		var close = s.indexOf(')', 4);
		if (close <= 4)
			return new None<>();
		var condExpr = s.substring(4, close).trim();
		var rest = s.substring(close + 1).trim();
		var elseIdx = rest.indexOf("else");
		if (elseIdx <= 0)
			return new None<>();
		var thenExpr = rest.substring(0, elseIdx).trim();
		var elseExpr = rest.substring(elseIdx + 4).trim();
		var condPairOpt = evaluateSingleWithSuffix(condExpr);
		if (condPairOpt instanceof Some<String[]>(var value)) {
			var condVal = value[0];
			var takeThen = "true".equals(condVal);
			var chosen = takeThen ? thenExpr : elseExpr;
			return evaluateSingleWithSuffix(chosen);
		}
		return new None<>();
	}

	private static Option<String[]> evaluateBracedWithSuffix(String s) {
		if (!s.startsWith("{") || !s.endsWith("}"))
			return new None<>();
		var inner = s.substring(1, s.length() - 1).trim();
		if (inner.isEmpty())
			return new None<>();
		if (inner.contains(";") || inner.startsWith("let ")) {
			var res = runSequence(inner);
			if (res instanceof Ok(var value)) {
				return new Some<>(new String[] { String.valueOf(value), "" });
			}
			return new None<>();
		}
		return evaluateSingleWithSuffix(inner);
	}

	private static Option<Integer> tryCompareInts(String left, String right, Map<String, String[]> env) {
		var lValOpt = evalInt(left, env);
		if (!(lValOpt instanceof Some<Integer>(var lVal)))
			return new None<>();
		var rValOpt = evalInt(right, env);
		if (!(rValOpt instanceof Some<Integer>(var rVal)))
			return new None<>();
		return new Some<>(Integer.compare(lVal, rVal));
	}

	private static Option<Integer> evalInt(String expr, Map<String, String[]> env) {
		Option<String[]> opt;
		if (Objects.isNull(env)) {
			opt = evaluateSingleWithSuffix(expr);
		} else {
			opt = evaluateRhsExpression(expr, env);
		}
		if (!(opt instanceof Some<String[]>(var pair)))
			return new None<>();
		try {
			return new Some<>(Integer.parseInt(pair[0]));
		} catch (NumberFormatException ex) {
			return new None<>();
		}
	}

	private static Option<String[]> evaluateComparisonWithSuffix(String s) {
		// Only support single '<' comparison for now
		var idx = s.indexOf('<');
		if (idx < 0)
			return new None<>();
		// ensure single '<'
		if (s.indexOf('<', idx + 1) >= 0)
			return new None<>();
		var cmp = compareAt(s, idx, Map.of());
		if (!(cmp instanceof Some<Integer>(var cmpResult)))
			return new None<>();
		var res = cmpResult < 0 ? "true" : "false";
		return new Some<>(new String[] { res, "" });
	}

	private static Option<Integer> tryCompareInts(String left, String right) {
		var lOpt = evaluateSingleWithSuffix(left);
		var rOpt = evaluateSingleWithSuffix(right);
		if (!(lOpt instanceof Some<String[]>(var lpair)))
			return new None<>();
		if (!(rOpt instanceof Some<String[]>(var rpair)))
			return new None<>();
		try {
			var l = Integer.parseInt(lpair[0]);
			var r = Integer.parseInt(rpair[0]);
			return new Some<>(Integer.compare(l, r));
		} catch (NumberFormatException ex) {
			return new None<>();
		}
	}

	private static Option<String[]> parseComparisonOperands(String s, int idx) {
		var left = s.substring(0, idx).trim();
		var right = s.substring(idx + 1).trim();
		if (left.isEmpty() || right.isEmpty())
			return new None<>();
		return new Some<>(new String[] { left, right });
	}

	private static Option<Integer> compareAt(String s, int idx, Map<String, String[]> env) {
		var opsOpt = parseComparisonOperands(s, idx);
		if (!(opsOpt instanceof Some<String[]>(var ops)))
			return new None<>();
		if (Objects.isNull(env))
			return tryCompareInts(ops[0], ops[1]);
		return tryCompareInts(ops[0], ops[1], env);
	}

	private static int matchingClosingBraceIndex(String s) {
		var depth = 0;
		for (var pos = 0; pos < s.length(); pos++) {
			var ch = s.charAt(pos);
			depth = updateBraceDepth(depth, ch);
			if (isClosingBraceAtTopLevel(ch, depth)) {
				return pos;
			}
		}
		return -1;
	}

	private static boolean isClosingBraceAtTopLevel(char ch, int depth) {
		return ch == '}' && depth == 0;
	}

	// Helper that consolidates arithmetic and leading-digit handling returning
	// Optional<Result> empty when not applicable. This removes duplication detected
	// by CPD.
	private static Option<Result<String, String>> evaluateArithmeticOrLeading(String s) {
		// arithmetic case handled by shared parser
		var opOpt = parsePlusOperands(s);
		if (opOpt instanceof Some<PlusOperands>(var value)) {
			if (!value.leftSuffix.equals(value.rightSuffix)) {
				return new Some<>(new Err<>("Mismatched operand suffixes"));
			}
			return new Some<>(new Ok<>(value.sum));
		}
		// leading digits
		var leading = extractLeadingDigits(s);
		return switch (leading) {
			case None<String[]> _ -> new None<>();
			case Some<String[]>(String[] value) -> new Some<>(new Ok<>(value[0]));
		};
	}

	// Helper that evaluates arithmetic and returns Optional of [value,suffix]
	// empty when not applicable
	private static Option<String[]> evaluateArithmeticWithSuffix(String s) {
		var opOpt = parsePlusOperands(s);
		if (!(opOpt instanceof Some<PlusOperands>(var op)))
			return new None<>();
		if (!op.leftSuffix.equals(op.rightSuffix))
			return new None<>();
		return new Some<>(new String[] { op.sum, op.leftSuffix });
	}

	// Parse a plus expression like "1U8 + 2U8" and return sum and suffixs when
	// parsable
	private static Option<PlusOperands> parsePlusOperands(String s) {
		var plusIndex = s.indexOf('+');
		if (plusIndex >= 0 && plusIndex == s.lastIndexOf('+')) {
			var left = s.substring(0, plusIndex).trim();
			var right = s.substring(plusIndex + 1).trim();
			var leftNum = leadingInteger(left);
			var rightNum = leadingInteger(right);
			if (!leftNum.isEmpty() && !rightNum.isEmpty()) {
				var leftSuffix = left.substring(leftNum.length());
				var rightSuffix = right.substring(rightNum.length());
				return parseAndSumStrings(leftNum, rightNum, leftSuffix, rightSuffix);
			}
			// Try evaluating each side as a full expression (handles braced expressions)
			var leftPairOpt = evaluateSingleWithSuffix(left);
			var rightPairOpt = evaluateSingleWithSuffix(right);
			if (leftPairOpt instanceof Some<String[]>(var leftResult)) {
				if (rightPairOpt instanceof Some<String[]>(var rightResult)) {
					var leftVal = leftResult[0];
					var rightVal = rightResult[0];
					var leftSuffix = leftResult[1];
					var rightSuffix = rightResult[1];
					return parseAndSumStrings(leftVal, rightVal, leftSuffix, rightSuffix);
				}
			}
		}
		return new None<>();
	}

	private static String leadingInteger(String s) {
		if (Objects.isNull(s) || s.isEmpty()) {
			return "";
		}
		var idx = 0;
		var c = s.charAt(0);
		if (c == '+' || c == '-') {
			idx = 1;
		}
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		return s.substring(0, idx);
	}

	private static Option<String[]> extractLeadingDigits(String s) {
		if (Objects.isNull(s) || s.isEmpty())
			return new None<>();
		var i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i))) {
			i++;
		}
		if (i > 0) {
			var num = s.substring(0, i);
			var suffix = s.substring(i);
			return new Some<>(new String[] { num, suffix });
		}
		return new None<>();
	}

	private static String extractIdentFromLhs(String lhs) {
		var ident = lhs;
		var colon = lhs.indexOf(':');
		if (colon > 0) {
			ident = lhs.substring(0, colon).trim();
		}
		return ident;
	}

	// Helpers for struct storage in env
	private static boolean envContainsStruct(String name) {
		// env is passed around; to resolve struct entries we must search a global
		// context. For the local evaluation we will rely on the caller to provide
		// an env map; however many call sites here don't have it. To keep this
		// helper simple and safe, we'll create a fresh env at usage time by reusing
		// the top-level runSequence env via a temporary empty map lookup - but the
		// evaluateSingleWithSuffix usage calls this in a context where env isn't
		// directly available. Instead, to avoid broad refactors, we'll attempt to
		// read struct entries from a shared registry stored in a static map.
		return structRegistry.containsKey(name);
	}

	private static String[] getStructEntry(String name) {
		return structRegistry.get(name);
	}

	private static final java.util.Map<String, String[]> structRegistry = new java.util.HashMap<>();

	private static Option<String[]> evaluateConstructorFieldAccess(String s) {
		var dotIdx = s.indexOf('.');
		if (dotIdx <= 0)
			return new None<>();
		var left = s.substring(0, dotIdx).trim();
		var field = s.substring(dotIdx + 1).trim();
		if (!(left.contains("{") && left.endsWith("}")))
			return new None<>();
		var p = left.indexOf('{');
		var name = left.substring(0, p).trim();
		var body = left.substring(p + 1, left.length() - 1).trim();
		if (!envContainsStruct(name))
			return new None<>();
		var structEntry = getStructEntry(name);
		if (Objects.isNull(structEntry))
			return new None<>();
		var fieldDeclObj = structEntry[0];
		if (Objects.isNull(fieldDeclObj))
			return new None<>();
		var fieldDecl = fieldDeclObj;
		var fields = fieldDecl.isEmpty() ? new String[0] : fieldDecl.split(",");
		var fieldNames = parseFieldNames(fields);
		if (fieldNames.isEmpty())
			return new None<>();
		var argPairs = parseAndEvalArgPairs(body, Map.of());
		if (argPairs.isEmpty())
			return new None<>();
		if (argPairs.size() != fieldNames.size())
			return new None<>();
		if (!validateConstructorArgs(fields, argPairs))
			return new None<>();

		// end of evaluateConstructorFieldAccess
		var idx = fieldNames.indexOf(field);
		if (idx < 0)
			return new None<>();
		var selected = argPairs.get(idx);
		return new Some<>(new String[] { selected[0], selected[1] });
	}

	private static java.util.List<String> parseFieldNames(String[] fields) {
		var fieldNames = new java.util.ArrayList<String>();
		for (var f : fields) {
			var c = f.indexOf(':');
			var fname = c > 0 ? f.substring(0, c).trim() : f.trim();
			fieldNames.add(fname);
		}
		return fieldNames;
	}

	private static boolean validateConstructorArgs(String[] fields, java.util.List<String[]> argPairs) {
		for (var i = 0; i < fields.length; i++) {
			var fdecl = fields[i];
			var colon = fdecl.indexOf(':');
			var declaredType = colon > 0 ? fdecl.substring(colon + 1).trim() : "";
			if (!Objects.isNull(declaredType) && !declaredType.isEmpty()) {
				var argSuffix = argPairs.get(i)[1];
				if (Objects.isNull(argSuffix) || argSuffix.isEmpty()) {
					var v = argPairs.get(i)[0];
					if ("true".equals(v) || "false".equals(v)) {
						if (!declaredType.equals("Bool"))
							return false;
					} else if (isIntegerLiteral(v)) {
						if (!(declaredType.startsWith("I") || declaredType.startsWith("U")))
							return false;
					} else {
						return false;
					}
				} else {
					if (isNotDeclaredCompatible(declaredType, argSuffix))
						return false;
				}
			}
		}
		return true;
	}

	private static boolean isIntegerLiteral(String v) {
		if (Objects.isNull(v) || v.isEmpty())
			return false;
		var i = 0;
		var c = v.charAt(0);
		if (c == '+' || c == '-')
			i = 1;
		if (i >= v.length())
			return false;
		while (i < v.length()) {
			if (!Character.isDigit(v.charAt(i)))
				return false;
			i++;
		}
		return true;
	}
}