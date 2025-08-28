package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.value.BoolVal;
import magma.value.EnumDefVal;
import magma.value.EnumVariantVal;
import magma.value.FunctionVal;
import magma.value.InstanceVal;
import magma.value.IntVal;
import magma.value.StructDefVal;
import magma.value.TraitDefVal;
import magma.value.TypeAliasVal;
import magma.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.AbstractMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

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

		Map<String, Value> env = new HashMap<>();
		Map<String, Boolean> mutable = new HashMap<>();
		// removed: varTypes; types are stored in env under type:var:
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
				Result<ParsedNameBody, InterpretError> parseResult = parseNameBodyExtraction(rest, isEnum ? "enum" : "struct",
						input);
				if (parseResult instanceof Err(var error)) {
					return new Err<>(error);
				}
				ParsedNameBody parsed = ((Ok<ParsedNameBody, InterpretError>) parseResult).value();
				String rawName = parsed.name;
				// strip any type parameter list like <A, B> so the base name (e.g. Pair)
				// is used for runtime lookups (constructors are written as 'Pair { ... }')
				String name = rawName;
				int ltIdx = rawName.indexOf('<');
				if (ltIdx != -1) {
					name = rawName.substring(0, ltIdx).trim();
				}

				String body = parsed.body;
				String remainder = parsed.remainder;
				if (!isEnum) {
					String[] fieldParts = splitNames(body);
					List<String> fieldNames = new ArrayList<>();
					for (String fp : fieldParts) {
						String f = fp.trim();
						int colon = f.indexOf(':');
						String fname = colon == -1 ? f : f.substring(0, colon).trim();
						if (!fname.isEmpty())
							fieldNames.add(fname);
					}
					env.put("struct:def:" + name, new StructDefVal(fieldNames));
					System.out.println("[DEBUG] define struct " + rawName + " -> "
							+ fieldNames.stream().reduce((a, b) -> a + "," + b).orElse(""));
				} else {
					String[] variants = splitNames(body);
					List<String> varNames = new ArrayList<>();
					for (String v : variants) {
						String vn = v.trim();
						if (!vn.isEmpty()) {
							varNames.add(vn);
							// register fully-qualified variant name so evalExpr can look it up
							env.put(name + "." + vn, new EnumVariantVal(name + "." + vn));
						}
					}
					env.put("enum:def:" + name, new EnumDefVal(varNames));
					System.out.println("[DEBUG] define enum " + name + " -> " + String.join(",", varNames));
				}
				if (remainder.isEmpty())
					continue;
				stmt = remainder;
				// fallthrough to process remainder
			}

			// let declaration: let [mut] name = expr
			Result<String, InterpretError> letResMain = processLetIfPresent(stmt, env, mutable, lastValue, input);
			if (letResMain instanceof Err)
				return letResMain;
			if (letResMain instanceof Ok)
				continue;

			// type alias: type AliasName = OriginalType
			if (stmt.startsWith("type ")) {
				String rest = stmt.substring(5).trim();
				int eqIndex = rest.indexOf('=');
				if (eqIndex == -1) {
					return err("Malformed type alias: missing '='", input);
				}
				String aliasName = rest.substring(0, eqIndex).trim();
				String originalType = rest.substring(eqIndex + 1).trim();
				if (aliasName.isEmpty() || originalType.isEmpty()) {
					return err("Malformed type alias", input);
				}
				// Parse optional drop specification like: I32 & drop(fnName)
				String baseType = originalType;
				String dropFn = null;
				int andIdx = originalType.indexOf('&');
				if (andIdx != -1) {
					baseType = originalType.substring(0, andIdx).trim();
					String after = originalType.substring(andIdx + 1).trim();
					// expect format: drop(fnName)
					if (after.startsWith("drop(") && after.endsWith(")")) {
						dropFn = after.substring(5, after.length() - 1).trim();
					}
				}
				// Store the type alias mapping with optional drop fn
				env.put("type:alias:" + aliasName, new TypeAliasVal(baseType, dropFn));
				System.out.println("[DEBUG] define type alias " + aliasName + " -> " + originalType
						+ (dropFn == null ? "" : " (drop=" + dropFn + ")"));
				continue;
			}

			// trait declaration: trait Name { ... }
			if (stmt.startsWith("trait ")) {
				String rest = stmt.substring(6).trim();
				// Parse trait name and body
				Result<ParsedNameBody, InterpretError> traitResult = parseNameBodyExtraction(rest, "trait", input);
				if (traitResult instanceof Err(var err)) {
					return new Err<>(err);
				}
				var traitData = ((Ok<ParsedNameBody, InterpretError>) traitResult).value();
				String traitName = traitData.name;
				String traitBody = traitData.body;
				String traitRemainder = traitData.remainder;

				// Store the trait definition (for now, traits are mainly for type checking)
				env.put("trait:def:" + traitName, new TraitDefVal(traitBody));
				System.out.println("[DEBUG] define trait " + traitName + " -> " + traitBody);

				if (traitRemainder.isEmpty())
					continue;
				stmt = traitRemainder;
				// fallthrough to process remainder
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
						"Invalid while condition",
						"Invalid assignment in while body", "Invalid expression in while body", null);
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
					} else {
						Result<String, InterpretError> rInit = executeSimpleOrExpression(init, env, mutable, lastValue, input,
								"Invalid init in for",
								"Invalid init in for");
						if (rInit != null)
							return rInit;
					}
				}
				// empty body -> continue
				if (body.isEmpty())
					continue;
				// loop
				Supplier<Result<String, InterpretError>> incrSupplier = null;
				if (!incr.isEmpty()) {
					incrSupplier = () -> executeSimpleOrExpression(incr, env, mutable, lastValue, input, "Invalid incr in for",
							"Invalid incr in for");
				}
				Result<String, InterpretError> rLoop = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid for condition",
						"Invalid assignment in for body", "Invalid expression in for body", incrSupplier);
				if (rLoop != null)
					return rLoop;
				continue;
			}

			// function declaration: fn name<typeParams>(params) => expr or fn name(params)
			// => expr
			// class function: class fn Name(params) => body
			if (stmt.startsWith("class fn ")) {
				String rest = stmt.substring("class fn ".length()).trim();
				int open = rest.indexOf('(');
				if (open == -1) {
					return err("Malformed class function", input);
				}
				int close = rest.indexOf(')', open);
				if (close == -1) {
					return err("Malformed class function", input);
				}

				String className = rest.substring(0, open).trim();
				String[] parsedParts = extractParamsArrowBodyRemainder(rest, open, close);
				if (parsedParts == null) {
					return err("Malformed class function", input);
				}
				String params = parsedParts[0];
				String body = parsedParts[1];
				// create struct definition from parameter names
				String[] paramNames = parseParameterNames(params);
				List<String> fields = new ArrayList<>();
				for (String p : paramNames) {
					if (!p.trim().isEmpty())
						fields.add(p.trim());
				}
				env.put("struct:def:" + className, new StructDefVal(fields));
				System.out.println("[DEBUG] define class " + className + " -> " + String.join(",", fields));

				// create constructor-style function stored under the class name that returns
				// this if body empty
				String fnBody;
				if (body.isEmpty()) {
					fnBody = "this";
				} else {
					String trimmedBody = body.trim();
					if (trimmedBody.endsWith("this")) {
						fnBody = body;
					} else {
						fnBody = body + "; this";
					}
				}
				// store constructor so calling ClassName(...) will invoke the class body
				storeFunctionInEnv(className, String.join(",", paramNames), fnBody, env);
				System.out.println("[DEBUG] define class constructor " + className);
				continue;
			}

			if (stmt.startsWith("fn ")) {
				String rest = stmt.substring(3).trim();
				int open = rest.indexOf('(');
				if (open == -1) {
					return err("Malformed function", input);
				}
				int close = rest.indexOf(')', open);
				if (close == -1) {
					return err("Malformed function", input);
				}

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

				String[] parsedParts = extractParamsArrowBodyRemainder(rest, open, close);
				if (parsedParts == null) {
					return err("Malformed function", input);
				}
				String params = parsedParts[0];
				String body = parsedParts[1];
				String remainderAfterFn = parsedParts[2];
				if (name.isEmpty() || body.isEmpty()) {
					return err("Malformed function", input);
				}
				// store function body with parameters and a special prefix in env
				storeFunctionInEnv(name, params, body, env);

				if (remainderAfterFn.isEmpty())
					continue;
				// fallthrough to process remainder after function definition
				stmt = remainderAfterFn;
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
						String[] methodParsed = extractParamsArrowBodyRemainder(methodRest, methodOpen, methodClose);
						if (methodParsed == null)
							return err("Malformed method in impl", input);
						String methodParams = methodParsed[0];
						String methodBody = methodParsed[1];

						if (methodName.isEmpty() || methodBody.isEmpty())
							return err("Malformed method in impl", input);

						// Store method with struct name prefix: "impl:StructName:methodName"
						storeFunctionInEnv("impl:" + structName + ":" + methodName, methodParams, methodBody, env);
						System.out.println("[DEBUG] define method " + structName + "." + methodName + " -> " + methodBody);
					}
				}
				if (remainder.isEmpty())
					continue;

				// Process remainder statements by falling through to normal statement
				// processing
				System.out.println("[DEBUG] impl block remainder: '" + remainder + "'");
				System.out.println("[DEBUG] Processing impl remainder as stmt: '" + remainder + "'");

				// Handle let declarations specifically
				if (remainder.startsWith("let ")) {
					Result<String, InterpretError> r = handleLetDeclaration(remainder.substring(4).trim(), env, mutable,
							lastValue, input);
					if (r instanceof Err<String, InterpretError>) {
						return r;
					}
				}
				// Handle other statements by evaluating them as expressions
				else {
					Option<String> exprResult = evalExpr(remainder, env);
					if (exprResult instanceof Some<String>(String value)) {
						lastValue.set(value);
						System.out.println("[DEBUG] set last -> " + value);
					}
				}
				continue;
			}

			// for any non-let, non-while statement delegate to helper to handle assignment,
			// post-increment, or expression
			Result<String, InterpretError> rMain = executeSimpleOrExpression(stmt, env, mutable, lastValue, input,
					"Invalid assignment expression for " +
							(splitNameExpr(stmt) == null ? ""
									: splitNameExpr(
											stmt)[0]),
					"Undefined expression: " + stmt);
			if (rMain != null)
				return rMain;
		}

		if (lastValue.get() != null) {
			return new Ok<>(lastValue.get());
		}

		return err("Undefined value", input);
	}

	// --- Value conversion helpers for typed env ---
	private static String fromValue(Value v) {
		if (v == null)
			return null;
		if (v instanceof IntVal iv)
			return Integer.toString(iv.value());
		if (v instanceof BoolVal bv)
			return bv.value() ? "true" : "false";
		if (v instanceof EnumVariantVal ev)
			return ev.qualifiedName();
		if (v instanceof InstanceVal iv) {
			StringBuilder sb = new StringBuilder();
			sb.append("inst:").append(iv.typeName());
			if (iv.fields() != null && !iv.fields().isEmpty()) {
				for (Map.Entry<String, Value> e : iv.fields().entrySet()) {
					sb.append("|").append(e.getKey()).append("=").append(fromValue(e.getValue()));
				}
			}
			return sb.toString();
		}
		if (v instanceof FunctionVal fv) {
			String params = fv.params() == null || fv.params().isEmpty() ? "" : String.join(",", fv.params()) + ":";
			return "fn:" + params + fv.body();
		}
		if (v instanceof StructDefVal sd) {
			return String.join(",", sd.fields());
		}
		if (v instanceof EnumDefVal ed) {
			return String.join(",", ed.variants());
		}
		if (v instanceof TraitDefVal td)
			return td.body();
		if (v instanceof TypeAliasVal ta)
			return ta.targetType();
		if (v instanceof magma.value.PointerVal pv)
			return "inst:ptr|target=" + pv.targetName();
		return null;
	}

	private static Value toValueFromEvaluatedString(String s) {
		if (s == null)
			return null;
		if (isInteger(s))
			return new IntVal(Integer.parseInt(s));
		if (isBoolean(s))
			return new BoolVal("true".equals(s));
		if (s.startsWith("inst:")) {
			// parse minimal "inst:Type|field=value|..." into InstanceVal
			String rest = s.substring(5);
			int pipe = rest.indexOf('|');
			String type = pipe == -1 ? rest : rest.substring(0, pipe);
			if (type.equals("ptr")) {
				// format: inst:ptr|target=var
				int tidx = rest.indexOf("target=");
				if (tidx != -1) {
					String target = rest.substring(tidx + "target=".length()).trim();
					return new magma.value.PointerVal(target);
				}
			}
			Map<String, Value> fields = new HashMap<>();
			if (pipe != -1) {
				String after = rest.substring(pipe + 1);
				if (!after.isEmpty()) {
					String[] pairs = after.split("\\|");
					for (String p : pairs) {
						int eq = p.indexOf('=');
						if (eq != -1) {
							String k = p.substring(0, eq).trim();
							String v = p.substring(eq + 1).trim();
							fields.put(k, toValueFromEvaluatedString(v));
						}
					}
				}
			}
			return new InstanceVal(type, fields);
		}
		return null;
	}

	private static String[] splitTopLevelStatements(String src) {
		if (src == null || src.isEmpty())
			return new String[0];
		List<String> parts = new ArrayList<>();
		int[] depths = new int[2];
		int last = 0;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			adjustDepths(c, depths);
			if (c == ';' && depths[0] == 0 && depths[1] == 0) {
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

	private static Result<String, InterpretError> handleLetDeclaration(String rest,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue,
			String input) {
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
		String nameWithType = ne[0];
		String expr = ne[1];

		// Extract just the variable name (without type annotation)
		String name = nameWithType;
		int colonIndex = nameWithType.indexOf(':');
		String declaredType = null;
		String declaredDrop = null;
		if (colonIndex != -1) {
			name = nameWithType.substring(0, colonIndex).trim();
			declaredType = nameWithType.substring(colonIndex + 1).trim();
			// resolve type aliases if present and capture drop function if any
			if (declaredType != null && env.containsKey("type:alias:" + declaredType)) {
				Value v = env.get("type:alias:" + declaredType);
				if (v instanceof TypeAliasVal ta) {
					declaredDrop = ta.dropFn();
					declaredType = ta.targetType();
				}
			}
		}

		// If an explicit declared type exists and the initializer is an integer literal
		// with a suffix (e.g. 10U8 or 10I32), ensure the suffix type matches the
		// declared type. If they mismatch, return an error.
		Result<String, InterpretError> sufErr = ensureIntegerSuffixMatches(declaredType, expr, input,
				"Mismatched types in let declaration");
		if (sufErr != null)
			return sufErr;

		// If the variable has a declared type with a drop function and an existing
		// value, call the drop function before overwriting.
		Value declaredTypeVal = env.get("type:var:" + name);
		if (declaredTypeVal instanceof TypeAliasVal td && td.dropFn() != null && env.containsKey(name)) {
			// attempt to call drop function (zero-arg)
			evalZeroArgFunction(td.dropFn(), env);
		}

		Option<String> value = evalAndPut(name, expr, env);
		Result<String, InterpretError> r1 = optionToResult(value, input, "Invalid initializer for " + name);
		Result<String, InterpretError> setErr1 = setLastFromResultOrErr(r1, lastValue);
		if (setErr1 != null)
			return setErr1;
		mutable.put(name, isMut);
		// persist mutability into env so nested function bodies can observe outer
		// mutability
		env.put("mut:" + name, new BoolVal(isMut));
		// Store declared type (resolved already against aliases) so future assignments
		// can validate suffix/type mismatches (e.g., assign with 20U8 to I32 variable)
		if (declaredType != null && !declaredType.isEmpty()) {
			env.put("type:var:" + name, new TypeAliasVal(declaredType, declaredDrop));
		}

		// If declared type has a drop function, invoke it now (basic semantics)
		Value tdv = env.get("type:var:" + name);
		if (tdv instanceof TypeAliasVal tav && tav.dropFn() != null) {
			evalZeroArgFunction(tav.dropFn(), env);
		}
		return null;
	}

	/**
	 * If the statement is a let-declaration, handle it and return an Ok marker on
	 * success,
	 * an Err on failure, or null if the statement is not a let declaration.
	 */
	private static Result<String, InterpretError> processLetIfPresent(String stmt,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue,
			String input) {
		if (!stmt.startsWith("let "))
			return null;
		Result<String, InterpretError> r = handleLetDeclaration(stmt.substring(4).trim(), env, mutable, lastValue, input);
		if (r == null)
			return new Ok<>("HANDLED");
		return r;
	}

	private static Result<String, InterpretError> executeConditionalLoop(String cond,
			String body,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue,
			String input,
			String condErrMsg,
			String assignErrMsg,
			String exprErrMsg,
			Supplier<Result<String, InterpretError>> optionalIncr) {
		while (true) {
			Option<String> condVal = evalExpr(cond, env);
			if (!(condVal instanceof Some(var cv)) || !isBoolean(cv)) {
				return err(condErrMsg, input);
			}
			if ("false".equals(cv))
				break;
			Result<String, InterpretError> rBody = executeSimpleOrExpression(body, env, mutable, lastValue, input,
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

	/**
	 * If declaredType is non-empty and expr is an integer literal with a suffix,
	 * checks that the suffix equals declaredType. Returns an Err on mismatch or
	 * null on success/no-op.
	 */
	private static Result<String, InterpretError> ensureIntegerSuffixMatches(String declaredType, String expr,
			String input, String errMsg) {
		if (declaredType == null || declaredType.isEmpty())
			return null;
		String[] split = splitIntegerSuffix(expr == null ? "" : expr);
		if (split != null) {
			String suf = split[1];
			// declaredType may be a union like "I32 | U8" - accept any alternative
			String[] alternatives = declaredType.split("\\|");
			boolean match = false;
			for (String a : alternatives) {
				if (a.trim().equals(suf)) {
					match = true;
					break;
				}
			}
			if (!match) {
				return err(errMsg, input);
			}
		}
		return null;
	}

	private static Result<String, InterpretError> executeSimpleOrExpression(String stmt,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue,
			String input,
			String assignmentContextMessage,
			String exprContextMessage) {
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
			if (!isInteger(v))
				return err("Right-hand side of '+=' is not an integer", input);
			try {
				int b = Integer.parseInt(v);
				int sum = a + b;
				env.put(name, new IntVal(sum));
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
				// inline performAssignmentInEnv to avoid duplication detected by CPD
				String[] ne = splitNameExpr(stmt);
				if (ne == null)
					return err("Missing '=' in assignment", input);
				String name = ne[0];
				String expr = ne[1];
				Result<String, InterpretError> checkRes = ensureExistsAndMutableOrErr(name, env, mutable, input);
				if (checkRes != null)
					return checkRes;
				// If the variable has a declared type, and the RHS is an integer literal with a
				// suffix (e.g., 20U8), ensure the suffix matches the declared type stored
				// under 'type:var:NAME'. If mismatch, return error.
				String declared = null;
				Value tval = env.get("type:var:" + name);
				if (tval instanceof TypeAliasVal ta)
					declared = ta.targetType();
				Result<String, InterpretError> sufErr = ensureIntegerSuffixMatches(declared, expr, input,
						"Mismatched types in assignment");
				if (sufErr != null)
					return sufErr;
				Option<String> value = evalAndPut(name, expr, env);
				Result<String, InterpretError> r = optionToResult(value, input, assignmentContextMessage);
				return setLastFromResultOrErr(r, lastValue);
			}
		}

		// post-increment
		if (stmt.endsWith("++")) {
			String name = stmt.substring(0, stmt.length() - 2).trim();
			return performIncrement(name, env, mutable, lastValue, input);
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

	private static Result<String, InterpretError> performIncrement(String name,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue,
			String input) {
		Result<Integer, InterpretError> r = getIntegerVarOrErr(name, env, mutable, input);
		if (r instanceof Err(var err))
			return new Err<>(err);
		int v = ((Ok<Integer, InterpretError>) r).value();
		int nv = v + 1;
		env.put(name, new IntVal(nv));
		lastValue.set(Integer.toString(nv));
		return null;
	}

	private static Result<Integer, InterpretError> getIntegerVarOrErr(String name,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			String input) {
		Result<String, InterpretError> check = ensureExistsAndMutableOrErr(name, env, mutable, input);
		if (check != null) {
			if (check instanceof Err(var e))
				return new Err<>(e);
		}
		String cur = fromValue(env.get(name));
		if (!isInteger(cur))
			return new Err<>(new InterpretError("Cannot use non-integer variable '" + name + "'", input));
		try {
			int v = Integer.parseInt(cur);
			return new Ok<>(v);
		} catch (NumberFormatException ex) {
			return new Err<>(new InterpretError("Invalid integer value for variable '" + name + "'", input));
		}
	}

	private static Result<String, InterpretError> performExpressionAndSetLast(String expr,
			Map<String, Value> env,
			AtomicReference<String> lastValue,
			String input,
			String contextMessage) {
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

	private static Result<String, InterpretError> optionToResult(Option<String> opt,
			String input,
			String contextMessage) {
		if (opt instanceof Some(var v)) {
			return new Ok<>(v);
		}
		return err(contextMessage, input);
	}

	private static Option<String> evalFunction(String name,
			Map<String, Value> env,
			Function<FunctionVal, Option<String>> processor) {
		Value fv = env.get(name);
		System.out.println("[DEBUG] call fn " + name + " -> " + fv);
		if (fv instanceof FunctionVal fn) {
			return processor.apply(fn);
		}
		return None.instance();
	}

	private static Option<String> evalZeroArgFunction(String name, Map<String, Value> env) {
		return evalFunction(name, env, fn -> evalBody(fn.body(), env));
	}

	private static Option<String> evalParameterizedFunction(String name, String argsStr, Map<String, Value> env) {
		return evalFunction(name, env, fn -> {
			List<String> params = fn.params();
			if (params == null)
				return None.instance();
			String[] argValues = parseArguments(argsStr, env);
			if (argValues == null || argValues.length != params.size()) {
				return None.instance();
			}
			Map<String, Value> localEnv = new HashMap<>(env);
			for (int i = 0; i < params.size(); i++) {
				localEnv.put(params.get(i).trim(), toValueFromEvaluatedString(argValues[i]));
			}
			return evalBody(fn.body(), localEnv);
		});
	}

	/**
	 * Evaluate a function body which may contain multiple top-level statements
	 * (separated by ';')
	 * using the provided environment (copied into a local env). Returns
	 * Some(lastValue) or None.
	 */
	private static Option<String> evalBody(String body, Map<String, Value> env) {
		// Execute function body such that:
		// - `let` declarations inside the body stay local to the function
		// - assignments to existing variables mutate the outer environment
		Map<String, Value> localEnv = new HashMap<>();
		Map<String, Boolean> localMutable = new HashMap<>();

		// Combined view used by eval/assign logic inside the function body.
		Map<String, Value> combinedEnv = new AbstractMap<>() {
			@Override
			public Set<Entry<String, Value>> entrySet() {
				Set<Entry<String, Value>> s = new HashSet<>();
				// union of keys, local overrides outer
				Set<String> keys = new HashSet<>(env.keySet());
				keys.addAll(localEnv.keySet());
				for (String k : keys) {
					s.add(new SimpleEntry<String, Value>(k, get(k)));
				}
				return s;
			}

			@Override
			public boolean containsKey(Object key) {
				return localEnv.containsKey(key) || env.containsKey(key);
			}

			@Override
			public Value get(Object key) {
				if (localEnv.containsKey(key))
					return localEnv.get(key);
				return env.get(key);
			}

			@Override
			public Value put(String key, Value value) {
				// If the outer environment already contains the key (existing var), write there
				if (env.containsKey(key) && !localEnv.containsKey(key)) {
					return env.put(key, value);
				}
				// Otherwise create/overwrite a local binding
				return localEnv.put(key, value);
			}
		};

		Map<String, Boolean> combinedMutable = new HashMap<>() {
			@Override
			public Boolean get(Object key) {
				if (localMutable.containsKey(key))
					return localMutable.get(key);
				Value mv = env.get("mut:" + key);
				return mv == null ? null : (mv instanceof BoolVal b ? b.value() : null);
			}

			@Override
			public Boolean put(String key, Boolean value) {
				// prefer to persist mutability to outer env if the variable exists there
				if (env.containsKey(key) && !localMutable.containsKey(key)) {
					Value prev = env.put("mut:" + key, new BoolVal(value));
					return prev == null ? null : (prev instanceof BoolVal b ? b.value() : null);
				}
				return localMutable.put(key, value);
			}
		};

		AtomicReference<String> lastValue = new AtomicReference<>(null);
		String[] parts = splitTopLevelStatements(body);
		for (String raw : parts) {
			String stmt = raw.trim();
			if (stmt.isEmpty())
				continue;

			// let declarations inside function body should be local
			Result<String, InterpretError> letRes = processLetIfPresent(stmt, combinedEnv, combinedMutable, lastValue, body);
			if (letRes instanceof Err)
				return None.instance();
			if (letRes instanceof Ok)
				continue;

			Result<String, InterpretError> rMain = executeSimpleOrExpression(stmt, combinedEnv, combinedMutable, lastValue,
					body,
					"Invalid assignment in function body", "Undefined expression: " + stmt);
			if (rMain != null)
				return None.instance();
		}
		if (lastValue.get() != null)
			return new Some<>(lastValue.get());
		return None.instance();
	}

	private static String[] parseArguments(String argsStr, Map<String, Value> env) {
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

	/**
	 * If the string is an integer literal with a suffix like 10U8 or 5I32,
	 * return a String[]{numericPart, suffix} otherwise return null.
	 */
	private static String[] splitIntegerSuffix(String t) {
		if (t == null)
			return null;
		String s = t.trim();
		int pos = 0;
		while (pos < s.length() && Character.isDigit(s.charAt(pos)))
			pos++;
		if (pos > 0 && pos < s.length()) {
			String numPart = s.substring(0, pos);
			String suf = s.substring(pos);
			if ((suf.startsWith("U") || suf.startsWith("I")) && suf.length() > 1) {
				boolean ok = true;
				for (int j = 1; j < suf.length(); j++) {
					if (!Character.isDigit(suf.charAt(j))) {
						ok = false;
						break;
					}
				}
				if (ok && isInteger(numPart)) {
					return new String[] { numPart, suf };
				}
			}
		}
		return null;
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

	// NOTE: previous wrapper removed to avoid CPD duplication; call
	// executeSimpleOrExpression directly

	private static Result<String, InterpretError> ensureExistsAndMutableOrErr(String name,
			Map<String, Value> env,
			Map<String, Boolean> mutable,
			String input) {
		if (!env.containsKey(name)) {
			return new Err<>(new InterpretError("Undefined value", input));
		}
		Boolean m = mutable.get(name);
		if (m == null) {
			Value mv = env.get("mut:" + name);
			m = (mv instanceof BoolVal b) ? b.value() : false;
		}
		if (!Boolean.TRUE.equals(m)) {
			return new Err<>(new InterpretError("Immutable assignment", input));
		}
		return null;
	}

	// Consolidate storing function-like definitions into env in a single place to
	// avoid duplicated token sequences (helps CPD)
	private static void storeFunctionInEnv(String key, String params, String body, Map<String, Value> env) {
		if (params == null || params.trim().isEmpty()) {
			env.put(key, new FunctionVal(java.util.List.of(), body));
		} else {
			String[] paramNames = parseParameterNames(params);
			env.put(key, new FunctionVal(java.util.Arrays.asList(paramNames), body));
		}
	}

	private static Option<String> evalExpr(String expr, Map<String, Value> env) {
		String t = expr == null ? "" : expr.trim();

		// NEW: strip matching outer parentheses
		while (t.length() >= 2 && t.charAt(0) == '(' && t.charAt(t.length() - 1) == ')') {
			t = t.substring(1, t.length() - 1).trim();
		}

		System.out.println("[DEBUG] evalExpr called with: '" + t + "'");

		// address-of: &name -> pointer to variable name
		if (t.startsWith("&")) {
			String var = t.substring(1).trim();
			// Only allow simple variable names for now
			if (!env.containsKey(var))
				return None.instance();
			// store pointer as a special value encoding
			env.put("__ptr__" + var, new magma.value.PointerVal(var));
			// represent pointer as special inst: pointer|target=var
			return new Some<>("inst:ptr|target=" + var);
		}

		// dereference: *name -> value of the variable pointed to
		if (t.startsWith("*")) {
			String var = t.substring(1).trim();
			// If var is a pointer expression (like *y where y holds a pointer representation),
			// evaluate var first
			Option<String> pv = evalExpr(var, env);
			String pstr = null;
			if (pv instanceof Some(var v)) {
				pstr = v;
			} else {
				pstr = fromValue(env.get(var));
			}
			if (pstr == null || !pstr.startsWith("inst:ptr|target="))
				return None.instance();
			String target = pstr.substring("inst:ptr|target=".length());
			// return the value of the target variable
			String val = fromValue(env.get(target));
			if (val == null)
				return None.instance();
			return new Some<>(val);
		}

		// top-level 'is' operator for runtime type checking: left is Type
		int isIdx = findTopLevelIs(t);
		if (isIdx != -1) {
			String leftTok = t.substring(0, isIdx).trim();
			String rightTok = t.substring(isIdx + 3).trim();
			// determine left runtime type without forcing full evaluation when possible
			String leftType = null;
			// literal integer with suffix: 5U8 or 10I32
			String[] split = splitIntegerSuffix(leftTok);
			if (split != null) {
				leftType = split[1];
			} else if (isBoolean(leftTok)) {
				leftType = "Bool";
			} else {
				// try evaluating; instances are encoded as inst:Type|...
				Option<String> lv = evalExpr(leftTok, env);
				leftType = inferRuntimeTypeFromEvaluatedValue(lv);
			}
			// rightTok may be a union like 'I32 | U8' or a single type; check if leftType
			// matches any alternative
			if (leftType == null) {
				// If left is a variable with declared type, consult that (e.g., union types)
				if (env.containsKey("type:var:" + leftTok)) {
					String declared = null;
					Value tv = env.get("type:var:" + leftTok);
					if (tv instanceof TypeAliasVal ta)
						declared = ta.targetType();
					// declared may be a union; if any alternative matches RHS we'll return true
					// below
					leftType = declared; // store temporarily to allow match against RHS
				} else {
					Option<String> lv = evalExpr(leftTok, env);
					leftType = inferRuntimeTypeFromEvaluatedValue(lv);
				}
			}
			if (leftType == null) {
				return None.instance();
			}
			String[] rights = rightTok.split("\\|");
			String[] leftAlts = leftType.split("\\|");
			for (String r : rights) {
				String rtrim = r.trim();
				for (String la : leftAlts) {
					if (la.trim().equals(rtrim))
						return new Some<>("true");
				}
			}
			return new Some<>("false");
		}

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
			return evalBinaryOperation(left, right, env, (lv, rv) -> new Some<>(lv.equals(rv) ? "true" : "false"));
		}

		// binary less-than: a < b
		int lt = t.indexOf('<');
		if (lt != -1) {
			String left = t.substring(0, lt).trim();
			String right = t.substring(lt + 1).trim();
			return evalIntegerComparison(left, right, env, (li, ri) -> li < ri);
		}

		// boolean and/or: a && b or a || b (short-circuit)
		int andIdx = t.indexOf("&&");
		int orIdx = t.indexOf("||");
		if (andIdx != -1) {
			String left = t.substring(0, andIdx).trim();
			String right = t.substring(andIdx + 2).trim();
			return evalShortCircuitBoolean(left, right, env, true);
		}
		if (orIdx != -1) {
			String left = t.substring(0, orIdx).trim();
			String right = t.substring(orIdx + 2).trim();
			return evalShortCircuitBoolean(left, right, env, false);
		}

		if (isInteger(t)) {
			return new Some<>(expr);
		}

		// support integer literals with suffixes like 5U8, 5I32 etc. - strip suffix and
		// return the numeric part if present
		String[] split = splitIntegerSuffix(t);
		if (split != null) {
			return new Some<>(split[0]);
		}

		if (isBoolean(t)) {
			return new Some<>(t);
		}

		// method call: instance.method() or instance.method(args)
		int dotIndex = t.lastIndexOf('.');
		System.out.println(
				"[DEBUG] Checking method call: dotIndex=" + dotIndex + " endsWithParen=" + t.endsWith(")") + " expr='" + t +
						"'");
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
							Value mv = env.get(methodKey);
							System.out.println("[DEBUG] Found method def: " + mv);
							if (mv instanceof FunctionVal fndef) {
								if (methodArgsStr.isEmpty()) {
									// Zero-arg method call
									String methodBody = fndef.body();
									Option<String> result = evalExpr(methodBody, env);
									System.out.println("[DEBUG] Method result: " + result);
									return result;
								} else {
									// Method call with arguments - for now, simple implementation
									List<String> paramNames = fndef.params();
									String methodBody = fndef.body();
									Map<String, Value> localEnv = new HashMap<>(env);
									String[] args = methodArgsStr.split(",");
									for (int i = 0; i < Math.min(paramNames.size(), args.length); i++) {
										Option<String> argValue = evalExpr(args[i].trim(), env);
										if (argValue instanceof Some(var av)) {
											localEnv.put(paramNames.get(i).trim(), toValueFromEvaluatedString(av));
										}
									}
									return evalExpr(methodBody, localEnv);
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

		// brace block: execute inner body in a local scope and optionally evaluate a
		// trailing expression. This ensures let-declarations inside the block do not
		// leak to the outer environment.
		if (t.startsWith("{")) {
			int openIdx = 0;
			int closeIdx = findMatchingBrace(t, openIdx);
			if (closeIdx != -1) {
				String inner = t.substring(openIdx + 1, closeIdx).trim();
				String remainder = t.substring(closeIdx + 1).trim();
				// evaluate the block in a local environment (evalBody copies the env)
				Option<String> blockResult = evalBody(inner, env);
				if (remainder.isEmpty()) {
					return blockResult instanceof Some ? blockResult : None.instance();
				}
				// if there's a trailing expression after the block, evaluate it in the
				// outer environment (block-local declarations should not leak)
				return evalExpr(remainder, env);
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
					Value dv = env.get("struct:def:" + possibleType);
					String[] fields = new String[0];
					if (dv instanceof StructDefVal sd) {
						fields = sd.fields().toArray(new String[0]);
					}

					if (fields.length == 0) {
						// Empty struct: create instance without any fields
						StringBuilder sb = new StringBuilder();
						sb.append("inst:").append(possibleType);
						System.out.println("[DEBUG] construct -> " + sb);
						return new Some<>(sb.toString());
					} else {
						// Non-empty struct: support comma-separated positional initializers
						String[] values = parseArguments(inner, env);
						if (values != null && values.length == fields.length) {
							StringBuilder sb = new StringBuilder();
							sb.append("inst:").append(possibleType);
							for (int i = 0; i < fields.length; i++) {
								sb.append("|").append(fields[i].trim()).append("=").append(values[i]);
							}
							System.out.println("[DEBUG] construct -> " + sb);
							return new Some<>(sb.toString());
						}
						// Fallback: try single-expression initializer for backwards compatibility
						Option<String> val = evalExpr(inner, env);
						if (val instanceof Some(var fv)) {
							StringBuilder sb = new StringBuilder();
							sb.append("inst:").append(possibleType);
							sb.append("|").append(fields[0].trim()).append("=").append(fv);
							System.out.println("[DEBUG] construct -> " + sb);
							return new Some<>(sb.toString());
						}
						return None.instance();
					}
				}
			}
		}

		// Handle 'this' keyword - return a struct-like representation with parameter
		// fields
		if ("this".equals(t)) {
			// Look for parameters in the environment and create a struct representation
			StringBuilder thisStruct = new StringBuilder("inst:this");
			for (Map.Entry<String, Value> e : env.entrySet()) {
				String key = e.getKey();
				if (key.startsWith("impl:") || key.startsWith("mut:"))
					continue;
				Value v = e.getValue();
				if (!(v instanceof FunctionVal) && !(v instanceof StructDefVal) && !(v instanceof EnumDefVal)
						&& !(v instanceof TraitDefVal) && !(v instanceof TypeAliasVal)) {
					thisStruct.append("|").append(key).append("=").append(fromValue(v));
				}
			}
			return new Some<>(thisStruct.toString());
		}

		String v = fromValue(env.get(t));
		if (v != null) {
			// function stored as special env entry 'fn:BODY'
			if (v.startsWith("fn:")) {
				String body = v.substring(3);
				return evalExpr(body, env);
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

		// handle modulo: a % b
		int mod = t.indexOf('%');
		if (mod != -1) {
			String left = t.substring(0, mod).trim();
			String right = t.substring(mod + 1).trim();
			return evalArithmeticOperation(left, right, env, (a, b) -> b == 0 ? null : a % b);
		}

		// member access: var.field or expression.field
		int dot = t.indexOf('.');
		if (dot != -1) {
			String varName = t.substring(0, dot).trim();
			String fieldName = t.substring(dot + 1).trim();

			// First try to evaluate the left side as an expression
			Option<String> instanceOpt = evalExpr(varName, env);
			String inst;
			if (instanceOpt instanceof Some(var instanceValue)) {
				inst = instanceValue;
			} else {
				// Fallback to direct variable lookup
				inst = fromValue(env.get(varName));
			}

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

	private static Option<String> evalAndPut(String name, String expr, Map<String, Value> env) {
		Option<String> opt = evalExpr(expr, env);
		if (opt instanceof Some(var optValue)) {
			env.put(name, toValueFromEvaluatedString(optValue));
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

	/**
	 * Parse the RHS of an arrow (=>) from `rest` starting at arrow index and
	 * return a String[]{body, remainder} or null if malformed.
	 */
	private static String[] parseArrowBodyWithRemainder(String rest, int arrow) {
		String afterArrow = rest.substring(arrow + 2).trim();
		String body;
		String remainder = "";
		if (afterArrow.startsWith("{")) {
			int bodyOpenIdx = rest.indexOf('{', arrow + 2);
			if (bodyOpenIdx == -1)
				return null;
			int bodyCloseIdx = findMatchingBrace(rest, bodyOpenIdx);
			if (bodyCloseIdx == -1)
				return null;
			body = rest.substring(bodyOpenIdx + 1, bodyCloseIdx).trim();
			remainder = rest.substring(bodyCloseIdx + 1).trim();
		} else {
			body = afterArrow;
		}
		return new String[] { body, remainder };
	}

	private static String extractParams(String rest, int open, int close) {
		return rest.substring(open + 1, close).trim();
	}

	private static int findArrow(String rest, int close) {
		return rest.indexOf("=>", close);
	}

	/**
	 * Extract params, parse arrow body (which may be braced) and return
	 * String[]{params, body, remainder} or null if malformed.
	 */
	private static String[] extractParamsArrowBodyRemainder(String rest, int open, int close) {
		String params = extractParams(rest, open, close);
		int arrow = findArrow(rest, close);
		if (arrow == -1)
			return null;
		String[] parsed = parseArrowBodyWithRemainder(rest, arrow);
		if (parsed == null)
			return null;
		return new String[] { params, parsed[0], parsed[1] };
	}

	private static Result<ParsedNameBody, InterpretError> parseAndValidateNameBody(String rest,
			String errorType,
			String input) {
		String[] parsed = parseNameBodyRemainder(rest);
		if (parsed == null)
			return new Err<>(new InterpretError("Malformed " + errorType, input));
		String name = parsed[0];
		String body = parsed[1];
		String remainder = parsed[2];
		if (name.isEmpty())
			return new Err<>(new InterpretError("Malformed " + errorType, input));
		return new Ok<>(new ParsedNameBody(name, body, remainder));
	}

	private static Result<ParsedNameBody, InterpretError> parseNameBodyExtraction(String rest,
			String errorType,
			String input) {
		Result<ParsedNameBody, InterpretError> parseResult = parseAndValidateNameBody(rest, errorType, input);
		if (parseResult instanceof Err(var error)) {
			return new Err<>(error);
		}
		return parseResult;
	}

	private static String[] splitNames(String s) {
		if (s == null || s.trim().isEmpty())
			return new String[0];
		return s.split(",");
	}

	private static Option<String> evalIntegerComparison(String left,
			String right,
			Map<String, Value> env,
			BiFunction<Integer, Integer, Boolean> comparison) {
		return evalIntegerOperation(left, right, env, (li, ri) -> new Some<>(comparison.apply(li, ri) ? "true" : "false"));
	}

	/**
	 * Get the type suffix from an expression if it has one, otherwise return null.
	 */
	private static String getTypeSuffix(String expr) {
		String[] split = splitIntegerSuffix(expr == null ? "" : expr.trim());
		return split != null ? split[1] : null;
	}

	private static Option<String> evalArithmeticOperation(String left,
			String right,
			Map<String, Value> env,
			BinaryOperator<Integer> operation) {
		// Check for type suffix compatibility before performing arithmetic
		String leftSuffix = getTypeSuffix(left);
		String rightSuffix = getTypeSuffix(right);

		// If both operands have type suffixes, they must match
		if (leftSuffix != null && rightSuffix != null && !leftSuffix.equals(rightSuffix)) {
			return None.instance(); // Type mismatch
		}

		return evalIntegerOperation(left, right, env, (li, ri) -> {
			Integer result = operation.apply(li, ri);
			if (result == null) // for division by zero
				return None.instance();
			return new Some<>(String.valueOf(result));
		});
	}

	private static Option<String> evalBinaryOperation(String left,
			String right,
			Map<String, Value> env,
			BiFunction<String, String, Option<String>> combiner) {
		Option<String> lopt = evalExpr(left, env);
		Option<String> ropt = evalExpr(right, env);
		if (lopt instanceof Some(var lv) && ropt instanceof Some(var rv)) {
			return combiner.apply(lv, rv);
		}
		return None.instance();
	}

	private static Option<String> evalShortCircuitBoolean(String left,
			String right,
			Map<String, Value> env,
			boolean isAnd) {
		Option<String> lopt = evalExpr(left, env);
		if (lopt instanceof Some(var lv)) {
			if (!isBoolean(lv))
				return None.instance();
			if (isAnd) {
				if ("false".equals(lv)) {
					return shortCircuitRequireRhsBoolean(right, env, "false");
				}
				// left true -> evaluate right (normal evaluation, allow side-effects)
				Option<String> ropt = evalExpr(right, env);
				return booleanOptionToSome(ropt);
			} else {
				if ("true".equals(lv)) {
					return shortCircuitRequireRhsBoolean(right, env, "true");
				}
				// left false -> evaluate right (normal evaluation, allow side-effects)
				Option<String> ropt = evalExpr(right, env);
				return booleanOptionToSome(ropt);
			}
		}
		return None.instance();
	}

	private static Option<String> booleanOptionToSome(Option<String> opt) {
		if (opt instanceof Some(var v) && isBoolean(v))
			return new Some<>("true".equals(v) ? "true" : "false");
		return None.instance();
	}

	private static boolean rhsIsBooleanInCopy(String right, Map<String, Value> env) {
		Option<String> ropt = evalExpr(right, new java.util.HashMap<>(env));
		return (booleanOptionToSome(ropt) instanceof Some);
	}

	private static Option<String> shortCircuitRequireRhsBoolean(String right, Map<String, Value> env,
			String returnValue) {
		if (rhsIsBooleanInCopy(right, env))
			return new Some<>(returnValue);
		return None.instance();
	}

	private static Option<String> evalIntegerOperation(String left,
			String right,
			Map<String, Value> env,
			BiFunction<Integer, Integer, Option<String>> operation) {
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

	/**
	 * Update depth counters for paren/brace based on char c. depths[0]=paren,
	 * depths[1]=brace
	 */
	private static void adjustDepths(char c, int[] depths) {
		if (c == '(')
			depths[0]++;
		else if (c == ')')
			depths[0]--;
		else if (c == '{')
			depths[1]++;
		else if (c == '}')
			depths[1]--;
	}

	/**
	 * Find top-level occurrence of ' is ' (space-delimited) not inside
	 * parens/braces. Returns index or -1.
	 */
	private static int findTopLevelIs(String s) {
		int[] depths = new int[2];
		for (int i = 0; i + 4 <= s.length(); i++) {
			char c = s.charAt(i);
			adjustDepths(c, depths);
			if (depths[0] == 0 && depths[1] == 0) {
				// check for " is " starting at i
				if (s.regionMatches(i, " is ", 0, 4)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Infer a runtime-like type string from an evaluated Option value.
	 * Returns a type name (e.g., "Sometype" or "Bool"), or null if unknown.
	 */
	private static String inferRuntimeTypeFromEvaluatedValue(Option<String> opt) {
		if (opt instanceof Some(var v)) {
			String vv = v;
			if (vv.startsWith("inst:")) {
				String rest = vv.substring(5);
				int pipe = rest.indexOf('|');
				return pipe == -1 ? rest : rest.substring(0, pipe);
			} else if (isInteger(vv)) {
				return null; // numeric but no suffix info
			} else if (isBoolean(vv)) {
				return "Bool";
			}
		}
		return null;
	}
}
