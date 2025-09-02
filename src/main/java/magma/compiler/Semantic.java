package magma.compiler;

import magma.ast.StructLiteral;
import magma.ast.Unit;
import magma.ast.VarDecl;
import magma.diagnostics.CompileError;
import magma.parser.ParserUtils;
import magma.util.Err;

import java.util.List;
import java.util.Set;

// Semantic helpers grouped to reduce method count in Compiler
public final class Semantic {
	private Semantic() {
	}

	public static String[] parseIfExpression(Compiler self, String src) {
		if (src == null)
			return null;
		var s = src.trim();
		var ifIdx = CompilerUtil.findStandaloneTokenIndex(s, "if", 0);
		if (ifIdx != 0)
			return null;
		var afterIf = ifIdx + "if".length();
		while (afterIf < s.length() && Character.isWhitespace(s.charAt(afterIf)))
			afterIf++;
		if (afterIf >= s.length() || s.charAt(afterIf) != '(')
			return null;
		var p = self.advanceNested(s, afterIf + 1);
		if (p == -1)
			return null;
		var cond = s.substring(afterIf + 1, p - 1).trim();
		var thenStart = p;
		while (thenStart < s.length() && Character.isWhitespace(s.charAt(thenStart)))
			thenStart++;
		var elseIdx = CompilerUtil.findStandaloneTokenIndex(s, "else", thenStart);
		if (elseIdx == -1)
			return null;
		var thenExpr = s.substring(thenStart, elseIdx).trim();
		var afterElse = elseIdx + "else".length();
		while (afterElse < s.length() && Character.isWhitespace(s.charAt(afterElse)))
			afterElse++;
		var elseExpr = s.substring(afterElse).trim();
		if (thenExpr.isEmpty() || elseExpr.isEmpty())
			return null;
		return new String[] { cond, thenExpr, elseExpr };
	}

	public static Err<Set<Unit>, CompileError> validateFunctionCallArity(Compiler self, String src, List<VarDecl> decls) {
		if (src == null || src.isEmpty())
			return null;
		for (var vd : decls) {
			if (vd.type() != null && vd.type().contains("=>")) {
				var name = vd.name();
				var idx = 0;
				while (true) {
					var pos = CompilerUtil.findStandaloneTokenIndex(src, name, idx);
					if (pos == -1)
						break;
					var j = CompilerUtil.skipWhitespace(src, pos + name.length());
					if (j < src.length() && src.charAt(j) == '(') {
						var end = self.advanceNested(src, j + 1);
						if (end == -1)
							return new Err<>(new CompileError("Unbalanced parentheses in call to '" + name + "'"));
						var argText = src.substring(j + 1, end - 1);
						var argCount = CompilerUtil.countTopLevelArgs(argText);
						var declParams = CompilerUtil.countParamsInType(vd.type());
						if (argCount != declParams)
							return new Err<>(new CompileError("Wrong number of arguments in call to '" + name + "'"));
						var args = Semantic.splitTopLevelArgs(argText);
						for (var a = 0; a < args.size(); a++) {
							var at = args.get(a).trim();
							var expected = Semantic.paramTypeAtIndex(vd.type(), a);
							var actual = Semantic.exprType(self, at, decls);
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

	public static String paramTypeAtIndex(String funcType, int idx) {
		var inner = CompilerUtil.getParamsInnerTypeSegment(funcType);
		if (inner == null)
			return null;
		var parts = splitTopLevelArgs(inner);
		if (idx < 0 || idx >= parts.size())
			return null;
		var p = parts.get(idx).trim();
		var colon = p.indexOf(':');
		if (colon == -1)
			return null;
		return p.substring(colon + 1).trim();
	}

	public static String exprType(Compiler self, String expr, List<VarDecl> decls) {
		if (expr == null)
			return null;
		var s = expr.trim();
		if (s.isEmpty())
			return null;
		// Address-of operator: &x -> pointer to x's type
		if (s.startsWith("&")) {
			var inner = s.substring(1).trim();
			if (inner.matches("[A-Za-z_][A-Za-z0-9_]*")) {
				for (var vd : decls) {
					if (vd.name().equals(inner)) {
						var dt = self.dTypeOf(vd);
						if (dt == null || dt.isEmpty())
							return null;
						return "*" + dt;
					}
				}
			}
			return null;
		}
		// Dereference operator: *p -> underlying pointed type
		if (s.startsWith("*")) {
			var inner = s.substring(1).trim();
			var innerType = exprType(self, inner, decls);
			if (innerType != null && innerType.startsWith("*")) {
				return innerType.substring(1);
			}
			return null;
		}
		if (s.equals("true") || s.equals("false"))
			return "Bool";
		if (CompilerUtil.isPlainNumeric(s) || CompilerUtil.isBracedNumeric(s))
			return "I32";
		if (self.findReadIntUsage(s) == 1)
			return "I32";
		var parenIdx = s.indexOf('(');
		if (parenIdx != -1) {
			var fnName = CompilerUtil.identifierLeftOf(s, parenIdx - 1);
			if (fnName != null) {
				for (var vd : decls) {
					if (vd.name().equals(fnName)) {
						var dt = self.dTypeOf(vd);
						if (dt != null && dt.contains("=>")) {
							var arrow = dt.indexOf("=>");
							var ret = dt.substring(arrow + 2).trim();
							if (ret.isEmpty())
								return "I32";
							return ret;
						}
					}
				}
			}
		}
		if (s.matches("[A-Za-z_][A-Za-z0-9_]*")) {
			for (var vd : decls) {
				if (vd.name().equals(s)) {
					var dt = self.dTypeOf(vd);
					if (dt != null && !dt.isEmpty()) {
						if (dt.contains("=>"))
							return null;
						return dt;
					}
				}
			}
		}
		return null;
	}

	public static Err<Set<Unit>, CompileError> detectNonIdentifierCall(String src) {
		if (src == null || src.isEmpty())
			return null;
		var idx = 0;
		while (true) {
			var p = src.indexOf('(', idx);
			if (p == -1)
				break;
			var k = p - 1;
			while (k >= 0 && Character.isWhitespace(src.charAt(k)))
				k--;
			if (k < 0) {
				idx = p + 1;
				continue;
			}
			var end = k + 1;
			var start = k;
			while (start >= 0 && (Character.isLetterOrDigit(src.charAt(start)) || src.charAt(start) == '_'))
				start--;
			start++;
			if (start >= end) {
				return new Err<>(new CompileError("Invalid function call on non-function"));
			}
			var first = src.charAt(start);
			if (!Character.isJavaIdentifierStart(first) && first != '_') {
				return new Err<>(new CompileError("Invalid function call on non-function"));
			}
			idx = p + 1;
		}
		return null;
	}

	public static CompileError validateStructLiteral(Compiler self, StructLiteral sl, List<VarDecl> decls) {
		if (sl == null)
			return null;
		var provided = sl.vals() == null ? 0 : sl.vals().size();
		var expected = sl.fields() == null ? 0 : sl.fields().size();
		if (provided != expected) {
			return new CompileError(
					"Struct initializer for '" + sl.name() + "' expects " + expected + " values, got " + provided);
		}
		var expectedTypes = self.structs.getFieldTypes(sl.name());
		if (expectedTypes != null) {
			for (var vi = 0; vi < provided; vi++) {
				var valExpr = sl.vals().get(vi).trim();
				var actual = Semantic.exprType(self, valExpr, decls);
				var exp = vi < expectedTypes.size() ? expectedTypes.get(vi) : null;
				if (exp != null && actual != null && !exp.equals(actual)) {
					return new CompileError(
							"Struct initializer type mismatch for '" + sl.name() + "' field '" + sl.fields().get(vi) + "'");
				}
			}
		}
		return null;
	}
}
