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

	private static String[] evalOperandsTwo(String left, String right, int[] tempCounter, StringBuilder out) {
		// emitOperand appends any required decls/code for readInt() temporaries
		var l = CompilerHelpers.emitOperand(left, out, tempCounter);
		var r = CompilerHelpers.emitOperand(right, out, tempCounter);
		return new String[]{l, r};
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
