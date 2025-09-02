package magma.compiler;

import java.util.List;
import java.util.Set;

import magma.ast.Unit;
import magma.ast.VarDecl;
import magma.diagnostics.CompileError;
import magma.util.Err;
import magma.parser.ParserUtils;

// Semantic helpers grouped to reduce method count in Compiler
public final class Semantic {
	private Semantic() {
	}

	public static String[] parseIfExpression(Compiler self, String src) {
		if (src == null) return null;
		String s = src.trim();
		int ifIdx = CompilerUtil.findStandaloneTokenIndex(s, "if", 0);
		if (ifIdx != 0) return null;
		int afterIf = ifIdx + "if".length();
		while (afterIf < s.length() && Character.isWhitespace(s.charAt(afterIf))) afterIf++;
		if (afterIf >= s.length() || s.charAt(afterIf) != '(') return null;
		int p = self.advanceNested(s, afterIf + 1);
		if (p == -1) return null;
		String cond = s.substring(afterIf + 1, p - 1).trim();
		int thenStart = p;
		while (thenStart < s.length() && Character.isWhitespace(s.charAt(thenStart))) thenStart++;
		int elseIdx = CompilerUtil.findStandaloneTokenIndex(s, "else", thenStart);
		if (elseIdx == -1) return null;
		String thenExpr = s.substring(thenStart, elseIdx).trim();
		int afterElse = elseIdx + "else".length();
		while (afterElse < s.length() && Character.isWhitespace(s.charAt(afterElse))) afterElse++;
		String elseExpr = s.substring(afterElse).trim();
		if (thenExpr.isEmpty() || elseExpr.isEmpty()) return null;
		return new String[]{cond, thenExpr, elseExpr};
	}

	public static Err<Set<Unit>, CompileError> validateFunctionCallArity(Compiler self, String src, List<VarDecl> decls) {
		if (src == null || src.isEmpty()) return null;
		for (VarDecl vd : decls) {
			if (vd.type != null && vd.type.contains("=>")) {
				String name = vd.name;
				int idx = 0;
				while (true) {
					int pos = CompilerUtil.findStandaloneTokenIndex(src, name, idx);
					if (pos == -1) break;
					int j = CompilerUtil.skipWhitespace(src, pos + name.length());
					if (j < src.length() && src.charAt(j) == '(') {
						int end = self.advanceNested(src, j + 1);
						if (end == -1) return new Err<>(new CompileError("Unbalanced parentheses in call to '" + name + "'"));
						String argText = src.substring(j + 1, end - 1);
						int argCount = CompilerUtil.countTopLevelArgs(argText);
						int declParams = CompilerUtil.countParamsInType(vd.type);
						if (argCount != declParams)
							return new Err<>(new CompileError("Wrong number of arguments in call to '" + name + "'"));
						List<String> args = Semantic.splitTopLevelArgs(argText);
						for (int a = 0; a < args.size(); a++) {
							String at = args.get(a).trim();
							String expected = Semantic.paramTypeAtIndex(self, vd.type, a);
							String actual = Semantic.exprType(self, at, decls);
							if (expected != null && actual != null && !expected.equals(actual)) {
								return new Err<>(new CompileError("Wrong argument type in call to '" + name + "'"));
							}
						}
						idx = end;
					} else {
						idx = j;
					}
				}
			}
		}
		return null;
	}

	public static List<String> splitTopLevelArgs(String s) {
		return ParserUtils.splitTopLevel(s, ',', '(', ')');
	}

	public static List<String> splitTopLevel(String s, char sep, char open, char close) {
		return ParserUtils.splitTopLevel(s, sep, open, close);
	}

	public static String paramTypeAtIndex(Compiler self, String funcType, int idx) {
		String inner = CompilerUtil.getParamsInnerTypeSegment(funcType);
		if (inner == null) return null;
		List<String> parts = splitTopLevelArgs(inner);
		if (idx < 0 || idx >= parts.size()) return null;
		String p = parts.get(idx).trim();
		int colon = p.indexOf(':');
		if (colon == -1) return null;
		return p.substring(colon + 1).trim();
	}

	public static String exprType(Compiler self, String expr, List<VarDecl> decls) {
		if (expr == null) return null;
		String s = expr.trim();
		if (s.isEmpty()) return null;
		if (s.equals("true") || s.equals("false")) return "Bool";
		if (CompilerUtil.isPlainNumeric(s) || CompilerUtil.isBracedNumeric(s)) return "I32";
		if (self.findReadIntUsage(s) == 1) return "I32";
		int parenIdx = s.indexOf('(');
		if (parenIdx != -1) {
			String fnName = CompilerUtil.identifierLeftOf(s, parenIdx - 1);
			if (fnName != null) {
				for (VarDecl vd : decls) {
					if (vd.name.equals(fnName)) {
						String dt = self.dTypeOf(vd);
						if (dt != null && dt.contains("=>")) {
							int arrow = dt.indexOf("=>");
							String ret = dt.substring(arrow + 2).trim();
							if (ret.isEmpty()) return "I32";
							return ret;
						}
					}
				}
			}
		}
		if (s.matches("[A-Za-z_][A-Za-z0-9_]*")) {
			for (VarDecl vd : decls) {
				if (vd.name.equals(s)) {
					String dt = self.dTypeOf(vd);
					if (dt != null && !dt.isEmpty()) {
						if (dt.contains("=>")) return null;
						return dt;
					}
				}
			}
		}
		return null;
	}

	public static Err<Set<Unit>, CompileError> detectNonIdentifierCall(String src) {
		if (src == null || src.isEmpty()) return null;
		int idx = 0;
		while (true) {
			int p = src.indexOf('(', idx);
			if (p == -1) break;
			int k = p - 1;
			while (k >= 0 && Character.isWhitespace(src.charAt(k))) k--;
			if (k < 0) {
				idx = p + 1;
				continue;
			}
			int end = k + 1;
			int start = k;
			while (start >= 0 && (Character.isLetterOrDigit(src.charAt(start)) || src.charAt(start) == '_')) start--;
			start++;
			if (start >= end) {
				return new Err<>(new CompileError("Invalid function call on non-function"));
			}
			char first = src.charAt(start);
			if (!Character.isJavaIdentifierStart(first) && first != '_') {
				return new Err<>(new CompileError("Invalid function call on non-function"));
			}
			idx = p + 1;
		}
		return null;
	}
}
