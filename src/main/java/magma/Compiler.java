package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

final class Compiler {
	private static final Pattern PATTERN = Pattern.compile("\\s+");

	/**
	 * Compiles the given source code string and returns the compiled output or a
	 * CompileError wrapped in Result.
	 */
	static Result<String, CompileError> compile(String source) {
		// Minimal, non-regex parser/codegen tailored to the small test-suite.
		// Regexes were removed because parsing logic should be explicit and
		// generalizable; regexes tend to conflate tokenization and grammar.

		// Split statements on top-level ';' while preserving brace-delimited blocks
		// (so "while (...) { ...; ...; }" stays as a single statement).
		Collection<String> stmts = new ArrayList<>();
		var sb = new StringBuilder();
		int braceDepth = 0;
		int parenDepth = 0;
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '{') {
				braceDepth++;
				sb.append(c);
			} else if (c == '}') {
				braceDepth--;
				sb.append(c);
			} else if (c == '(') {
				parenDepth++;
				sb.append(c);
			} else if (c == ')') {
				parenDepth--;
				sb.append(c);
			} else if (c == ';' && braceDepth == 0 && parenDepth == 0) {
				var t = sb.toString().trim();
				if (!t.isEmpty() && !t.startsWith("intrinsic ")) {
					stmts.add(t);
				}
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		var remaining = sb.toString().trim();
		if (!remaining.isEmpty() && !remaining.startsWith("intrinsic ")) {
			stmts.add(remaining);
		}

		// Simple symbol table for lets: kind -> "i32" or "bool"
		Map<String, String> kinds = new HashMap<>();
		// track mutability for variables declared with `let`.
		Map<String, Boolean> mutables = new HashMap<>();
		Map<String, String> boolValues = new HashMap<>();

		var finalExpr = "";

		// We'll build C declarations and code in-order so scanf calls map to stdin
		var decls = new StringBuilder();
		var code = new StringBuilder();

		var tempCounter = new int[] { 0 };

		// Process statements: collect lets and remember final expression
		for (var s : stmts) {
			var res = Compiler.processStatement(s, kinds, mutables, boolValues, decls, code, tempCounter, source, true);
			if (res instanceof Result.Err)
				return res;
			if (res instanceof Result.Ok<String, CompileError>(var v) && !v.isEmpty()) {
				finalExpr = v;
			}
		}

		// Generate C program

		// Generate C program
		if (finalExpr.isEmpty()) {
			var cProgram = "#include <stdio.h>\n\nint main(void) {\n  return 0;\n}\n";
			return Result.ok(cProgram);
		}

		var out = new StringBuilder();
		out.append(CodeGen.header());
		out.append(decls);
		out.append(code);

		// Binary ops
		// support simple compile-time if-expression when condition is a literal
		if (finalExpr.startsWith("if")) {
			var open = finalExpr.indexOf('(');
			var close = (0 <= open) ? CompilerHelpers.findMatchingParen(finalExpr, open) : -1;
			if (-1 != open && -1 != close && close > open) {
				var cond = finalExpr.substring(open + 1, close).trim();
				var rest = finalExpr.substring(close + 1).trim();
				var elseIdx = rest.indexOf(" else ");
				if (-1 != elseIdx) {
					var thenExpr = rest.substring(0, elseIdx).trim();
					var elseExpr = rest.substring(elseIdx + 6).trim();
					if ("true".equals(cond) || "false".equals(cond)) {
						finalExpr = ("true".equals(cond)) ? thenExpr : elseExpr;
					} else if (cond.contains("==")) {
						// runtime if with equality condition
						var ee = cond.indexOf("==");
						var l = cond.substring(0, ee).trim();
						var r = cond.substring(ee + 2).trim();
						var check = Compiler.ensureNumericOperands(l, r, kinds, source, "if");
						if (check instanceof Result.Err) {
							return check;
						}
						var lv = CompilerHelpers.emitOperand(l, out, tempCounter);
						var rv = CompilerHelpers.emitOperand(r, out, tempCounter);
						var ctmp = "c" + (tempCounter[0]);
						tempCounter[0]++;
						out.append(CodeGen.declareInt(ctmp));
						out.append(CodeGen.assign(ctmp, "(" + lv + ") == (" + rv + ")"));
						out.append("  if (").append(ctmp).append(") {\n");
						out.append(CompilerHelpers.printSingleExpr(thenExpr, kinds, out, tempCounter));
						out.append("  } else {\n");
						out.append(CompilerHelpers.printSingleExpr(elseExpr, kinds, out, tempCounter));
						out.append("  }\n");
						out.append(CodeGen.footer());
						return Result.ok(out.toString());
					} else {
						return Result.err(new CompileError("unsupported if condition (only literal true/false supported)", source));
					}
				}
			}
		}

		var plus = finalExpr.indexOf('+');
		var minus = finalExpr.indexOf('-');
		var eqeq = finalExpr.indexOf("==");
		var lt = finalExpr.indexOf('<');
		if (-1 != eqeq) {
			var left = finalExpr.substring(0, eqeq).trim();
			var right = finalExpr.substring(eqeq + 2).trim();
			var eqCheck = Compiler.ensureNumericOperands(left, right, kinds, source, "eq");
			if (eqCheck instanceof Result.Err)
				return eqCheck;
			out.append(CompilerHelpers.emitCondPrint(left, right, tempCounter, out));
		} else if (-1 != lt) {
			var left = finalExpr.substring(0, lt).trim();
			var right = finalExpr.substring(lt + 1).trim();
			var ltCheck = Compiler.ensureNumericOperands(left, right, kinds, source, "lt");
			if (ltCheck instanceof Result.Err)
				return ltCheck;
			out.append(CompilerHelpers.emitCondPrint(left, right, tempCounter, out));
			} else {
				if (-1 != plus) {
				var l = finalExpr.substring(0, plus).trim();
				var r = finalExpr.substring(plus + 1).trim();
				var addCheck = Compiler.ensureNumericOperands(l, r, kinds, source, "add");
				if (addCheck instanceof Result.Err)
					return addCheck;
				out.append(CompilerHelpers.emitBinaryPrint(l, r, "+", tempCounter, out));
			} else if (-1 != minus) {
				var l = finalExpr.substring(0, minus).trim();
				var r = finalExpr.substring(minus + 1).trim();
				var subCheck = Compiler.ensureNumericOperands(l, r, kinds, source, "sub");
				if (subCheck instanceof Result.Err)
					return subCheck;
				out.append(CompilerHelpers.emitBinaryPrint(l, r, "-", tempCounter, out));
			} else {
				  out.append(CompilerHelpers.printSingleExpr(finalExpr.trim(), kinds, out, tempCounter));
			}
		}

		out.append(CodeGen.footer());
		return Result.ok(out.toString());
	}

    

	private static Result<String, CompileError> checkAndAppendI32(String targetKind,
			String kind,
			String left,
			String right,
			String source,
			StringBuilder code) {
		var ok = Compiler.ensureI32(targetKind, source);
		if (ok instanceof Result.Err)
			return ok;
		if ("scan".equals(kind)) {
			code.append(CodeGen.scanInt(left));
		} else if ("assign".equals(kind)) {
			code.append(CodeGen.assign(left, right));
		}
		return Result.ok("");
	}

	private static Result<String, CompileError> ensureI32(String targetKind, String source) {
		if (!"i32".equals(targetKind)) {
			return Result.err(new CompileError("type mismatch in assignment", source));
		}
		return Result.ok("");
	}

	private static Result<String, CompileError> ensureAssignableAny(String left,
			Map<String, String> kinds,
			Map<String, Boolean> mutables,
			String source) {
		if (!kinds.containsKey(left)) {
			return Result.err(new CompileError("assignment to undeclared variable: " + left, source));
		}
		if (!mutables.getOrDefault(left, false)) {
			return Result.err(new CompileError("assignment to immutable variable: " + left, source));
		}
		return Result.ok("");
	}

	private static Result<String, CompileError> ensureAssignableI32(String left,
			Map<String, String> kinds,
			Map<String, Boolean> mutables,
			String source) {
		var base = Compiler.ensureAssignableAny(left, kinds, mutables, source);
		if (base instanceof Result.Err)
			return base;
		return Compiler.ensureI32(kinds.get(left), source);
	}

	private static boolean isBoolToken(String token, Map<String, String> kinds) {
		return "true".equals(token) || "false".equals(token) || "bool".equals(kinds.get(token));
	}

	private static Result<String, CompileError> ensureNumericOperands(String left,
			String right,
			Map<String, String> kinds,
			String source,
			String opName) {
		if (Compiler.isBoolToken(left, kinds) || Compiler.isBoolToken(right, kinds)) {
			return Result.err(new CompileError(opName + " requires numeric operands", source));
		}
		return Result.ok("");
	}

	private static Result<String, CompileError> appendAddAssign(String left,
			String right,
			Map<String, String> kinds,
			Map<String, Boolean> mutables,
			StringBuilder decls,
			StringBuilder code,
			int[] tempCounter,
			String source) {
		var pre = Compiler.ensureAssignableI32(left, kinds, mutables, source);
		if (pre instanceof Result.Err)
			return pre;
		var rhsRes = AssignHelpers.resolveRhsToI32Expr(right, kinds, decls, code, tempCounter, source);
		if (rhsRes instanceof Result.Err)
			return rhsRes;
		String rhs;
		if (rhsRes instanceof Result.Ok<String, CompileError>(var value)) {
			rhs = value;
		} else {
			return Result.err(new CompileError("internal error: unexpected result variant", source));
		}
		code.append(CodeGen.assign(left, left + " + " + rhs));
		return Result.ok("");
	}

	static Result<String, CompileError> processStatement(String s,
			Map<String, String> kinds,
			Map<String, Boolean> mutables,
			Map<String, String> boolValues,
			StringBuilder decls,
			StringBuilder code,
			int[] tempCounter,
			String source,
			boolean allowLet) {
		if (s.startsWith("let ")) {
			if (!allowLet)
				return Result.err(new CompileError("let inside while not supported in tests", source));
			var rem = s.substring(4).trim();
			var isMutable = false;
			if (rem.startsWith("mut ")) {
				isMutable = true;
				rem = rem.substring(4).trim();
			}
			var eq = rem.indexOf('=');
			if (-1 == eq)
				return Result.err(new CompileError("malformed let", source));
			var left = rem.substring(0, eq).trim();
			var init = rem.substring(eq + 1).trim();
			String name;
			var type = "";
			var colon = left.indexOf(':');
			if (-1 == colon) {
				name = Compiler.PATTERN.split(left)[0];
			} else {
				name = left.substring(0, colon).trim();
				type = left.substring(colon + 1).trim();
			}
			if (kinds.containsKey(name))
				return Result.err(new CompileError("duplicate variable: " + name, source));
			mutables.put(name, isMutable);
			if ("I32".equals(type) && ("true".equals(init) || "false".equals(init)))
				return Result.err(new CompileError("type mismatch in let", source));
			if ("readInt()".equals(init)) {
				kinds.put(name, "i32");
				decls.append(CodeGen.declareInt(name));
				code.append(CodeGen.scanInt(name));
			} else if ("true".equals(init) || "false".equals(init)) {
				kinds.put(name, "bool");
				boolValues.put(name, init);
				decls.append(CodeGen.declareStr(name));
				code.append(CompilerHelpers.codeForAssignBool(name, init));
			} else {
				try {
					Integer.parseInt(init);
					kinds.put(name, "i32");
					decls.append(CodeGen.declareInt(name));
					code.append(CodeGen.assign(name, init));
				} catch (NumberFormatException nfe) {
					var baseKind = kinds.getOrDefault(init, "i32");
					kinds.put(name, baseKind);
					if ("i32".equals(baseKind)) {
						decls.append(CodeGen.declareInt(name));
						code.append(CodeGen.assign(name, init));
					} else {
						var bv = boolValues.getOrDefault(init, "false");
						kinds.put(name, "bool");
						boolValues.put(name, bv);
						decls.append(CodeGen.declareStr(name));
						code.append(CompilerHelpers.codeForAssignBool(name, bv));
					}
				}
			}
			return Result.	ok("");
		}
		if (s.startsWith("while")) {
			var open = s.indexOf('(');
			var close = CompilerHelpers.findCloseParenFromOpenIdx(s, open);
			if (close == -1)
				return Result.err(new CompileError("malformed while", source));
			var cond = s.substring(open + 1, close).trim();
			var braceOpen = s.indexOf('{', close);
			if (braceOpen == -1)
				return Result.err(new CompileError("malformed while", source));
			var braceClose = CompilerHelpers.findMatchingBrace(s, braceOpen);
			if (braceClose == -1)
				return Result.err(new CompileError("malformed while", source));
			var body = s.substring(braceOpen + 1, braceClose).trim();
			var lrRes = CompilerHelpers.evalLtConditionOrError(cond, kinds, tempCounter, decls, code, source, "while");
			if (lrRes instanceof Result.Err<String[], CompileError>(var e)) return Result.err(e);
			String[] lr;
			if (lrRes instanceof Result.Ok<String[], CompileError>(var _lr)) lr = _lr;
			else return Result.err(new CompileError("internal error: unexpected result variant", source));
			// compile the loop using a shared helper to avoid duplicated fragments
			var loopRes = CompilerHelpers.compileLoopFromLR(lr, body, "", kinds, mutables, boolValues, decls, code, tempCounter, source);
			if (loopRes instanceof Result.Err) return loopRes;
			String tail = "";
			if (loopRes instanceof Result.Ok<String, CompileError>(var tr)) tail = tr;
			var remainder = s.substring(braceClose + 1).trim();
			if (!remainder.isEmpty()) tail = remainder;
			return Result.ok(tail);
		}
		if (s.startsWith("for")) {
			var open = s.indexOf('(');
			var close = CompilerHelpers.findCloseParenFromOpenIdx(s, open);
			if (close == -1) return Result.err(new CompileError("malformed for", source));
			var inner = s.substring(open + 1, close).trim();
			var parts = inner.split(";");
			if (parts.length != 3) return Result.err(new CompileError("malformed for", source));
			var init = parts[0].trim();
			var cond = parts[1].trim();
			var post = parts[2].trim();
			var braceOpen = s.indexOf('{', close);
			String body;
			int braceClose = -1;
			if (braceOpen != -1) {
				braceClose = CompilerHelpers.findMatchingBrace(s, braceOpen);
				if (braceClose == -1) return Result.err(new CompileError("malformed for", source));
				body = s.substring(braceOpen + 1, braceClose).trim();
			} else {
				body = s.substring(close + 1).trim();
			}
			var rinit = Compiler.processStatement(init, kinds, mutables, boolValues, decls, code, tempCounter, source, true);
			if (rinit instanceof Result.Err) return rinit;
			var lrRes = CompilerHelpers.evalLtConditionOrError(cond, kinds, tempCounter, decls, code, source, "for");
			if (lrRes instanceof Result.Err<String[], CompileError>(var e2)) return Result.err(e2);
			String[] lr;
			if (lrRes instanceof Result.Ok<String[], CompileError>(var _lr2)) lr = _lr2;
			else return Result.err(new CompileError("internal error: unexpected result variant", source));
			var loopRes2 = CompilerHelpers.compileLoopFromLR(lr, body, post, kinds, mutables, boolValues, decls, code, tempCounter, source);
			if (loopRes2 instanceof Result.Err) return loopRes2;
			if (braceClose != -1) {
				var rem = s.substring(braceClose + 1).trim();
				if (!rem.isEmpty()) return Result.ok(rem);
			} else {
				var rem = s.substring(close + 1).trim();
				if (!rem.isEmpty()) return Result.ok(rem);
			}
			return Result.ok("");
		}
		if (s.contains("+=")) {
			var idx = s.indexOf("+=");
			var left = s.substring(0, idx).trim();
			var right = s.substring(idx + 2).trim();
			var addAssign = Compiler.appendAddAssign(left, right, kinds, mutables, decls, code, tempCounter, source);
			if (addAssign instanceof Result.Err)
				return addAssign;
			return Result.ok("");
		}
		if (s.contains("=") && !s.contains("==")) {
			var eq = s.indexOf('=');
			var left = s.substring(0, eq).trim();
			var right = s.substring(eq + 1).trim();
			var pre = Compiler.ensureAssignableAny(left, kinds, mutables, source);
			if (pre instanceof Result.Err)
				return pre;
			var targetKind = kinds.get(left);
			if ("readInt()".equals(right)) {
				var check = Compiler.checkAndAppendI32(targetKind, "scan", left, "", source, code);
				if (check instanceof Result.Err)
					return check;
			} else if ("true".equals(right) || "false".equals(right)) {
				if (!"bool".equals(targetKind))
					return Result.err(new CompileError("type mismatch in assignment", source));
				code.append(CompilerHelpers.codeForAssignBool(left, right));
			} else {
				try {
					Integer.parseInt(right);
					var assignCheck = Compiler.checkAndAppendI32(targetKind, "assign", left, right, source, code);
					if (assignCheck instanceof Result.Err)
						return assignCheck;
				} catch (NumberFormatException nfe) {
					var r = AssignHelpers.handleAssignFromIdentifier(left, right, kinds, code, source);
					if (r instanceof Result.Err)
						return r;
				}
			}
			return Result.ok("");
		}
		if (s.endsWith("++")) {
			var var = s.substring(0, s.length() - 2).trim();
			if (!kinds.containsKey(var))
				return Result.err(new CompileError("increment of undeclared variable: " + var, source));
			if (!mutables.getOrDefault(var, false))
				return Result.err(new CompileError("increment of immutable variable: " + var, source));
			var ok = Compiler.ensureI32(kinds.get(var), source);
			if (ok instanceof Result.Err)
				return ok;
			code.append(CodeGen.assign(var, var + " + 1"));
			return Result.ok("");
		}
		return Result.ok(s);
	}

    

  

}
