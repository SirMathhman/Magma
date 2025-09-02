package magma.emit;

// C/JS emitter helpers moved to nested emitter classes to reduce outer
// Compiler method count.

import magma.ast.StmtSeq;
import magma.ast.Structs;
import magma.ast.VarDecl;
import magma.compiler.Compiler;
import magma.parser.ParseResult;
import magma.compiler.CompilerUtil;
import magma.parser.Parser;

import java.util.Collections;

public final class JsEmitter {
	private JsEmitter() {
	}

	private static String pointerBox(String inner) {
		return "({ get: () => " + inner + ", set: (v) => (" + inner + " = v) })";
	}

	public static String renderSeqPrefix(Compiler self, ParseResult pr) {
		var prefix = new StringBuilder();
		// emit enums as JS objects so references like State.Valid resolve
		for (var e : self.enums.entrySet()) {
			var name = e.getKey();
			var members = e.getValue();
			prefix.append("const ").append(name).append(" = { ");
			var first = true;
			for (var m : members) {
				if (!first)
					prefix.append(", ");
				prefix.append(m).append(": \"").append(m).append("\"");
				first = false;
			}
			prefix.append(" }; ");
		}
		for (var o : pr.seq()) {
			if (o instanceof VarDecl) {
				VarDecl d = (VarDecl) o;
				if (EmitterCommon.isFunctionTyped(d) || (d.rhs() != null && d.rhs().contains("=>"))) {
					var rhsOut = self.normalizeArrowRhsForJs(d.rhs());
					// If we have recorded impl methods for this function (class factory),
					rhsOut = attachMethodsIfRecorded(self, d, rhsOut);
					appendJsVarDecl(prefix, d, rhsOut);
				} else {
					appendVarDeclToBuilder(self, prefix, d);
				}
			} else if (o instanceof StmtSeq) {
				var stmt = ((StmtSeq) o).stmt();
				var trimmedS = stmt.trim();
				if (trimmedS.startsWith("fn ")) {
					var convertedFn = Parser.convertFnToJs(self, trimmedS, Collections.emptyList());
					prefix.append(convertedFn).append("; ");
				} else {
					// detect top-level pointer assignment like '*y = expr' and translate to
					// 'y.set(expr)' for JS
					var s = stmt;
					var eqPos = CompilerUtil.findTopLevelOp(s, "=");
					if (eqPos != -1) {
						var lhs = s.substring(0, eqPos).trim();
						if (lhs.startsWith("*")) {
							var target = lhs.substring(1).trim();
							var rhs = s.substring(eqPos + 1).trim();
							prefix.append(target).append(".set(").append(rewriteDerefInExpr(rhs)).append("); ");
						} else {
							prefix.append(rewriteDerefInExpr(stmt)).append("; ");
						}
					} else {
						prefix.append(rewriteDerefInExpr(stmt)).append("; ");
					}
				}
			}
		}
		return prefix.toString();
	}

	public static void appendJsVarDecl(StringBuilder b, VarDecl d, String rhsOut) {
		b.append(d.mut() ? "let " : "const ").append(d.name()).append(" = ").append(rhsOut).append("; ");
	}

	public static void appendVarDeclToBuilder(Compiler self, StringBuilder b, VarDecl d) {
		// mimic previous Compiler.appendVarDeclToBuilder behaviour for JS use
		if (d.rhs() == null || d.rhs().isEmpty()) {
			b.append("let ").append(d.name()).append("; ");
		} else {
			var rhsOut = d.rhs();
			if (rhsOut.contains("=>")) {
				rhsOut = self.normalizeArrowRhsForJs(rhsOut);
			} else {
				rhsOut = self.convertLeadingIfToTernary(rhsOut);
				rhsOut = self.unwrapBraced(rhsOut);
			}
			var trimmed = rhsOut.trim();
			var sl = self.structs.parseStructLiteral(trimmed);
			if (sl != null) {
				rhsOut = Structs.buildStructLiteral(sl.name(), sl.vals(), sl.fields(), false);
				// If there are impl methods for this struct, attach them to the object
				var methods = self.implMethods.get(sl.name());
				if (methods != null && !methods.isEmpty()) {
					rhsOut = buildObjIife(rhsOut, methods);
				}
			} else {
				// If this variable is a class factory (arrow function) and we have
				// recorded impl methods for its class name, wrap the factory body so
				// created objects get the methods attached at runtime.
				if (rhsOut.contains("=>")) {
					rhsOut = attachMethodsIfRecorded(self, d, rhsOut);
				}
			}
			// JS runtime pointer emulation:
			// &x -> ({ get: () => x, set: (v) => x = v })
			// *p -> p.get()
			var roTrim = rhsOut.trim();
			if (roTrim.startsWith("&") || roTrim.startsWith("*")) {
				var inner = roTrim.substring(1).trim();
				if (inner.startsWith("mut "))
					inner = inner.substring(4).trim();
				if (roTrim.startsWith("&"))
					rhsOut = pointerBox(inner);
				else
					rhsOut = inner + ".get()";
			}
			// rewrite deref occurrences inside initializer expressions
			rhsOut = rewriteDerefInExpr(rhsOut);
			appendJsVarDecl(b, d, rhsOut);
		}
	}

	public static String rewriteDerefInExpr(String src) {
		if (src == null || src.isEmpty())
			return src;
		// Replace occurrences of *ident with ident.get()
		return src.replaceAll("\\*([A-Za-z_][A-Za-z0-9_]*)", "$1.get()");
	}

	// Build JS assignment statements for methods onto `obj`.
	private static String buildAssignments(java.util.Map<String, String> methods) {
		var assignments = new StringBuilder();
		for (var e : methods.entrySet()) {
			assignments.append("obj.").append(e.getKey()).append(" = ").append(e.getValue()).append("; ");
		}
		return assignments.toString();
	}

	// If the provided rhs is an arrow function that returns an object literal
	// (e.g. "(...) => { ... return { ... }; ... }"), rewrite it so the returned
	// object is assigned to `obj`, the recorded methods attached, and `obj`
	// returned. Returns the modified rhs or the original if not applicable.
	private static String attachMethodsToArrow(String rhsOut, java.util.Map<String, String> methods) {
		var arrowIdx = rhsOut.indexOf("=>");
		if (arrowIdx == -1)
			return rhsOut;
		var params = rhsOut.substring(0, arrowIdx + 2);
		var body = rhsOut.substring(arrowIdx + 2).trim();
		var retIdx = body.indexOf("return");
		if (retIdx != -1 && body.substring(retIdx).trim().startsWith("return {")) {
			var semi = body.indexOf(";", retIdx);
			if (semi != -1) {
				var objExpr = body.substring(retIdx + "return ".length(), semi).trim();
				var newBody = body.substring(0, retIdx) + buildObjNonIife(objExpr, methods) + body.substring(semi + 1);
				return params + " " + newBody;
			}
		}
		return rhsOut;
	}

	private static String attachMethodsIfRecorded(Compiler self, VarDecl d, String rhsOut) {
		var methods = self.implMethods.get(d.name());
		if (methods != null && !methods.isEmpty()) {
			return attachMethodsToArrow(rhsOut, methods);
		}
		return rhsOut;
	}

	private static String buildObjIife(String objExpr, java.util.Map<String, String> methods) {
		return "(() => { const obj = " + objExpr + "; " + buildAssignments(methods) + "return obj; })()";
	}

	private static String buildObjNonIife(String objExpr, java.util.Map<String, String> methods) {
		return "const obj = " + objExpr + "; " + buildAssignments(methods) + "return obj; ";
	}
}
