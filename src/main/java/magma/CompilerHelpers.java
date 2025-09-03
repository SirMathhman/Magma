package magma;

public final class CompilerHelpers {
	static String codeForAssignBool(String name, String val) {
		return CodeGen.assign(name, "\"" + val + "\"");
	}

	static String emitOperand(String op, StringBuilder out, int[] tempCounter) {
		var trim = op.trim();
		if ("readInt()".equals(trim)) {
			var tmp = "r" + (tempCounter[0]);
			tempCounter[0]++;
			out.append(CodeGen.declareInt(tmp));
			out.append(CodeGen.scanInt(tmp));
			return tmp;
		}
		if ("true".equals(trim) || "false".equals(trim)) {
			return "\"" + trim + "\"";
		}
		try {
			Integer.parseInt(trim);
			return trim;
		} catch (NumberFormatException nfe) {
			return trim;
		}
	}

	static String emitReadIntTemp(String tmp, StringBuilder decls, StringBuilder code) {
		decls.append(CodeGen.declareInt(tmp));
		code.append(CodeGen.scanInt(tmp));
		return tmp;
	}

	static int findMatchingParen(String s, int openIdx) {
		int depth = 0;
		for (int i = openIdx; i < s.length(); i++) {
			int cp = s.codePointAt(i);
			if (40 == cp) { // '('
				depth++;
			} else if (41 == cp) { // ')'
				depth--;
				if (0 == depth)
					return i;
			}
		}
		return -1;
	}

	static int findCloseParenFromOpenIdx(String s, int openIdx) {
		if (openIdx < 0) return -1;
		return CompilerHelpers.findMatchingParen(s, openIdx);
	}

	static void appendWhileHeader(StringBuilder code, String[] lr) {
		code.append("  while (").append(lr[0]).append(" < ").append(lr[1]).append(") {\n");
	}

	static int findMatchingBrace(String s, int openIdx) {
		int depth = 0;
		for (int i = openIdx; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '{')
				depth++;
			else if (ch == '}') {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
	}

	private static String[] evalOperandsTwo(String left, String right, int[] tempCounter, StringBuilder out) {
		// emitOperand appends any required decls/code for readInt() temporaries
		var l = CompilerHelpers.emitOperand(left, out, tempCounter);
		var r = CompilerHelpers.emitOperand(right, out, tempCounter);
		return new String[] { l, r };
	}

	static String emitBinaryPrint(String left, String right, String operator, int[] tempCounter, StringBuilder out) {
		String[] lr;
		lr = CompilerHelpers.evalOperandsTwo(left, right, tempCounter, out);
		return CodeGen.printfIntExpr(lr[0] + " " + operator + " " + lr[1]);
	}

	public static String emitCondPrint(String left, String right, int[] tempCounter, StringBuilder out) {
		String[] lr;
		lr = CompilerHelpers.evalOperandsTwo(left, right, tempCounter, out);
		var expr = "((" + lr[0] + ") " + "<" + " (" + lr[1] + ")) ? \"true\" : \"false\"";
		return CodeGen.printfStrExpr(expr);
	}

		// Evaluate a simple '<' condition like "i < readInt()" and emit any
		// required temporaries into decls/code. Returns a two-element array with
		// left and right operand expressions, or null if unsupported.
		public static String[] evalLtCondition(String cond, java.util.Map<String, String> kinds,
				int[] tempCounter, StringBuilder decls, StringBuilder code) {
			if (java.util.Objects.isNull(cond)) return new String[0];
			var idx = cond.indexOf('<');
			if (idx == -1) return new String[0];
			var left = cond.substring(0, idx).trim();
			var right = cond.substring(idx + 1).trim();
			// reject boolean tokens
				if ("true".equals(left) || "false".equals(left) || "true".equals(right) || "false".equals(right)) {
					return new String[0];
				}
			// If either side is readInt(), emit a temp into decls/code and return that temp.
			String lval = left;
			String rval = right;
			if ("readInt()".equals(left)) {
				var tmp = "r" + (tempCounter[0]++);
				CompilerHelpers.emitReadIntTemp(tmp, decls, code);
				lval = tmp;
			}
			if ("readInt()".equals(right)) {
				var tmp = "r" + (tempCounter[0]++);
				CompilerHelpers.emitReadIntTemp(tmp, decls, code);
				rval = tmp;
			}
			return new String[] { lval, rval };
		}

		public static Result<String[], CompileError> evalLtConditionOrError(String cond, java.util.Map<String, String> kinds,
				int[] tempCounter, StringBuilder decls, StringBuilder code, String source, String context) {
			var lr = CompilerHelpers.evalLtCondition(cond, kinds, tempCounter, decls, code);
			if (lr.length == 0) return Result.err(new CompileError("unsupported " + context + " condition (only '<' supported)", source));
			return Result.ok(lr);
		}

		public static Result<String, CompileError> processInnerStatements(String body,
				java.util.Map<String, String> kinds,
				java.util.Map<String, Boolean> mutables,
				java.util.Map<String, String> boolValues,
				StringBuilder decls,
				StringBuilder code,
				int[] tempCounter,
				String source) {
			if (java.util.Objects.isNull(body) || body.isEmpty()) return Result.ok("");
			var inner = body.split(";");
			var tail = "";
			for (var st : inner) {
				var sst = st.trim();
				if (!sst.isEmpty()) {
					var r = Compiler.processStatement(sst, kinds, mutables, boolValues, decls, code, tempCounter, source, false);
					if (r instanceof Result.Err) return r;
					if (r instanceof Result.Ok<String, CompileError>(var v) && !v.isEmpty()) tail = v;
				}
			}
			return Result.ok(tail);
		}

		/**
		 * Compile a loop given left/right operands (lr), body source, and optionally a post-statement.
		 * Appends loop header, body and post handling into code/decls and returns any tail expression.
		 */
		public static Result<String, CompileError> compileLoopFromLR(String[] lr,
				String body,
				String post,
				java.util.Map<String, String> kinds,
				java.util.Map<String, Boolean> mutables,
				java.util.Map<String, String> boolValues,
				StringBuilder decls,
				StringBuilder code,
				int[] tempCounter,
				String source) {
			CompilerHelpers.appendWhileHeader(code, lr);
			var tail = "";
			if (!body.isEmpty()) {
				var bodyRes = CompilerHelpers.processInnerStatements(body, kinds, mutables, boolValues, decls, code, tempCounter, source);
				if (bodyRes instanceof Result.Err) return bodyRes;
				if (bodyRes instanceof Result.Ok<String, CompileError>(var v) && !v.isEmpty()) tail = v;
			}
			if (post != null && !post.isEmpty()) {
				var rpost = Compiler.processStatement(post, kinds, mutables, boolValues, decls, code, tempCounter, source, false);
				if (rpost instanceof Result.Err) return rpost;
			}
			code.append("  }\n");
			return Result.ok(tail);
		}

		public static String printSingleExpr(String op, java.util.Map<String, String> kinds, StringBuilder out,
			int[] tempCounter) {
			var expr = op.trim();
			if ("true".equals(expr) || "false".equals(expr)) {
				return CodeGen.printfStrExpr("\"" + expr + "\"");
			}
			if ("readInt()".equals(expr)) {
				var tmp = CompilerHelpers.emitOperand(expr, out, tempCounter);
				return CodeGen.printfIntExpr(tmp);
			}
			try {
				Integer.parseInt(expr);
				return CodeGen.printfIntExpr(expr);
			} catch (NumberFormatException nfe) {
				if ("bool".equals(kinds.get(expr))) {
					return CodeGen.printfStrExpr(expr);
				} else {
					return CodeGen.printfIntExpr(expr);
				}
			}
		}
}
