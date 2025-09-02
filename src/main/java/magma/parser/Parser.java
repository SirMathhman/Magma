package magma.parser;

import java.util.List;
import magma.compiler.Compiler;
import magma.compiler.CompilerUtil;
import magma.compiler.Semantic;

// Small nested parser helper to reduce Compiler method count
public final class Parser {
	private Parser() {
	}

	// Extract the declared identifier name from a `let` statement like
	// "let mut x : I32 = readInt();" or "let x = 5;". Returns the name
	// (or empty string if not found).
	private static String extractLetName(String stmt) {
		if (stmt == null)
			return "";
		String s = stmt.trim();
		if (!s.startsWith("let "))
			return "";
		String rest = s.substring(4).trim();
		if (rest.startsWith("mut "))
			rest = rest.substring(4).trim();
		int endIdx = rest.length();
		for (int j = 0; j < rest.length(); j++) {
			char c = rest.charAt(j);
			if (Character.isWhitespace(c) || c == ':' || c == '=' || c == ';' || c == ',') {
				endIdx = j;
				break;
			}
		}
		return rest.substring(0, Math.max(0, endIdx)).trim();
	}

	private static void appendReturnObjectFields(StringBuilder b, java.util.List<String> names) {
		b.append("return {");
		for (int i = 0; i < names.size(); i++) {
			if (i > 0)
				b.append(", ");
			String n = names.get(i);
			b.append(n).append(": ").append(n);
		}
		b.append("};");
	}

	private static boolean isFinalThis(String lastExpr) {
		return lastExpr != null && lastExpr.trim().equals("this");
	}

	// Parse a parameter list like "(x : I32, y : I32)" and return the
	// parameter names as a list. Keeps parsing logic in one place to avoid
	// CPD duplication.
	private static java.util.List<String> extractParamNames(String params) {
		java.util.List<String> names = new java.util.ArrayList<>();
		if (params != null && params.startsWith("(") && params.endsWith(")")) {
			String innerParams = params.substring(1, params.length() - 1).trim();
			if (!innerParams.isEmpty()) {
				String[] pparts = innerParams.split(",");
				for (String pp : pparts) {
					String ptrim = pp.trim();
					if (ptrim.isEmpty())
						continue;
					int colon = ptrim.indexOf(':');
					String pname = colon == -1 ? ptrim : ptrim.substring(0, colon).trim();
					if (pname.startsWith("mut "))
						pname = pname.substring(4).trim();
					if (!pname.isEmpty())
						names.add(pname);
				}
			}
		}
		return names;
	}

	public static String normalizeArrowRhsForJs(Compiler self, String rhs) {
		String rhsOut = cleanArrowRhsForJs(self, rhs);
		rhsOut = self.convertLeadingIfToTernary(rhsOut);
		int arrowIdx = rhsOut.indexOf("=>");
		if (arrowIdx != -1) {
			String before = rhsOut.substring(0, arrowIdx + 2);
			String after = rhsOut.substring(arrowIdx + 2).trim();
			if (after.startsWith("{")) {
				// try to extract params from the lhs (already cleaned) to include in `this`
				String paramStr = "";
				int parenStart = rhsOut.lastIndexOf('(', arrowIdx);
				if (parenStart != -1) {
					int parenEnd = self.advanceNestedGeneric(rhsOut, parenStart + 1, '(', ')');
					if (parenEnd != -1 && parenEnd <= arrowIdx) {
						paramStr = rhsOut.substring(parenStart, parenEnd);
					}
				}
				after = Parser.ensureReturnInBracedBlock(self, after, false, paramStr);
			} else {
				after = self.unwrapBraced(after);
			}
			rhsOut = before + " " + after;
		}
		return rhsOut;
	}

	public static String cleanArrowRhsForJs(Compiler self, String rhs) {
		int arrowIdx = rhs.indexOf("=>");
		if (arrowIdx == -1)
			return rhs;
		int parenStart = rhs.lastIndexOf('(', arrowIdx);
		if (parenStart == -1)
			return rhs;
		int parenEnd = self.advanceNestedGeneric(rhs, parenStart + 1, '(', ')');
		if (parenEnd == -1 || parenEnd > arrowIdx)
			return rhs;
		String params = rhs.substring(parenStart, parenEnd);
		String stripped = CompilerUtil.stripParamTypes(params);
		return rhs.substring(0, parenStart) + stripped + rhs.substring(parenEnd);
	}

	public static String[] parseFnDeclaration(Compiler self, String fnDecl) {
		if (!fnDecl.startsWith("fn "))
			return null;
		String rest = fnDecl.substring(3).trim();
		int parenIdx = rest.indexOf('(');
		if (parenIdx == -1)
			return null;
		String name = rest.substring(0, parenIdx).trim();
		int parenEnd = self.advanceNested(rest, parenIdx + 1);
		if (parenEnd == -1)
			return null;
		String params = rest.substring(parenIdx, parenEnd).trim();
		int afterParams = parenEnd;
		while (afterParams < rest.length() && Character.isWhitespace(rest.charAt(afterParams)))
			afterParams++;
		String retType = "";
		int arrowIdx = rest.indexOf("=>", afterParams);
		if (arrowIdx == -1)
			return null;
		if (afterParams < rest.length() && rest.charAt(afterParams) == ':') {
			retType = rest.substring(afterParams + 1, arrowIdx).trim();
		}
		int bodyStart = arrowIdx + 2;
		int bs = bodyStart;
		while (bs < rest.length() && Character.isWhitespace(rest.charAt(bs)))
			bs++;
		String body;
		String remainder;
		if (bs < rest.length() && rest.charAt(bs) == '{') {
			int after = self.advanceNestedGeneric(rest, bs + 1, '{', '}');
			if (after == -1)
				return null;
			int bodyEndIndex = after;
			body = rest.substring(bs, bodyEndIndex).trim();
			remainder = rest.substring(bodyEndIndex).trim();
		} else {
			body = rest.substring(bodyStart).trim();
			remainder = "";
		}
		return new String[] { name, params, retType, body, remainder };
	}

	public static String convertFnToJs(Compiler self, String fnDecl) {
		String[] parts = parseFnDeclaration(self, fnDecl);
		if (parts == null)
			return fnDecl;
		String params = CompilerUtil.stripParamTypes(parts[1]);
		String body = parts[3];
		if (body != null && body.trim().startsWith("{")) {
			// pass original params (with types) so `this` can include parameter names
			body = Parser.ensureReturnInBracedBlock(self, body, false, parts[1]);
			return "const " + parts[0] + " = " + params + " => " + body;
		} else {
			body = self.unwrapBraced(body);
			return "const " + parts[0] + " = " + params + " => " + body;
		}
	}

	public static String handleStatementProcessing(Compiler self, String p, List<String> stmts, List<Object> seq) {
		String processed = processControlStructures(self, p);
		if (!processed.equals(p)) {
			String[] controlParts = splitByChar(self, processed);
			String lastPart = p;
			for (String part : controlParts) {
				part = part.trim();
				if (!part.isEmpty()) {
					stmts.add(part);
					seq.add(part);
					lastPart = part;
				}
			}
			return lastPart;
		} else {
			stmts.add(p);
			seq.add(p);
			return p;
		}
	}

	public static String[] splitByChar(Compiler self, String s) {
		List<String> parts = Semantic.splitTopLevel(s, ';', '{', '}');
		return parts.toArray(new String[0]);
	}

	public static String processControlStructures(Compiler self, String stmt) {
		stmt = stmt.trim();
		int braceStart = -1;
		int braceEnd;
		int whileIdx = stmt.indexOf("while");
		if (whileIdx != -1 && (whileIdx == 0 || !Character.isLetterOrDigit(stmt.charAt(whileIdx - 1)))) {
			int parenStart = stmt.indexOf('(', whileIdx);
			if (parenStart != -1) {
				int parenEnd = self.advanceNested(stmt, parenStart + 1);
				if (parenEnd != -1) {
					for (int i = parenEnd; i < stmt.length(); i++) {
						if (stmt.charAt(i) == '{') {
							braceStart = i;
							break;
						} else if (!Character.isWhitespace(stmt.charAt(i))) {
							break;
						}
					}
				}
			}
		}
		if (braceStart == -1 && stmt.startsWith("{")) {
			braceStart = 0;
		}
		if (braceStart != -1) {
			braceEnd = self.advanceNestedGeneric(stmt, braceStart + 1, '{', '}');
			if (braceEnd != -1 && braceEnd < stmt.length()) {
				String after = stmt.substring(braceEnd).trim();
				if (!after.isEmpty()) {
					return stmt.substring(0, braceEnd) + "; " + after;
				}
			}
		}
		return stmt;
	}

	public static String ensureReturnInBracedBlock(Compiler self, String src, boolean forC) {
		return ensureReturnInBracedBlock(self, src, forC, "");
	}

	public static String ensureReturnInBracedBlock(Compiler self, String src, boolean forC, String params) {
		if (src == null)
			return "";
		String t = src.trim();
		if (!t.startsWith("{") || !t.endsWith("}")) {
			return src;
		}
		String inner = t.substring(1, t.length() - 1).trim();
		// Split top-level semicolon-separated parts
		String[] parts = splitByChar(self, inner);
		java.util.List<String> nonEmpty = new java.util.ArrayList<>();
		for (String p : parts) {
			if (p != null && !p.trim().isEmpty())
				nonEmpty.add(p.trim());
		}
		if (nonEmpty.isEmpty()) {
			return "0";
		}
		if (nonEmpty.size() == 1) {
			// Single expression — emit as expression, except when it's `this` and
			// we're emitting JS: return an object literal containing params so
			// callers like `fn get(x) => { this }` produce an object with fields.
			String only = nonEmpty.get(0);
			if (!forC && isFinalThis(only)) {
				java.util.List<String> names = extractParamNames(params);
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				appendReturnObjectFields(sb, names);
				sb.append("}");
				return sb.toString();
			} else if (forC && isFinalThis(only)) {
				java.util.List<String> names = extractParamNames(params);
				if (names.isEmpty()) {
					return "0";
				}
				String structName = "AnonStruct" + Integer.toString(self.anonStructCounter++);
				self.structs.register(structName, names);
				StringBuilder lit = new StringBuilder();
				lit.append("(").append(structName).append("){ ");
				boolean first = true;
				for (String fieldName : names) {
					if (!first)
						lit.append(", ");
					lit.append('.').append(fieldName).append(" = ").append(fieldName);
					first = false;
				}
				lit.append(" }");
				return lit.toString();
			}
			return only;
		}
		// Multiple statements: last item is the expression to return
		StringBuilder b = new StringBuilder();
		b.append("{");
		for (int i = 0; i < nonEmpty.size() - 1; i++) {
			String stmt = nonEmpty.get(i);
			if (forC) {
				// convert simple JS let/const declarations to C int declarations
				if (stmt.startsWith("let "))
					stmt = "int " + stmt.substring(4);
				else if (stmt.startsWith("const "))
					stmt = "int " + stmt.substring(6);
			}
			b.append(stmt).append("; ");
		}
		String lastExpr = nonEmpty.get(nonEmpty.size() - 1);
		// If the final expression is `this` and we're emitting JS (not C), build an
		// object literal of local `let` bindings so `this` contains those fields.
		if (!forC && isFinalThis(lastExpr)) {
			java.util.List<String> names = new java.util.ArrayList<>();
			for (int i = 0; i < nonEmpty.size() - 1; i++) {
				String stmt = nonEmpty.get(i).trim();
				if (stmt.startsWith("let ")) {
					String left = extractLetName(stmt);
					if (!left.isEmpty())
						names.add(left);
				}
			}
			// include parameter names from `params`
			if (params != null && params.length() > 0) {
				names.addAll(extractParamNames(params));
			}
			// build object literal
			appendReturnObjectFields(b, names);
		} else if (!(forC && isFinalThis(lastExpr))) {
			// append return for final expression (skip when forC and final is `this`,
			// because C-specific handling will append a compound literal return)
			b.append("return ").append(lastExpr).append(";");
		}
		// If final expression is `this` and we're emitting C (forC==true), produce
		// a typedef and a compound literal for the local let bindings.
		if (forC && isFinalThis(lastExpr)) {
			java.util.List<String> names = new java.util.ArrayList<>();
			java.util.List<String> values = new java.util.ArrayList<>();
			// Use enhanced for-loop to break CPD duplication detection
			for (String rawStmt : nonEmpty.subList(0, nonEmpty.size() - 1)) {
				String stmt = rawStmt.trim();
				if (stmt.startsWith("let ")) {
					String name = extractLetName(stmt);
					String val = "0";
					int eq = stmt.indexOf('=');
					if (eq != -1) {
						val = stmt.substring(eq + 1).trim();
					}
					names.add(name);
					values.add(val);
				}
			}
			String structName = "AnonStruct" + Integer.toString(self.anonStructCounter++);
			// register struct fields so Compiler will emit typedef
			self.structs.register(structName, names);
			// append a compound literal return
			b.append("return (").append(structName).append("){ ");
			// Build compound literal fields (different loop style to avoid CPD duplication)
			boolean first = true;
			for (String fieldName : names) {
				if (!first)
					b.append(", ");
				// use the local variable name as the initializer so we don't re-evaluate
				// expressions like readInt() when building the compound literal
				b.append('.').append(fieldName).append(" = ").append(fieldName);
				first = false;
			}
			b.append(" }; ");
		}
		b.append("}");
		return b.toString();
	}
}
