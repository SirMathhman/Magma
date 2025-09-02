package magma;

import java.util.List;

// Small nested parser helper to reduce Compiler method count
final class Parser {
	private Parser() {
	}

	public static String normalizeArrowRhsForJs(Compiler self, String rhs) {
		String rhsOut = cleanArrowRhsForJs(self, rhs);
		rhsOut = self.convertLeadingIfToTernary(rhsOut);
		int arrowIdx = rhsOut.indexOf("=>");
		if (arrowIdx != -1) {
			String before = rhsOut.substring(0, arrowIdx + 2);
			String after = rhsOut.substring(arrowIdx + 2).trim();
			if (after.startsWith("{")) {
				after = Parser.ensureReturnInBracedBlock(self, after, false);
			} else {
				after = self.unwrapBraced(after);
			}
			rhsOut = before + " " + after;
		}
		return rhsOut;
	}

	public static String cleanArrowRhsForJs(Compiler self, String rhs) {
		int arrowIdx = rhs.indexOf("=>");
		if (arrowIdx == -1) return rhs;
		int parenStart = rhs.lastIndexOf('(', arrowIdx);
		if (parenStart == -1) return rhs;
		int parenEnd = self.advanceNestedGeneric(rhs, parenStart + 1, '(', ')');
		if (parenEnd == -1 || parenEnd > arrowIdx) return rhs;
		String params = rhs.substring(parenStart, parenEnd);
		String stripped = CompilerUtil.stripParamTypes(params);
		return rhs.substring(0, parenStart) + stripped + rhs.substring(parenEnd);
	}

	public static String[] parseFnDeclaration(Compiler self, String fnDecl) {
		if (!fnDecl.startsWith("fn ")) return null;
		String rest = fnDecl.substring(3).trim();
		int parenIdx = rest.indexOf('(');
		if (parenIdx == -1) return null;
		String name = rest.substring(0, parenIdx).trim();
		int parenEnd = self.advanceNested(rest, parenIdx + 1);
		if (parenEnd == -1) return null;
		String params = rest.substring(parenIdx, parenEnd).trim();
		int afterParams = parenEnd;
		while (afterParams < rest.length() && Character.isWhitespace(rest.charAt(afterParams))) afterParams++;
		String retType = "";
		int arrowIdx = rest.indexOf("=>", afterParams);
		if (arrowIdx == -1) return null;
		if (afterParams < rest.length() && rest.charAt(afterParams) == ':') {
			retType = rest.substring(afterParams + 1, arrowIdx).trim();
		}
		int bodyStart = arrowIdx + 2;
		int bs = bodyStart;
		while (bs < rest.length() && Character.isWhitespace(rest.charAt(bs))) bs++;
		String body;
		String remainder;
		if (bs < rest.length() && rest.charAt(bs) == '{') {
			int after = self.advanceNestedGeneric(rest, bs + 1, '{', '}');
			if (after == -1) return null;
			int bodyEndIndex = after;
			body = rest.substring(bs, bodyEndIndex).trim();
			remainder = rest.substring(bodyEndIndex).trim();
		} else {
			body = rest.substring(bodyStart).trim();
			remainder = "";
		}
		return new String[]{name, params, retType, body, remainder};
	}

	public static String convertFnToJs(Compiler self, String fnDecl) {
		String[] parts = parseFnDeclaration(self, fnDecl);
		if (parts == null) return fnDecl;
		String params = CompilerUtil.stripParamTypes(parts[1]);
		String body = parts[3];
		if (body != null && body.trim().startsWith("{")) {
			body = Parser.ensureReturnInBracedBlock(self, body, false);
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
			// Single expression — emit as expression
			return nonEmpty.get(0);
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
		// append return for final expression
		b.append("return ").append(nonEmpty.get(nonEmpty.size() - 1)).append(";");
		b.append("}");
		return b.toString();
	}
}
