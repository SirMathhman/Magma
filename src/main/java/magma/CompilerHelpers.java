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
}
