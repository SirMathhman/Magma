package magma.compiler;

import magma.ast.SeqItem;
import magma.ast.VarDecl;
import magma.parser.Parser;
import magma.parser.ParserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import magma.ast.Unit;
import magma.diagnostics.CompileError;
import magma.util.Err;

public final class CompilerUtil {
	private CompilerUtil() {
	}

	// Helper to add a VarDecl to both lists
	public static void appendDecl(List<VarDecl> decls,
																List<SeqItem> seq,
																VarDecl vd) {
		decls.add(vd);
		seq.add(vd);
	}

	public static boolean isBracedNumeric(String s) {
		if (s == null) return false;
		var t = s.trim();
		if (t.length() < 3 || t.charAt(0) != '{' || t.charAt(t.length() - 1) != '}') return false;
		var inner = t.substring(1, t.length() - 1).trim();
		if (inner.isEmpty()) return false;
		for (var i = 0; i < inner.length(); i++) {
			if (!Character.isDigit(inner.charAt(i))) return false;
		}
		return true;
	}

	public static boolean isPlainNumeric(String s) {
		if (s == null) return false;
		var t = s.trim();
		if (t.isEmpty()) return false;
		for (var i = 0; i < t.length(); i++) {
			if (!Character.isDigit(t.charAt(i))) return false;
		}
		return true;
	}

	public static boolean isIdentifierChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch == '_';
	}

	public static int findStandaloneTokenEnd(String src, String key, int start) {
		if (src == null || src.isEmpty()) return -1;
		var idx = start;
		while (true) {
			idx = src.indexOf(key, idx);
			if (idx == -1) return -1;
			if (idx > 0) {
				var prev = src.charAt(idx - 1);
				if (Character.isLetterOrDigit(prev) || prev == '_') {
					idx += key.length();
					continue;
				}
			}
			return idx + key.length();
		}
	}

	public static int findStandaloneTokenIndex(String src, String key, int start) {
		var end = findStandaloneTokenEnd(src, key, start);
		if (end == -1) return -1;
		return end - key.length();
	}

	public static int skipWhitespace(String s, int idx) {
		var j = idx;
		while (j < s.length() && Character.isWhitespace(s.charAt(j))) j++;
		return j;
	}

	public static boolean isTopLevelPos(String s, int pos) {
		if (s == null || pos < 0) return false;
		var depth = 0;
		for (var i = 0; i < pos && i < s.length(); i++) {
			var ch = s.charAt(i);
			if (ch == '(') depth++;
			else if (ch == ')') depth--;
		}
		return depth == 0;
	}

	public static int findTopLevelOp(String s, String op) {
		if (s == null || op == null) return -1;
		var idx = 0;
		while (true) {
			idx = s.indexOf(op, idx);
			if (idx == -1) return -1;
			if (isTopLevelPos(s, idx)) return idx;
			idx += 1;
		}
	}

	public static int countTopLevelArgs(String s) {
		if (s == null) return 0;
		var t = s.trim();
		if (t.isEmpty()) return 0;
		// reuse ParserUtils
		return ParserUtils.splitTopLevel(t, ',', '(', ')').size();
	}

	public static int countParamsInType(String type) {
		if (type == null) return 0;
		var inner = getParamsInnerTypeSegment(type);
		if (inner == null || inner.isEmpty()) return 0;
		return countTopLevelArgs(inner);
	}

	public static String getParamsInnerTypeSegment(String funcType) {
		if (funcType == null) return null;
		var arrow = funcType.indexOf("=>");
		if (arrow == -1) return null;
		var params = funcType.substring(0, arrow).trim();
		if (params.length() >= 2 && params.charAt(0) == '(' && params.charAt(params.length() - 1) == ')') {
			return params.substring(1, params.length() - 1).trim();
		}
		return null;
	}

	// Convert a param list like "(x : I32, y : I32)" into C params "(int x, int
	// y)".
	public static String paramsToC(String params) {
		if (params == null) return "()";
		var p = params.trim();
		if (p.length() >= 2 && p.charAt(0) == '(' && p.charAt(p.length() - 1) == ')') {
			var inner = p.substring(1, p.length() - 1).trim();
			if (inner.isEmpty()) return "()";
			var parts = inner.split(",");
			var out = new StringBuilder();
			out.append('(');
			var first = true;
			for (var part : parts) {
				var t = part.trim();
				if (t.isEmpty()) continue;
				var colon = t.indexOf(':');
				var name = colon == -1 ? t : t.substring(0, colon).trim();
				var type = "int";
				if (colon != -1) {
					var typ = t.substring(colon + 1).trim();
					if (typ.equals("I32")) type = "int";
					else if (typ.equals("Bool")) type = "int";
					else type = "int";
				}
				if (!first) out.append(", ");
				out.append(type).append(' ').append(name);
				first = false;
			}
			out.append(')');
			return out.toString();
		}
		return "()";
	}

	// Remove type annotations from a parameter list like "(x : I32, y : I32)"
	// without using regular expressions.
	public static String stripParamTypes(String params) {
		if (params == null) return "";
		var out = new StringBuilder();
		var i = 0;
		while (i < params.length()) {
			var c = params.charAt(i);
			if (c == ':') {
				do i++; while (i < params.length() && Character.isWhitespace(params.charAt(i)));
				while (i < params.length()) {
					var cc = params.charAt(i);
					if (cc == ',' || cc == ')') break;
					i++;
				}
			} else {
				out.append(c);
				i++;
			}
		}
		var temp = out.toString();
		var norm = new StringBuilder();
		var lastWs = false;
		for (var j = 0; j < temp.length(); j++) {
			var ch = temp.charAt(j);
			if (Character.isWhitespace(ch)) {
				if (!lastWs) {
					norm.append(' ');
					lastWs = true;
				}
			} else {
				norm.append(ch);
				lastWs = false;
			}
		}
		var cleaned = norm.toString();
		cleaned = cleaned.replace(" ,", ",");
		cleaned = cleaned.replace("( ", "(");
		cleaned = cleaned.replace(" )", ")");
		return cleaned.trim();
	}

	// Find a top-level braced region starting at or after nameStart.
	// Returns int[]{braceStart, braceEnd} or null when not found/unbalanced.
	public static int[] findBracedRegion(String p, int nameStart) {
		if (p == null) return null;
		var brace = p.indexOf('{', nameStart);
		if (brace == -1) return null;
		var braceEnd = ParserUtils.advanceNested(p, brace + 1, '{', '}');
		if (braceEnd == -1) return null;
		return new int[]{brace, braceEnd};
	}

	// Scan left from index j (inclusive) for an identifier and return it, or
	// null if none found. Skips whitespace before the identifier.
	public static String identifierLeftOf(String s, int j) {
		if (s == null || j < 0) return null;
		var k = j;
		while (k >= 0 && Character.isWhitespace(s.charAt(k))) k--;
		if (k < 0) return null;
		var end = k + 1;
		while (k >= 0) {
			var c = s.charAt(k);
			if (Character.isLetterOrDigit(c) || c == '_') k--;
			else break;
		}
		var start = k + 1;
		if (start >= end) return null;
		return s.substring(start, end);
	}

	// Return the LHS identifier of a simple assignment statement `name = ...`,
	// or null if the statement is not an assignment.
	public static String getAssignmentLhs(String stmt) {
		if (stmt == null) return null;
		var idx = 0;
		while (true) {
			idx = stmt.indexOf('=', idx);
			if (idx == -1) break;
			if (idx + 1 < stmt.length() && stmt.charAt(idx + 1) == '=') {
				idx += 2;
				continue;
			}
			if (isTopLevelPos(stmt, idx)) {
				var leftIdx = idx - 1;
				if (leftIdx >= 0) {
					var pc = stmt.charAt(leftIdx);
					if (pc == '+' || pc == '-' || pc == '*' || pc == '/') leftIdx--;
				}
				return identifierLeftOf(stmt, leftIdx);
			}
			idx += 1;
		}

		// compound assignments like '+=', '-=', '*=', '/='
		var comp = new String[]{"+=", "-=", "*=", "/="};
		for (var op : comp) {
			var i = findTopLevelOp(stmt, op);
			if (i != -1) {
				return identifierLeftOf(stmt, i - 1);
			}
		}

		// postfix 'name++' / 'name--'
		var incs = new String[]{"++", "--"};
		for (var op : incs) {
			var i = 0;
			while (true) {
				i = stmt.indexOf(op, i);
				if (i == -1) break;
				if (isTopLevelPos(stmt, i)) {
					var left = identifierLeftOf(stmt, i - 1);
					if (left != null) {
						return left;
					}
					var k = i + op.length();
					while (k < stmt.length() && Character.isWhitespace(stmt.charAt(k))) k++;
					if (k < stmt.length() && isIdentifierChar(stmt.charAt(k))) {
						var l = k;
						while (l < stmt.length() && isIdentifierChar(stmt.charAt(l))) l++;
						return stmt.substring(k, l);
					}
				}
				i += 1;
			}
		}
		return null;
	}

	public static boolean isCompoundOrIncrement(String stmt) {
		if (stmt == null) return false;
		var ops = new String[]{"++", "--", "+=", "-=", "*=", "/="};
		for (var op : ops) {
			if (findTopLevelOp(stmt, op) != -1) return true;
		}
		return false;
	}

	// Register an impl function declaration text as a method for ownerName into
	// the Compiler's implMethods and implMethodBodies maps. Returns true if
	// registration occurred.
	public static boolean registerImplFn(Compiler self, String ownerName, String fnDecl) {
		String[] fparts = Parser.parseFnDeclaration(self, fnDecl);
		if (fparts == null) return false;
		var mname = fparts[0];
		var params = fparts[1];
		var body = fparts[3];
		var paramsClean = stripParamTypes(params);
		String funcExpr;
		if (body != null && body.trim().startsWith("{")) {
			var ensured = Parser.ensureReturnInBracedBlock(self, body, false, params);
			funcExpr = "function" + paramsClean + " " + ensured;
		} else {
			var expr = self.unwrapBraced(body);
			funcExpr = "function" + paramsClean + " { return " + expr + "; }";
		}
		var map = self.implMethods.get(ownerName);
		if (map == null) {
			map = new HashMap<>();
			self.implMethods.put(ownerName, map);
		}
		map.put(mname, funcExpr);
		if ("()".equals(paramsClean)) {
			var bodyExpr =
					(body != null && body.trim().startsWith("{")) ? Parser.ensureReturnInBracedBlock(self, body, true, "")
																												: self.unwrapBraced(body);
			self.implMethodBodies.put(ownerName + "." + mname, bodyExpr);
		}
		return true;
	}

		// Validate assignment targets in then/else branches. If both branches assign the same
		// variable, mark it assigned. Returns an Err on invalid assignment (undefined or
		// mismatched targets), otherwise null.
		public static Err<Set<Unit>, CompileError> handleThenElseAssignment(Compiler self, String lhsThen, String lhsElse, List<VarDecl> decls, Map<String, Boolean> assigned) {
			if (lhsThen == null && lhsElse == null)
				return null;
			if (lhsThen != null && lhsElse != null && !lhsThen.equals(lhsElse)) {
				return new Err<>(new CompileError("Mismatched assignment targets in then/else: '" + lhsThen + "' vs '" + lhsElse + "'"));
			}
			String target = lhsThen != null ? lhsThen : lhsElse;
			if (target != null) {
				boolean declared = false;
				for (var vd : decls) {
					if (vd.name().equals(target)) { declared = true; break; }
				}
				if (!declared) {
					return new Err<>(new CompileError("Assignment to undefined variable '" + target + "' in branch"));
				}
				if (lhsThen != null && lhsElse != null && lhsThen.equals(lhsElse)) {
					assigned.put(target, true);
				}
			}
			return null;
		}
}
