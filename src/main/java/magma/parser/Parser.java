package magma.parser;

import java.util.List;
import java.util.ArrayList;

import magma.ast.SeqItem;
import magma.ast.StmtSeq;
import magma.compiler.Compiler;
import magma.compiler.CompilerUtil;
import magma.compiler.Semantic;

// Small nested parser helper to reduce Compiler method count
public final class Parser {
	private Parser() {
	}




	// Parse a fn declaration and return [name, body] or null when not a fn.
	private static String[] parseFnNameAndBody(Compiler self, String stmt) {
		String[] fparts = parseFnDeclaration(self, stmt);
		if (fparts == null)
			return null;
		return new String[] { fparts[0], fparts[3] };
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
		return rest.substring(0, endIdx).trim();
	}

	private static void appendReturnObjectFields(StringBuilder b, List<String> names) {
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
	private static List<String> extractParamNames(String params) {
		List<String> names = new ArrayList<>();
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

	// Collect prefix local declarations (let/fn/const) from a list of statements.
	// Returns an array-like list: [filteredStatements, namesList, valuesList,
	// typesList]
	private static List<List<String>> collectAndFilterPrefix(Compiler self,
																																		 List<String> stmts) {
		List<String> filtered = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<String> values = new ArrayList<>();
		List<String> types = new ArrayList<>();
		for (String s : stmts) {
			String stmt = s.trim();
			if (stmt.startsWith("let ")) {
				String name = extractLetName(stmt);
				String val = "0";
				int eq = stmt.indexOf('=');
				if (eq != -1) val = stmt.substring(eq + 1).trim();
				names.add(name);
				values.add(val);
				types.add("int");
				filtered.add(stmt);
			} else if (stmt.startsWith("fn ")) {
				handleFnPrefix(self, stmt, names, values, types, filtered);
			} else if (stmt.startsWith("const ")) {
				String rest = stmt.substring(6).trim();
				int eq = rest.indexOf('=');
				if (eq != -1) {
					String cname = rest.substring(0, eq).trim();
					String val = rest.substring(eq + 1).trim();
					names.add(cname);
					values.add(val);
					types.add("int");
				}
				filtered.add(stmt);
			} else {
				filtered.add(stmt);
			}
		}
		List<List<String>> out = new ArrayList<>();
		out.add(filtered);
		out.add(names);
		out.add(values);
		out.add(types);
		return out;
	}

	// Helper extracted from collectAndFilterPrefix to handle `fn` statements so
	// duplication is reduced for CPD checks.
	private static void handleFnPrefix(Compiler self, String stmt, List<String> names,
			List<String> values, List<String> types, List<String> filtered) {
		String[] fparts = parseFnDeclaration(self, stmt);
		if (fparts != null) {
			String fname = fparts[0];
			String fbody = fparts[3];
			String implName = fname + "_impl_" + self.anonStructCounter++;
			String implC;
			if (fbody != null && fbody.trim().startsWith("{")) {
				implC = Parser.ensureReturnInBracedBlock(self, fbody, true, "");
				implC = "int " + implName + "() " + implC + "\n";
			} else {
				implC = "int " + implName + "() { return " + self.unwrapBraced(fbody) + "; }\n";
			}
			self.extraGlobalFunctions.add(implC);
			names.add(fname);
			values.add(implName);
			types.add("fn");
			// do not add original fn stmt to filtered (hoisted)
		} else {
			filtered.add(stmt);
		}
	}

	private static List<List<String>> collectPrefixForThis(Compiler self,
																																	 List<String> nonEmpty) {
		return collectAndFilterPrefix(self, nonEmpty.subList(0, nonEmpty.size() - 1));
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
			body = rest.substring(bs, after).trim();
			remainder = rest.substring(after).trim();
		} else {
			body = rest.substring(bodyStart).trim();
			remainder = "";
		}
		return new String[] { name, params, retType, body, remainder };
	}

	public static String convertFnToJs(Compiler self, String fnDecl, List<String> outerNames) {
		String[] parts = parseFnDeclaration(self, fnDecl);
		if (parts == null)
			return fnDecl;
		String params = CompilerUtil.stripParamTypes(parts[1]);
		String body = parts[3];
		if (body != null && body.trim().startsWith("{")) {
			// include outerNames and original params (with types) so `this` can include parameter names
			String mergedParams = parts[1];
			if (outerNames != null && !outerNames.isEmpty()) {
				// merge outer names into a fake param string so ensureReturnInBracedBlock
				// will include them in the constructed `this` object for JS
				StringBuilder sb = new StringBuilder(mergedParams == null ? "()" : mergedParams);
				// ensure we have parentheses
				if (!sb.toString().startsWith("(")) {
					sb.insert(0, '(');
					sb.append(')');
				}
				// append outer names as additional params without types
				String inner = sb.substring(1, sb.length() - 1).trim();
				if (!inner.isEmpty())
					inner = inner + ", ";
				StringBuilder merged = new StringBuilder();
				merged.append('(').append(inner);
				for (int i = 0; i < outerNames.size(); i++) {
					if (i > 0)
						merged.append(", ");
					merged.append(outerNames.get(i));
				}
				merged.append(')');
				mergedParams = merged.toString();
			}
				body = Parser.ensureReturnInBracedBlock(self, body, false, mergedParams);
				// If the body references `this` we must emit a regular function so `this`
				// is dynamic at call-time (arrow functions capture lexical this).
				if (body != null && body.contains("this")) {
					return "const " + parts[0] + " = function" + params + " " + body;
				}
		} else {
			body = self.unwrapBraced(body);
				if (body != null && body.contains("this")) {
					return "const " + parts[0] + " = function" + params + " { return " + body + "; }";
				}
		}
		return "const " + parts[0] + " = " + params + " => " + body;
	}

	private static String tryInlineFnCall(Compiler self, List<String> nonEmpty, int idx, String lastExpr,
			String params) {
		String stmt = nonEmpty.get(idx).trim();
		if (!stmt.startsWith("fn "))
			return null;
		String[] pn = parseFnNameAndBody(self, stmt);
		if (pn == null)
			return null;
		String fname = pn[0];
		String fbody = pn[1];
		if (!lastExpr.trim().equals(fname + "()"))
			return null;
		String res;
		if (fbody != null && fbody.trim().startsWith("{")) {
			res = Parser.ensureReturnInBracedBlock(self, fbody, true, params);
		} else {
			res = self.unwrapBraced(fbody);
		}
		nonEmpty.remove(idx);
		return res;
	}

	// Return true if the provided function body (either braced or expression)
	// returns `this` as its final expression.
	private static boolean fnReturnsThis(Compiler self, String fbody) {
		if (fbody == null)
			return false;
		String ftrim = fbody.trim();
		if (ftrim.startsWith("{")) {
			List<String> nonEmpty = ParserUtils.splitNonEmptyFromBraced(ftrim);
			if (nonEmpty.isEmpty())
				return false;
			String last = nonEmpty.getLast();
			return isFinalThis(last);
		} else {
			String un = self.unwrapBraced(fbody).trim();
			// strip trailing semicolon if present (e.g. "this;")
			if (un.endsWith(";"))
				un = un.substring(0, un.length() - 1).trim();
			return isFinalThis(un);
		}
	}

	public static String handleStatementProcessing(Compiler self, String p, List<String> stmts, List<SeqItem> seq) {
		String processed = processControlStructures(self, p);
		if (!processed.equals(p)) {
			String[] controlParts = splitByChar(processed);
			String lastPart = p;
			for (String part : controlParts) {
				part = part.trim();
				if (!part.isEmpty()) {
					stmts.add(part);
					seq.add(new StmtSeq(part));
					lastPart = part;
				}
			}
			return lastPart;
		} else {
			stmts.add(p);
			seq.add(new StmtSeq(p));
			return p;
		}
	}

	public static String[] splitByChar(String s) {
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
		// Split top-level semicolon-separated parts from the braced block
		List<String> nonEmpty = ParserUtils.splitNonEmptyFromBraced(t);
		if (nonEmpty.isEmpty()) {
			return "0";
		}
		if (nonEmpty.size() == 1) {
			// Single expression — emit as expression, except when it's `this` and
			// we're emitting JS: return an object literal containing params so
			// callers like `fn get(x) => { this }` produce an object with fields.
			String only = nonEmpty.getFirst();
			if (!forC && isFinalThis(only)) {
				List<String> names = extractParamNames(params);
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				appendReturnObjectFields(sb, names);
				sb.append("}");
				return sb.toString();
			} else if (forC && isFinalThis(only)) {
				List<String> names = extractParamNames(params);
				if (names.isEmpty()) {
					return "0";
				}
				String structName = "AnonStruct" + self.anonStructCounter++;
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
		String lastExpr = nonEmpty.getLast();
		// For C output, if there's a nested `fn name() => expr;` and the final
		// expression is `name()`, inline the function body and remove the
		// nested fn statement (C doesn't support nested fn definitions).
		if (forC) {
			for (int idx = 0; idx < nonEmpty.size() - 1; ++idx) {
				String inlined = tryInlineFnCall(self, nonEmpty, idx, lastExpr, params);
				if (inlined != null) {
					lastExpr = inlined;
					break;
				}
			}
		}
		// For JS output, if a nested fn is called as the final expression and
		// that nested fn's body returns `this`, treat the final expression as
		// `this` (so the JS emitter will build an object literal). This mirrors
		// the C inlining behaviour but keeps JS semantics.
		if (!forC) {
			for (int idx = 0; idx < nonEmpty.size() - 1; ++idx) {
				String stmt = nonEmpty.get(idx).trim();
				if (stmt.startsWith("fn ")) {
					String[] pn = parseFnNameAndBody(self, stmt);
					if (pn != null) {
						String fname = pn[0];
						String fbody = pn[1];
						if (lastExpr.trim().equals(fname + "()") && fnReturnsThis(self, fbody)) {
							lastExpr = "this";
							nonEmpty.remove(idx);
							break;
						}
					}
				}
			}
		}
		// Pre-scan for C `this` compound literal: hoist nested fn/const/let and
		// remove fn statements from the emitted local body (they are hoisted).
		List<String> hoistNames = null;
		List<String> hoistValues = null;
		List<String> hoistTypes = null;
		if (forC && isFinalThis(lastExpr)) {
			List<List<String>> collect = collectPrefixForThis(self, nonEmpty);
			List<String> filtered = collect.get(0);
			List<String> names = collect.get(1);
			List<String> values = collect.get(2);
			List<String> types = collect.get(3);
			// adopt collected lists as hoisted lists (fn impls already added by helper)
			hoistNames = names;
			hoistValues = values;
			hoistTypes = types;
			List<String> rebuilt = new ArrayList<>(filtered);
			rebuilt.add(nonEmpty.getLast());
			nonEmpty = rebuilt;
		}

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
				// when emitting JS, convert nested `fn` declarations to JS consts
				if (!forC && stmt.trim().startsWith("fn ")) {
					// collect outer-local names up to this statement so inner `this`
					// can include them when converting to an arrow function
					List<List<String>> col = collectAndFilterPrefix(self, nonEmpty.subList(0, i));
					List<String> outerNames = col.get(1);
					stmt = convertFnToJs(self, stmt.trim(), outerNames);
				}
			b.append(stmt).append("; ");
		}
		// If the final expression is `this` and we're emitting JS (not C), build an
		// object literal of local `let` bindings so `this` contains those fields.
		if (!forC && isFinalThis(lastExpr)) {
			// collect prefix declarations to extract declared names
			List<List<String>> collected = collectPrefixForThis(self, nonEmpty);
			List<String> names = collected.get(1);
			// include parameter names from `params`
			if (params != null && !params.isEmpty()) {
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
			List<String> names;
			List<String> values;
			List<String> types;
			if (hoistNames != null) {
				names = hoistNames;
				values = hoistValues;
				types = hoistTypes;
			} else {
				// collect prefix declarations to build names/values/types
				List<List<String>> collected = collectAndFilterPrefix(self,
																																				nonEmpty.subList(0, nonEmpty.size() - 1));
				names = collected.get(1);
				values = collected.get(2);
				types = collected.get(3);
				// keep nonEmpty unchanged here; filtered is unused in this branch
			}
			String structName = "AnonStruct" + self.anonStructCounter++;
			// register struct fields so Compiler will emit typedef (with types)
			self.structs.registerWithTypes(structName, names, types);
			// append a compound literal return
			b.append("return (").append(structName).append("){ ");
			// Build compound literal fields (different loop style to avoid CPD duplication)
			boolean first = true;
			for (int i = 0; i < names.size(); i++) {
				String fieldName = names.get(i);
				String initVal = (i < values.size() ? values.get(i) : fieldName);
				if (!first)
					b.append(", ");
				// use the local variable name or hoisted impl name as the initializer
				b.append('.').append(fieldName).append(" = ");
				if (i < types.size() && "fn".equals(types.get(i))) {
					b.append(initVal);
				} else {
					b.append(fieldName);
				}
				first = false;
			}
			b.append(" }; ");
		}
		b.append("}");
		return b.toString();
	}
}
