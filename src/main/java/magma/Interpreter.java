package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		String trimmed = input == null ? "" : input.trim();

		// strip matching outer braces, e.g. "{5}" -> "5"
		while (trimmed.length() >= 2 && trimmed.charAt(0) == '{' && trimmed.charAt(trimmed.length() - 1) == '}') {
			trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
		}

		// quick path: integer literal
		if (isInteger(trimmed)) {
			return new Ok<>(trimmed);
		}

		// quick path: boolean literal
		if (isBoolean(trimmed)) {
			return new Ok<>(trimmed);
		}

		Map<String, String> env = new HashMap<>();
		Map<String, Boolean> mutable = new HashMap<>();
		// struct definitions stored in env as special entries 'struct:def:Name' ->
		//
		// comma-separated field names

		String[] parts = splitTopLevelStatements(trimmed);
		AtomicReference<String> lastValue = new AtomicReference<>(null);
		for (String raw : parts) {
			String stmt = raw.trim();
			System.out.println("[DEBUG] Processing statement: '" + stmt + "'");
			if (stmt.isEmpty())
				continue;

			// struct or enum declaration: struct Name { ... } OR enum Name { ... }
			if (stmt.startsWith("struct ") || stmt.startsWith("enum ")) {
				boolean isEnum = stmt.startsWith("enum ");
				String rest = stmt.substring(isEnum ? 5 : 7).trim();
				String[] parsed = parseNameBodyRemainder(rest);
				if (parsed == null)
					return err(isEnum ? "Malformed enum" : "Malformed struct", input);
				String name = parsed[0];
				String body = parsed[1];
				String remainder = parsed[2];
				if (name.isEmpty())
					return err(isEnum ? "Malformed enum" : "Malformed struct", input);
				if (!isEnum) {
					String[] fieldParts = splitNames(body);
					java.util.List<String> fieldNames = new java.util.ArrayList<>();
					for (String fp : fieldParts) {
						String f = fp.trim();
						int colon = f.indexOf(':');
						String fname = colon == -1 ? f : f.substring(0, colon).trim();
						if (!fname.isEmpty())
							fieldNames.add(fname);
					}
					env.put("struct:def:" + name, String.join(",", fieldNames));
					System.out.println("[DEBUG] define struct " + name + " -> " + env.get("struct:def:" + name));
				} else {
					String[] variants = splitNames(body);
					java.util.List<String> varNames = new java.util.ArrayList<>();
					for (String v : variants) {
						String vn = v.trim();
						if (!vn.isEmpty()) {
							varNames.add(vn);
							// register fully-qualified variant name so evalExpr can look it up
							env.put(name + "." + vn, name + "." + vn);
						}
					}
					env.put("enum:def:" + name, String.join(",", varNames));
					System.out.println("[DEBUG] define enum " + name + " -> " + env.get("enum:def:" + name));
				}
				if (remainder.isEmpty())
					continue;
				stmt = remainder;
				// fallthrough to process remainder
			}

			// let declaration: let [mut] name = expr
			if (stmt.startsWith("let ")) {
				Result<String, InterpretError> r = handleLetDeclaration(stmt.substring(4).trim(), env, mutable, lastValue,
						input);
				if (r != null)
					return r;
				continue;
			}

			// while loop: while (cond) body (handle first to avoid confusing other checks)
			if (stmt.startsWith("while")) {
				int open = stmt.indexOf('(');
				if (open == -1)
					return err("Malformed while", input);
				int close = findMatchingParen(stmt, open);
				if (close == -1)
					return err("Malformed while", input);
				String cond = stmt.substring(open + 1, close).trim();
				String body = stmt.substring(close + 1).trim();
				if (body.isEmpty()) {
					// no-op body
					continue;
				}
				Result<String, InterpretError> r = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid while condition", "Invalid assignment in while body", "Invalid expression in while body",
						null);
				if (r != null)
					return r;
				continue;
			}

			// for loop: for(init; cond; incr) body
			if (stmt.startsWith("for")) {
				int open = stmt.indexOf('(');
				if (open == -1)
					return err("Malformed for", input);
				int close = findMatchingParen(stmt, open);
				if (close == -1)
					return err("Malformed for", input);
				String header = stmt.substring(open + 1, close).trim();
				// split header into three parts by top-level semicolons
				int depth = 0;
				int firstSemi = -1;
				int secondSemi = -1;
				for (int i = 0; i < header.length(); i++) {
					char c = header.charAt(i);
					if (c == '(')
						depth++;
					else if (c == ')')
						depth--;
					else if (c == ';' && depth == 0) {
						if (firstSemi == -1)
							firstSemi = i;
						else {
							secondSemi = i;
							break;
						}
					}
				}
				if (firstSemi == -1 || secondSemi == -1)
					return err("Malformed for header", input);
				String init = header.substring(0, firstSemi).trim();
				String cond = header.substring(firstSemi + 1, secondSemi).trim();
				String incr = header.substring(secondSemi + 1).trim();
				String body = stmt.substring(close + 1).trim();
				// execute init
				if (!init.isEmpty()) {
					if (init.startsWith("let ")) {
						String rest = init.substring(4).trim();
						Result<String, InterpretError> r = handleLetDeclaration(rest, env, mutable, lastValue, input);
						if (r != null)
							return r;
					} else if (!init.isEmpty()) {
						Result<String, InterpretError> rInit = executeSimpleOrExpression(init, env, mutable, lastValue, input,
								false,
								"Invalid init in for", "Invalid init in for");
						if (rInit != null)
							return rInit;
					}
				}
				// empty body -> continue
				if (body.isEmpty())
					continue;
				// loop
				java.util.function.Supplier<Result<String, InterpretError>> incrSupplier = null;
				if (!incr.isEmpty()) {
					incrSupplier = () -> executeSimpleOrExpression(incr, env, mutable, lastValue, input, true,
							"Invalid incr in for", "Invalid incr in for");
				}
				Result<String, InterpretError> rLoop = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid for condition", "Invalid assignment in for body", "Invalid expression in for body", incrSupplier);
				if (rLoop != null)
					return rLoop;
				continue;
			}

			// function declaration: fn name<typeParams>(params) => expr or fn name(params)
			// => expr
			if (stmt.startsWith("fn ")) {
				String rest = stmt.substring(3).trim();
				int open = rest.indexOf('(');
				if (open == -1)
					return err("Malformed function", input);
				int close = rest.indexOf(')', open);
				if (close == -1)
					return err("Malformed function", input);

				// Extract function name, handling type parameters like get<T>
				String nameWithTypeParams = rest.substring(0, open).trim();
				String name;
				int typeParamStart = nameWithTypeParams.indexOf('<');
				if (typeParamStart != -1) {
					// Function has type parameters like fn get<T>(x : T) => x
					name = nameWithTypeParams.substring(0, typeParamStart).trim();
					// For now, we ignore type parameters and just store the base name
				} else {
					// Regular function without type parameters
					name = nameWithTypeParams;
				}

				String params = rest.substring(open + 1, close).trim();
				int arrow = rest.indexOf("=>", close);
				if (arrow == -1)
					return err("Malformed function", input);
				String body = rest.substring(arrow + 2).trim();
				if (name.isEmpty() || body.isEmpty())
					return err("Malformed function", input);
				// store function body with parameters and a special prefix in env
				if (params.isEmpty()) {
					env.put(name, "fn:" + body);
				} else {
					// For parameterized functions, store as "fn:param1,param2:body"
					String[] paramNames = parseParameterNames(params);
					env.put(name, "fn:" + String.join(",", paramNames) + ":" + body);
				}
				System.out.println("[DEBUG] define fn " + name + " -> " + body);
				continue;
			}

			// impl block: impl StructName { ... }
			if (stmt.startsWith("impl ")) {
				String rest = stmt.substring(5).trim();
				int openBrace = rest.indexOf('{');
				if (openBrace == -1)
					return err("Malformed impl block", input);
				String structName = rest.substring(0, openBrace).trim();
				if (structName.isEmpty())
					return err("Malformed impl block", input);

				int closeBrace = findMatchingBrace(rest, openBrace);
				if (closeBrace == -1)
					return err("Malformed impl block", input);

				String body = rest.substring(openBrace + 1, closeBrace).trim();
				String remainder = rest.substring(closeBrace + 1).trim();
				System.out.println("[DEBUG] impl block remainder: '" + remainder + "'");

				// Parse method definitions in the impl block
				String[] methods = splitTopLevelStatements(body);
				for (String method : methods) {
					method = method.trim();
					if (method.isEmpty())
						continue;

					if (method.startsWith("fn ")) {
						String methodRest = method.substring(3).trim();
						int methodOpen = methodRest.indexOf('(');
						if (methodOpen == -1)
							return err("Malformed method in impl", input);
						int methodClose = methodRest.indexOf(')', methodOpen);
						if (methodClose == -1)
							return err("Malformed method in impl", input);

						String methodName = methodRest.substring(0, methodOpen).trim();
						String methodParams = methodRest.substring(methodOpen + 1, methodClose).trim();
						int methodArrow = methodRest.indexOf("=>", methodClose);
						if (methodArrow == -1)
							return err("Malformed method in impl", input);
						String methodBody = methodRest.substring(methodArrow + 2).trim();

						if (methodName.isEmpty() || methodBody.isEmpty())
							return err("Malformed method in impl", input);

						// Store method with struct name prefix: "impl:StructName:methodName"
						if (methodParams.isEmpty()) {
							env.put("impl:" + structName + ":" + methodName, "fn:" + methodBody);
						} else {
							String[] paramNames = parseParameterNames(methodParams);
							env.put("impl:" + structName + ":" + methodName, "fn:" + String.join(",", paramNames) + ":" + methodBody);
						}
						System.out.println("[DEBUG] define method " + structName + "." + methodName + " -> " + methodBody);
					}
				}
				if (remainder.isEmpty())
					continue;
				stmt = remainder;
				System.out.println("[DEBUG] Processing impl remainder as stmt: '" + stmt + "'");
				// fallthrough to process remainder - DON'T continue, let it process below
			}

			// let declaration: let [mut] name = expr
			if (stmt.startsWith("let ")) {
				Result<String, InterpretError> r = handleLetDeclaration(stmt.substring(4).trim(), env, mutable, lastValue,
						input);
				if (r != null)
					return r;
				continue;
			}

			// while loop: while (cond) body (handle first to avoid confusing other checks)
			if (stmt.startsWith("while")) {
				int open = stmt.indexOf('(');
				if (open == -1)
					return err("Malformed while", input);
				int close = findMatchingParen(stmt, open);
				if (close == -1)
					return err("Malformed while", input);
				String cond = stmt.substring(open + 1, close).trim();
				String body = stmt.substring(close + 1).trim();
				if (body.isEmpty()) {
					// no-op body
					continue;
				}
				Result<String, InterpretError> r = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid while condition", "Invalid assignment in while body", "Invalid expression in while body",
						null);
				if (r != null)
					return r;
				continue;
			}

			// for loop: for(init; cond; incr) body
			if (stmt.startsWith("for")) {
				int open = stmt.indexOf('(');
				if (open == -1)
					return err("Malformed for", input);
				int close = findMatchingParen(stmt, open);
				if (close == -1)
					return err("Malformed for", input);
				String header = stmt.substring(open + 1, close).trim();
				// split header into three parts by top-level semicolons
				int depth = 0;
				int firstSemi = -1;
				int secondSemi = -1;
				for (int i = 0; i < header.length(); i++) {
					char c = header.charAt(i);
					if (c == '(')
						depth++;
					else if (c == ')')
						depth--;
					else if (c == ';' && depth == 0) {
						if (firstSemi == -1)
							firstSemi = i;
						else {
							secondSemi = i;
							break;
						}
					}
				}
				if (firstSemi == -1 || secondSemi == -1)
					return err("Malformed for header", input);
				String init = header.substring(0, firstSemi).trim();
				String cond = header.substring(firstSemi + 1, secondSemi).trim();
				String incr = header.substring(secondSemi + 1).trim();
				String body = stmt.substring(close + 1).trim();
				// execute init
				if (!init.isEmpty()) {
					if (init.startsWith("let ")) {
						String rest = init.substring(4).trim();
						Result<String, InterpretError> r = handleLetDeclaration(rest, env, mutable, lastValue, input);
						if (r != null)
							return r;
					} else if (!init.isEmpty()) {
						Result<String, InterpretError> rInit = executeSimpleOrExpression(init, env, mutable, lastValue, input,
								false,
								"Invalid init in for", "Invalid init in for");
						if (rInit != null)
							return rInit;
					}
				}
				// empty body -> continue
				if (body.isEmpty())
					continue;
				// loop
				java.util.function.Supplier<Result<String, InterpretError>> incrSupplier = null;
				if (!incr.isEmpty()) {
					incrSupplier = () -> executeSimpleOrExpression(incr, env, mutable, lastValue, input, true,
							"Invalid incr in for", "Invalid incr in for");
				}
				Result<String, InterpretError> rLoop = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid for condition", "Invalid assignment in for body", "Invalid expression in for body", incrSupplier);
				if (rLoop != null)
					return rLoop;
				continue;
			}

			// function declaration: fn name<typeParams>(params) => expr or fn name(params)
			// => expr
			if (stmt.startsWith("fn ")) {
				String rest = stmt.substring(3).trim();
				int open = rest.indexOf('(');
				if (open == -1)
					return err("Malformed function", input);
				int close = rest.indexOf(')', open);
				if (close == -1)
					return err("Malformed function", input);

				// Extract function name, handling type parameters like get<T>
				String nameWithTypeParams = rest.substring(0, open).trim();
				String name;
				int typeParamStart = nameWithTypeParams.indexOf('<');
				if (typeParamStart != -1) {
					// Function has type parameters like fn get<T>(x : T) => x
					name = nameWithTypeParams.substring(0, typeParamStart).trim();
					// For now, we ignore type parameters and just store the base name
				} else {
					// Regular function without type parameters
					name = nameWithTypeParams;
				}

				String params = rest.substring(open + 1, close).trim();
				int arrow = rest.indexOf("=>", close);
				if (arrow == -1)
					return err("Malformed function", input);
				String body = rest.substring(arrow + 2).trim();
				if (name.isEmpty() || body.isEmpty())
					return err("Malformed function", input);
				// store function body with parameters and a special prefix in env
				if (params.isEmpty()) {
					env.put(name, "fn:" + body);
				} else {
					// For parameterized functions, store as "fn:param1,param2:body"
					String[] paramNames = parseParameterNames(params);
					env.put(name, "fn:" + String.join(",", paramNames) + ":" + body);
				}
				System.out.println("[DEBUG] define fn " + name + " -> " + body);
				continue;
			}

			// for any non-let, non-while statement delegate to helper to handle assignment,
			// post-increment, or expression
			Result<String, InterpretError> rMain = executeSimpleOrExpression(stmt, env, mutable, lastValue, input, false,
					"Invalid assignment expression for " + (splitNameExpr(stmt) == null ? "" : splitNameExpr(stmt)[0]),
					"Undefined expression: " + stmt);
			if (rMain != null)
				return rMain;
		}

		if (lastValue.get() != null) {
			return new Ok<>(lastValue.get());
		}

		return err("Undefined value", input);
	}

	private static String[] splitTopLevelStatements(String src) {
		if (src == null || src.isEmpty())
			return new String[0];
		java.util.List<String> parts = new java.util.ArrayList<>();
		int depthParen = 0;
		int depthBrace = 0;
		int last = 0;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c == '(')
				depthParen++;
			else if (c == ')')
				depthParen--;
			else if (c == '{')
				depthBrace++;
			else if (c == '}')
				depthBrace--;
			else if (c == ';' && depthParen == 0 && depthBrace == 0) {
				parts.add(src.substring(last, i));
				last = i + 1;
			}
		}
		if (last <= src.length())
			parts.add(src.substring(last));
		return parts.stream().map(String::trim).toArray(String[]::new);
	}

	private static int findMatchingParen(String s, int openIndex) {
		return findMatching(s, openIndex, '(', ')');
	}

	private static int findMatchingBrace(String s, int openIndex) {
		return findMatching(s, openIndex, '{', '}');
	}

	private static int findMatching(String s, int openIndex, char openChar, char closeChar) {
		int depth = 1;
		for (int i = openIndex + 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == openChar)
				depth++;
			else if (c == closeChar) {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
	}

	private static Result<String, InterpretError> handleLetDeclaration(String rest, Map<String, String> env,
			Map<String, Boolean> mutable, AtomicReference<String> lastValue, String input) {
		System.out.println("[DEBUG] handleLetDeclaration called with: '" + rest + "'");
		boolean isMut = false;
		if (rest.startsWith("mut ")) {
			isMut = true;
			rest = rest.substring(4).trim();
		}
		String[] ne = splitNameExpr(rest);
		if (ne == null) {
			return err("Missing '=' in let declaration", input);
		}
		String name = ne[0];
		String expr = ne[1];
		Option<String> value = evalAndPut(name, expr, env);
		Result<String, InterpretError> r1 = optionToResult(value, input, "Invalid initializer for " + name);
		Result<String, InterpretError> setErr1 = setLastFromResultOrErr(r1, lastValue);
		if (setErr1 != null)
			return setErr1;
		mutable.put(name, isMut);
		return null;
	}

	private static Result<String, InterpretError> executeConditionalLoop(String cond, String body,
			Map<String, String> env, Map<String, Boolean> mutable, AtomicReference<String> lastValue, String input,
			String condErrMsg, String assignErrMsg, String exprErrMsg,
			java.util.function.Supplier<Result<String, InterpretError>> optionalIncr) {
		while (true) {
			Option<String> condVal = evalExpr(cond, env);
			if (!(condVal instanceof Some(var cv)) || !isBoolean(cv)) {
				return err(condErrMsg, input);
			}
			if ("false".equals(cv))
				break;
			Result<String, InterpretError> rBody = executeSimpleOrExpression(body, env, mutable, lastValue, input, true,
					assignErrMsg, exprErrMsg);
			if (rBody != null)
				return rBody;
			if (optionalIncr != null) {
				Result<String, InterpretError> rIncr = optionalIncr.get();
				if (rIncr != null)
					return rIncr;
			}
		}
		return null;
	}

	private static Result<String, InterpretError> performAssignmentInEnv(String stmt, Map<String, String> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue, String input, String contextMessage) {
		// simple '=' assignment
		String[] ne = splitNameExpr(stmt);
		if (ne == null)
			return err("Missing '=' in assignment", input);
		String name = ne[0];
		String expr = ne[1];
		Result<String, InterpretError> checkRes = ensureExistsAndMutableOrErr(name, env, mutable, input);
		if (checkRes != null)
			return checkRes;
		Option<String> value = evalAndPut(name, expr, env);
		Result<String, InterpretError> r = optionToResult(value, input, contextMessage);
		return setLastFromResultOrErr(r, lastValue);

	}

	private static Result<String, InterpretError> executeSimpleOrExpression(String stmt, Map<String, String> env,
			Map<String, Boolean> mutable, AtomicReference<String> lastValue, String input, boolean allowIncrementInPlace,
			String assignmentContextMessage, String exprContextMessage) {
		// handle '+=' or '=' assignments, post-increment, or plain expressions
		// handle compound and simple assignments
		int plusEq = stmt.indexOf("+=");
		if (plusEq != -1) {
			// reuse logic from performAssignmentInEnv for '+='
			String name = stmt.substring(0, plusEq).trim();
			String expr = stmt.substring(plusEq + 2).trim();
			Result<Integer, InterpretError> curRes = getIntegerVarOrErr(name, env, mutable, input);
			if (curRes instanceof Err(var e))
				return new Err<>(e);
			int a = ((Ok<Integer, InterpretError>) curRes).value();
			Option<String> valueOpt = evalExpr(expr, env);
			if (!(valueOpt instanceof Some(var v))) {
				return err(assignmentContextMessage, input);
			}
			String addend = v;
			if (!isInteger(addend))
				return err("Right-hand side of '+=' is not an integer", input);
			try {
				int b = Integer.parseInt(addend);
				int sum = a + b;
				env.put(name, Integer.toString(sum));
				return setLastFromResultOrErr(new Ok<>(Integer.toString(sum)), lastValue);
			} catch (NumberFormatException ex) {
				return err("Invalid integer during '+='", input);
			}
		}

		// simple '=' assignment (but ignore '==' equality operator)
		int eqIndex = stmt.indexOf('=');
		if (eqIndex != -1) {
			// if this is a '==' comparison, fall through to expression handling
			if (!(eqIndex + 1 < stmt.length() && stmt.charAt(eqIndex + 1) == '=')) {
				Result<String, InterpretError> r = performAssignmentInEnv(stmt, env, mutable, lastValue, input,
						assignmentContextMessage);
				return r;
			}
		}

		// post-increment
		if (stmt.endsWith("++")) {
			String name = stmt.substring(0, stmt.length() - 2).trim();
			Result<String, InterpretError> r = performIncrement(name, env, mutable, lastValue, input, allowIncrementInPlace);
			return r;
		}

		// zero-arg function call (e.g., name()) - but not method calls
		// (instance.method())
		if (stmt.endsWith("()")) {
			String name = stmt.substring(0, stmt.length() - 2).trim();
			// If it contains a dot, it's likely a method call, so treat as expression
			if (name.contains(".")) {
				return performExpressionAndSetLast(stmt, env, lastValue, input, exprContextMessage);
			}
			Option<String> res = evalZeroArgFunction(name, env);
			Result<String, InterpretError> rr = optionToResult(res, input, exprContextMessage);
			return setLastFromResultOrErr(rr, lastValue);
		}

		// expression
		return performExpressionAndSetLast(stmt, env, lastValue, input, exprContextMessage);
	}

	private static Result<String, InterpretError> performIncrement(String name, Map<String, String> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue, String input, boolean inPlace) {
		Result<Integer, InterpretError> r = getIntegerVarOrErr(name, env, mutable, input);
		if (r instanceof Err(var err))
			return new Err<>(err);
		int v = ((Ok<Integer, InterpretError>) r).value();
		int nv = v + 1;
		env.put(name, Integer.toString(nv));
		lastValue.set(Integer.toString(nv));
		return null;
	}

	private static Result<Integer, InterpretError> getIntegerVarOrErr(String name, Map<String, String> env,
			Map<String, Boolean> mutable, String input) {
		Result<String, InterpretError> check = ensureExistsAndMutableOrErr(name, env, mutable, input);
		if (check != null) {
			if (check instanceof Err(var e))
				return new Err<>(e);
		}
		String cur = env.get(name);
		if (!isInteger(cur))
			return new Err<>(new InterpretError("Cannot use non-integer variable '" + name + "'", input));
		try {
			int v = Integer.parseInt(cur);
			return new Ok<>(v);
		} catch (NumberFormatException ex) {
			return new Err<>(new InterpretError("Invalid integer value for variable '" + name + "'", input));
		}
	}

	private static Result<String, InterpretError> performExpressionAndSetLast(String expr, Map<String, String> env,
			AtomicReference<String> lastValue, String input, String contextMessage) {
		Option<String> opt = evalExpr(expr, env);
		Result<String, InterpretError> r = optionToResult(opt, input, contextMessage);
		return setLastFromResultOrErr(r, lastValue);
	}

	private static String[] splitNameExpr(String stmt) {
		if (stmt == null)
			return null;
		int idx = stmt.indexOf('=');
		if (idx == -1)
			return null;
		String name = stmt.substring(0, idx).trim();
		String expr = stmt.substring(idx + 1).trim();
		return new String[] { name, expr };
	}

	private static Result<String, InterpretError> optionToResult(Option<String> opt, String input,
			String contextMessage) {
		if (opt instanceof Some(var v)) {
			return new Ok<>(v);
		}
		return err(contextMessage, input);
	}

	private static Option<String> evalFunction(String name, Map<String, String> env,
			java.util.function.Function<String, Option<String>> processor) {
		String fv = env.get(name);
		System.out.println("[DEBUG] call fn " + name + " -> " + fv);
		if (fv != null && fv.startsWith("fn:")) {
			String fnDef = fv.substring(3);
			return processor.apply(fnDef);
		}
		return None.instance();
	}

	private static Option<String> evalZeroArgFunction(String name, Map<String, String> env) {
		return evalFunction(name, env, body -> evalExpr(body, env));
	}

	private static Option<String> evalParameterizedFunction(String name, String argsStr, Map<String, String> env) {
		return evalFunction(name, env, fnDef -> {
			int colonIdx = fnDef.indexOf(':');
			if (colonIdx == -1) {
				// No parameters expected, but arguments provided - error
				return None.instance();
			}

			String paramNamesStr = fnDef.substring(0, colonIdx);
			String body = fnDef.substring(colonIdx + 1);
			String[] paramNames = paramNamesStr.split(",");

			// Parse the arguments
			String[] argValues = parseArguments(argsStr, env);
			if (argValues == null || argValues.length != paramNames.length) {
				return None.instance();
			}

			// Create a new environment with parameter bindings
			Map<String, String> localEnv = new HashMap<>(env);
			for (int i = 0; i < paramNames.length; i++) {
				localEnv.put(paramNames[i].trim(), argValues[i]);
			}

			return evalExpr(body, localEnv);
		});
	}

	private static String[] parseArguments(String argsStr, Map<String, String> env) {
		if (argsStr == null || argsStr.trim().isEmpty()) {
			return new String[0];
		}

		// Simple parsing - split by comma and evaluate each argument
		String[] args = argsStr.split(",");
		String[] values = new String[args.length];

		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();
			Option<String> result = evalExpr(arg, env);
			if (result instanceof Some(var value)) {
				values[i] = value;
			} else {
				return null; // Failed to evaluate argument
			}
		}

		return values;
	}

	private static Result<String, InterpretError> err(String message, String input) {
		return new Err<>(new InterpretError(message, input));
	}

	private static Result<String, InterpretError> setLastFromResultOrErr(Result<String, InterpretError> r,
			AtomicReference<String> last) {
		if (r instanceof Ok(var v)) {
			last.set(v);
			System.out.println("[DEBUG] set last -> " + v);
			return null;
		}
		return r;
	}

	private static Result<String, InterpretError> ensureExistsAndMutableOrErr(String name, Map<String, String> env,
			Map<String, Boolean> mutable, String input) {
		if (!env.containsKey(name)) {
			return new Err<>(new InterpretError("Undefined value", input));
		}
		if (!Boolean.TRUE.equals(mutable.get(name))) {
			return new Err<>(new InterpretError("Immutable assignment", input));
		}
		return null;
	}

	private static Option<String> evalExpr(String expr, Map<String, String> env) {
		String t = expr == null ? "" : expr.trim();

		System.out.println("[DEBUG] evalExpr called with: '" + t + "'");

		// if-expression: if (cond) thenExpr else elseExpr
		if (t.startsWith("if")) {
			int open = t.indexOf('(');
			if (open == -1)
				return None.instance();
			// find matching closing parenthesis
			int depth = 1;
			int close = -1;
			for (int i = open + 1; i < t.length(); i++) {
				char c = t.charAt(i);
				if (c == '(')
					depth++;
				else if (c == ')') {
					depth--;
					if (depth == 0) {
						close = i;
						break;
					}
				}
			}
			if (close == -1)
				return None.instance();
			String cond = t.substring(open + 1, close).trim();
			int afterClose = close + 1;
			int elseIdx = t.indexOf("else", afterClose);
			if (elseIdx == -1)
				return None.instance();
			String thenPart = t.substring(afterClose, elseIdx).trim();
			String elsePart = t.substring(elseIdx + 4).trim();
			Option<String> condVal = evalExpr(cond, env);
			if (condVal instanceof Some(var cv)) {
				if (!isBoolean(cv))
					return None.instance();
				if ("true".equals(cv))
					return evalExpr(thenPart, env);
				return evalExpr(elsePart, env);
			}
			return None.instance();
		}

		// equality: a == b
		int eqeq = t.indexOf("==");
		if (eqeq != -1) {
			String left = t.substring(0, eqeq).trim();
			String right = t.substring(eqeq + 2).trim();
			Option<String> res = evalBinaryOperation(left, right, env,
					(lv, rv) -> new Some<>(lv.equals(rv) ? "true" : "false"));
			return res;
		}

		// binary less-than: a < b
		int lt = t.indexOf('<');
		if (lt != -1) {
			String left = t.substring(0, lt).trim();
			String right = t.substring(lt + 1).trim();
			Option<String> res = evalIntegerComparison(left, right, env, (li, ri) -> li < ri);
			return res;
		}

		if (isInteger(t)) {
			return new Some<>(expr);
		}

		if (isBoolean(t)) {
			return new Some<>(t);
		}

		// method call: instance.method() or instance.method(args)
		int dotIndex = t.lastIndexOf('.');
		System.out.println("[DEBUG] Checking method call: dotIndex=" + dotIndex + " endsWithParen=" + t.endsWith(")")
				+ " expr='" + t + "'");
		if (dotIndex != -1 && t.endsWith(")")) {
			String instanceExpr = t.substring(0, dotIndex).trim();
			String methodCall = t.substring(dotIndex + 1).trim();

			int methodOpenParen = methodCall.indexOf('(');
			if (methodOpenParen != -1) {
				String methodName = methodCall.substring(0, methodOpenParen).trim();
				String methodArgsStr = methodCall.substring(methodOpenParen + 1, methodCall.length() - 1).trim();

				System.out.println("[DEBUG] Method call: instanceExpr=" + instanceExpr + " methodName=" + methodName);

				// Evaluate the instance expression
				Option<String> instanceOpt = evalExpr(instanceExpr, env);
				System.out.println("[DEBUG] Instance evaluation result: " + instanceOpt);
				if (instanceOpt instanceof Some(var instanceValue)) {
					System.out.println("[DEBUG] Instance value: " + instanceValue);
					// Parse instance to get its type
					if (instanceValue.startsWith("inst:")) {
						String instanceData = instanceValue.substring(5); // Remove "inst:"
						int pipeIndex = instanceData.indexOf('|');
						String structType = pipeIndex == -1 ? instanceData : instanceData.substring(0, pipeIndex);

						System.out.println("[DEBUG] Struct type: " + structType);

						// Look up the method in the impl block
						String methodKey = "impl:" + structType + ":" + methodName;
						System.out.println("[DEBUG] Looking for method key: " + methodKey);
						if (env.containsKey(methodKey)) {
							String methodDef = env.get(methodKey);
							System.out.println("[DEBUG] Found method def: " + methodDef);
							if (methodDef.startsWith("fn:")) {
								if (methodArgsStr.isEmpty()) {
									// Zero-arg method call
									String methodBody = methodDef.substring(3);
									Option<String> result = evalExpr(methodBody, env);
									System.out.println("[DEBUG] Method result: " + result);
									return result;
								} else {
									// Method call with arguments - for now, simple implementation
									String[] parts = methodDef.substring(3).split(":", 2);
									if (parts.length == 2) {
										String[] paramNames = parts[0].split(",");
										String methodBody = parts[1];

										// Create local environment with parameters
										Map<String, String> localEnv = new HashMap<>(env);
										// Parse arguments and create local environment
										String[] args = methodArgsStr.split(",");
										for (int i = 0; i < Math.min(paramNames.length, args.length); i++) {
											Option<String> argValue = evalExpr(args[i].trim(), env);
											if (argValue instanceof Some(var av)) {
												localEnv.put(paramNames[i].trim(), av);
											}
										}

										return evalExpr(methodBody, localEnv);
									}
								}
							}
						} else {
							System.out.println("[DEBUG] Method not found in env");
						}
					} else {
						System.out.println("[DEBUG] Instance value does not start with 'inst:'");
					}
				} else {
					System.out.println("[DEBUG] Failed to evaluate instance expression");
				}
			}
		}

		// function call: name() or name(args)
		if (t.endsWith(")")) {
			int openParen = t.indexOf('(');
			if (openParen != -1) {
				String name = t.substring(0, openParen).trim();
				String argsStr = t.substring(openParen + 1, t.length() - 1).trim();
				if (argsStr.isEmpty()) {
					// zero-arg function call: name()
					return evalZeroArgFunction(name, env);
				} else {
					// function call with arguments: name(arg1, arg2, ...)
					return evalParameterizedFunction(name, argsStr, env);
				}
			}
		}

		// struct constructor like: Type { expr }
		int braceIdx = t.indexOf('{');
		if (braceIdx != -1 && braceIdx < t.length() - 1) {
			String possibleType = t.substring(0, braceIdx).trim();
			if (!possibleType.isEmpty() && env.containsKey("struct:def:" + possibleType)) {
				int close = findMatchingBrace(t, braceIdx);
				if (close != -1) {
					String inner = t.substring(braceIdx + 1, close).trim();
					String def = env.get("struct:def:" + possibleType);
					String[] fields = def.isEmpty() ? new String[0] : def.split(",");

					if (fields.length == 0) {
						// Empty struct: create instance without any fields
						StringBuilder sb = new StringBuilder();
						sb.append("inst:").append(possibleType);
						System.out.println("[DEBUG] construct -> " + sb.toString());
						return new Some<>(sb.toString());
					} else {
						// Non-empty struct: evaluate the inner expression for the first field
						Option<String> val = evalExpr(inner, env);
						if (val instanceof Some(var fv)) {
							// build instance string: inst:Type|field0=value
							StringBuilder sb = new StringBuilder();
							sb.append("inst:").append(possibleType);
							sb.append("|").append(fields[0].trim()).append("=").append(fv);
							System.out.println("[DEBUG] construct -> " + sb.toString());
							return new Some<>(sb.toString());
						}
						return None.instance();
					}
				}
			}
		}

		String v = env.get(t);
		if (v != null) {
			// function stored as special env entry 'fn:BODY'
			if (v.startsWith("fn:")) {
				String body = v.substring(3);
				Option<String> res = evalExpr(body, env);
				return res;
			}
			return new Some<>(v);
		}

		// arithmetic operations: + - * /
		// handle addition: a + b
		int plus = t.indexOf('+');
		if (plus != -1) {
			String left = t.substring(0, plus).trim();
			String right = t.substring(plus + 1).trim();
			return evalArithmeticOperation(left, right, env, Integer::sum);
		}

		// handle subtraction: a - b
		int minus = t.indexOf('-');
		if (minus != -1) {
			String left = t.substring(0, minus).trim();
			String right = t.substring(minus + 1).trim();
			return evalArithmeticOperation(left, right, env, (a, b) -> a - b);
		}

		// handle multiplication: a * b
		int mult = t.indexOf('*');
		if (mult != -1) {
			String left = t.substring(0, mult).trim();
			String right = t.substring(mult + 1).trim();
			return evalArithmeticOperation(left, right, env, (a, b) -> a * b);
		}

		// handle division: a / b
		int div = t.indexOf('/');
		if (div != -1) {
			String left = t.substring(0, div).trim();
			String right = t.substring(div + 1).trim();
			return evalArithmeticOperation(left, right, env, (a, b) -> b == 0 ? null : a / b);
		}

		// member access: var.field
		int dot = t.indexOf('.');
		if (dot != -1) {
			String varName = t.substring(0, dot).trim();
			String fieldName = t.substring(dot + 1).trim();
			String inst = env.get(varName);
			System.out.println("[DEBUG] member access var=" + varName + " field=" + fieldName + " inst=" + inst);
			if (inst != null && inst.startsWith("inst:")) {
				// format: inst:Type|field=value|...
				int bar = inst.indexOf('|');
				if (bar == -1)
					return None.instance();
				String rest = inst.substring(bar + 1);
				String[] pairs = rest.split("\\|");
				for (String p : pairs) {
					int eq = p.indexOf('=');
					if (eq == -1)
						continue;
					String fn = p.substring(0, eq).trim();
					String fv = p.substring(eq + 1).trim();
					if (fn.equals(fieldName))
						return new Some<>(fv);
				}
			}
			return None.instance();
		}
		return None.instance();
	}

	private static Option<String> evalAndPut(String name, String expr, Map<String, String> env) {
		Option<String> opt = evalExpr(expr, env);
		if (opt instanceof Some(var optValue)) {
			env.put(name, optValue);
			System.out.println("[DEBUG] stored variable: " + name + " = " + optValue);
			return opt;
		}
		System.out.println("[DEBUG] failed to store variable: " + name + " expr: " + expr);
		return None.instance();
	}

	/**
	 * Parse a string of form "Name { body } remainder" and return [name, body,
	 * remainder]
	 * or null if malformed.
	 */
	private static String[] parseParameterNames(String params) {
		if (params == null || params.trim().isEmpty()) {
			return new String[0];
		}
		// Parse parameters like "x : I32, y : String" and extract just the names
		String[] parts = params.split(",");
		String[] names = new String[parts.length];
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].trim();
			int colon = part.indexOf(':');
			if (colon != -1) {
				names[i] = part.substring(0, colon).trim();
			} else {
				names[i] = part; // fallback if no type annotation
			}
		}
		return names;
	}

	private static String[] parseNameBodyRemainder(String rest) {
		int open = rest.indexOf('{');
		if (open == -1)
			return null;
		int close = findMatchingBrace(rest, open);
		if (close == -1)
			return null;
		String name = rest.substring(0, open).trim();
		String body = rest.substring(open + 1, close).trim();
		String remainder = rest.substring(close + 1).trim();
		return new String[] { name, body, remainder };
	}

	private static String[] splitNames(String s) {
		if (s == null || s.trim().isEmpty())
			return new String[0];
		return s.split(",");
	}

	private static Option<String> evalIntegerComparison(String left, String right, Map<String, String> env,
			java.util.function.BiFunction<Integer, Integer, Boolean> comparison) {
		return evalIntegerOperation(left, right, env, (li, ri) -> new Some<>(comparison.apply(li, ri) ? "true" : "false"));
	}

	private static Option<String> evalArithmeticOperation(String left, String right, Map<String, String> env,
			java.util.function.BinaryOperator<Integer> operation) {
		return evalIntegerOperation(left, right, env, (li, ri) -> {
			Integer result = operation.apply(li, ri);
			if (result == null) // for division by zero
				return None.instance();
			return new Some<>(String.valueOf(result));
		});
	}

	private static Option<String> evalBinaryOperation(String left, String right, Map<String, String> env,
			java.util.function.BiFunction<String, String, Option<String>> combiner) {
		Option<String> lopt = evalExpr(left, env);
		Option<String> ropt = evalExpr(right, env);
		if (lopt instanceof Some(var lv) && ropt instanceof Some(var rv)) {
			return combiner.apply(lv, rv);
		}
		return None.instance();
	}

	private static Option<String> evalIntegerOperation(String left, String right, Map<String, String> env,
			java.util.function.BiFunction<Integer, Integer, Option<String>> operation) {
		return evalBinaryOperation(left, right, env, (lv, rv) -> {
			if (!isInteger(lv) || !isInteger(rv))
				return None.instance();
			try {
				int li = Integer.parseInt(lv);
				int ri = Integer.parseInt(rv);
				return operation.apply(li, ri);
			} catch (NumberFormatException | ArithmeticException ex) {
				return None.instance();
			}
		});
	}

	private static boolean isInteger(String s) {
		if (s == null || s.isEmpty())
			return false;
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	private static boolean isBoolean(String s) {
		return "true".equals(s) || "false".equals(s);
	}
}
