package magma.emit;

// C-specific helper class to reduce outer class method count

import magma.ast.StmtSeq;
import magma.ast.Structs;
import magma.ast.VarDecl;
import magma.compiler.Compiler;
import magma.compiler.CompilerUtil;
import magma.parser.ParseResult;
import magma.parser.ParserUtils;
import magma.parser.Parser;

public final class CEmitter {
	private CEmitter() {
	}

	private static void emitTopLevelVar(Compiler self, StringBuilder global, StringBuilder local, VarDecl d,
			magma.parser.ParseResult pr) {
		if (d.rhs() == null || d.rhs().isEmpty()) {
			// default plain int when no initializer/type provided
			global.append("int ").append(d.name()).append("; ");
			return;
		}
		var parsedStructName = new StringBuilder();
		var rhsOut = prepareRhs(self, d, parsedStructName);
		// If no declared type but RHS is an address-of, infer a C pointer type
		if ((d.type() == null || d.type().isEmpty()) && rhsOut != null && rhsOut.trim().startsWith("&")) {
			appendVarDeclWithInit(global, local, "int *", d.name(), rhsOut);
			return;
		}
		// If the declared type is a pointer like '*I32' or '*Wrapper', emit a C pointer
		// type
		if (d.type() != null && d.type().startsWith("*")) {
			// allow '*mut I32' or '*I32'
			var baseRaw = d.type().substring(1).trim();
			var base = baseRaw.startsWith("mut ") ? baseRaw.substring(4).trim() : baseRaw;
			var cbase = "int";
			if (!parsedStructName.isEmpty()) {
				cbase = parsedStructName.toString();
			} else if ("I32".equals(base)) {
				cbase = "int";
			} else if ("Bool".equals(base)) {
				cbase = "int";
			} else {
				cbase = base;
			}
			appendVarDeclWithInit(global, local, cbase + " *", d.name(), rhsOut);
			return;
		}
		// If not a struct literal but RHS is a call to a function variable just
		// declared with a
		// pointer type in local (pattern: "Type (*Name)(...)") use that Type as the
		// var's type.
		if (parsedStructName.isEmpty() && rhsOut.matches("[A-Za-z_][A-Za-z0-9_]*\\(.*\\)")) {
			var fname = rhsOut.substring(0, rhsOut.indexOf('('));
			var sig = local.toString();
			var marker = "(*" + fname + ")";
			var sigIdx = sig.indexOf(marker);
			if (sigIdx != -1) {
				// scan backwards to previous space to get return type token
				int i = sigIdx - 2; // before '('
				while (i >= 0 && sig.charAt(i) == ' ')
					i--;
				int end = i + 1;
				while (i >= 0 && (Character.isLetterOrDigit(sig.charAt(i)) || sig.charAt(i) == '_'))
					i--;
				var ret = sig.substring(i + 1, end);
				if (!ret.isEmpty())
					parsedStructName.append(ret);
			}
		}
		if (!parsedStructName.isEmpty()) {
			var lit = buildLiteralIfStruct(self, rhsOut);
			if (lit == null)
				lit = rhsOut;
			appendVarDeclWithInit(global, local, parsedStructName.toString(), d.name(), lit);
		} else {
			// handle fixed-size array declaration like [I32; N]
			// support inferred array types when no declared type provided
			var declaredType = d.type();
			if ((declaredType == null || declaredType.isEmpty()) && rhsOut != null && rhsOut.trim().startsWith("[")) {
				var inferred = magma.compiler.Semantic.exprType(self, rhsOut.trim(), pr.decls());
				if (inferred != null && inferred.startsWith("["))
					declaredType = inferred;
			}

			if (declaredType != null && declaredType.startsWith("[") && rhsOut != null && rhsOut.trim().startsWith("[")) {
				var innerDecl = declaredType.substring(1, declaredType.length() - 1).trim();
				var semi = innerDecl.indexOf(';');
				if (semi != -1) {
					var elem = innerDecl.substring(0, semi).trim();
					var num = innerDecl.substring(semi + 1).trim();
					var ctype = "int";
					if ("I32".equals(elem))
						ctype = "int";
					else if ("Bool".equals(elem))
						ctype = "bool";
					// e.g. int name[num] = { ... };
					var vals = rhsOut.trim();
					vals = vals.substring(1, vals.length() - 1).trim();
					// declare array globally and assign elements at runtime in local
					global.append(ctype).append(" ").append(d.name()).append("[").append(num).append("]; ");
					// split vals by top-level commas
					var elems = ParserUtils.splitTopLevelMulti(vals, ',');
					for (int ei = 0; ei < elems.size(); ei++) {
						var ev = elems.get(ei).trim();
						if (ev.isEmpty())
							continue;
						local.append(d.name()).append("[").append(ei).append("] = ").append(ev).append("; ");
					}
					return;
				}
			}
			appendVarDeclWithInit(global, local, "int", d.name(), rhsOut);
		}
	}

	private static void appendVarDeclWithInit(StringBuilder global,
			StringBuilder local,
			String type,
			String name,
			String init) {
		global.append(type).append(" ").append(name).append("; ");
		local.append(name).append(" = ").append(init).append("; ");
	}

	private static String prepareRhs(Compiler self, VarDecl d, StringBuilder outStructName) {
		var rhsOut = self.convertLeadingIfToTernary(d.rhs());
		rhsOut = self.unwrapBraced(rhsOut);
		// normalize Rust-like '&mut x' to C '&x' for emitter
		if (rhsOut != null)
			rhsOut = rhsOut.replace("&mut ", "&");
		// replace enum dotted access
		rhsOut = self.replaceEnumDotAccess(rhsOut).orElse(rhsOut);
		var trimmed = rhsOut.trim();
		// array literal detection
		if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
			// leave as-is for caller to handle
			return rhsOut;
		}
		var sl = self.structs.parseStructLiteral(trimmed);
		if (sl != null) {
			outStructName.append(sl.name());
			// build literal will be called by caller
			return rhsOut;
		}
		return rhsOut;
	}

	private static String buildLiteralIfStruct(Compiler self, String rhsOut) {
		var sl = self.structs.parseStructLiteral(rhsOut.trim());
		if (sl != null) {
			return Structs.buildStructLiteral(sl.name(), sl.vals(), sl.fields(), true);
		}
		return null;
	}

	public static String[] renderSeqPrefixC(Compiler self, ParseResult pr) {
		var global = new StringBuilder();
		var local = new StringBuilder();
		// We'll emit typedefs and enum defines after processing seq so any anonymous
		// structs registered while handling function bodies are included.
		for (int i = 0; i < pr.seq().size(); i++) {
			var o = pr.seq().get(i);
			if (o instanceof VarDecl) {
				VarDecl d = (VarDecl) o;
				if (EmitterCommon.isFunctionTyped(d)) {
					// function-typed declaration
					var rhs = d.rhs() == null ? "" : d.rhs().trim();
					if (rhs.isEmpty()) {
						// no initializer: emit pointer declaration without init
						local.append("int ").append(d.name()).append("; ");
					} else if (rhs.contains("=>")) {
						var arrowIdx = rhs.indexOf("=>");
						var parenStart = rhs.lastIndexOf('(', arrowIdx);
						var parenEnd = parenStart == -1 ? -1 : self.advanceNestedGeneric(rhs, parenStart + 1, '(', ')');
						var params = parenStart != -1 && parenEnd != -1 ? rhs.substring(parenStart, parenEnd) : "()";
						var body = rhs.substring(arrowIdx + 2).trim();
						if (body.startsWith("{")) {
							body = Parser.ensureReturnInBracedBlock(self, body, true, params);
						} else {
							body = self.unwrapBraced(body);
						}
						// detect if body returns a compound literal like (AnonStructN){...}
						var detectedRetType = "int";
						// detect `return (StructName){ ... }` or direct compound literal `(StructName){
						// ... }`
						var retPos = body.indexOf("return (");
						if (retPos == -1 && body.startsWith("(")) {
							retPos = 0;
						}
						if (retPos != -1) {
							var ps = body.indexOf('(', retPos);
							var pe = ps == -1 ? -1 : self.advanceNestedGeneric(body, ps + 1, '(', ')');
							if (ps != -1 && pe != -1) {
								// find the token between '(' and ')' — that's the type name
								var maybeName = body.substring(ps + 1, pe - 1).trim();
								if (!maybeName.isEmpty())
									detectedRetType = maybeName;
							}
						}
						var cParams = CompilerUtil.paramsToC(params);
						var implName = d.name() + "_impl";
						if (body.startsWith("{")) {
							global.append(detectedRetType)
									.append(" ")
									.append(implName)
									.append(cParams)
									.append(" ")
									.append(body)
									.append("\n");
						} else {
							var implBody = self.convertLeadingIfToTernary(body);
							global.append(detectedRetType)
									.append(" ")
									.append(implName)
									.append(cParams)
									.append(" { return ")
									.append(implBody)
									.append("; }\n");
						}
						var ptrSig = "(" + "*" + d.name() + ")" + cParams;
						local.append(detectedRetType).append(" ").append(ptrSig).append(" = ").append(implName).append("; ");
					} else {
						var rhsOutF = self.unwrapBraced(rhs);
						local.append("int (*").append(d.name()).append(")() = ").append(rhsOutF).append("; ");
					}
				} else {
					emitTopLevelVar(self, global, local, d, pr);
				}
			} else if (o instanceof StmtSeq) {
				var stmt = ((StmtSeq) o).stmt();
				handleFnStringForC(self, stmt, global, local);
			}
		}
		// Prepend typedefs and enum defines now that all structs/enums are registered
		var typedefs = self.structs.emitCTypeDefs() + self.emitEnumDefinesC();
		var finalGlobal = typedefs + global.toString();
		// Post-process: if we emitted an array declaration like `int name[N];` in
		// global
		// and later emitted `name = { ... };` in local, merge into `int name[N] = { ...
		// };`
		for (var o : pr.seq()) {
			if (o instanceof VarDecl) {
				var d = (VarDecl) o;
				if (d.type() != null && d.type().startsWith("[")) {
					var name = d.name();
					var li = local.indexOf(name + " = {");
					if (li != -1) {
						String localStr = local.toString();
						int start = localStr.indexOf('{', li);
						int end = localStr.indexOf('}', start);
						if (start != -1 && end != -1) {
							String init = localStr.substring(start, end + 1);
							int declIdx = finalGlobal.indexOf(name + "];");
							if (declIdx != -1) {
								finalGlobal = finalGlobal.replaceFirst(name + "\\\\[[^\\\\]]+\\\\];",
										name + " = " + init + "; ");
								// remove assignment from local string
								int assignStart = li;
								int assignEnd = localStr.indexOf(';', assignStart);
								if (assignEnd != -1) {
									localStr = localStr.substring(0, assignStart) + localStr.substring(assignEnd + 1);
									local = new StringBuilder(localStr);
								}
							}
						}
					}
				}
			}
		}
		return new String[] { finalGlobal, local.toString() };
	}

	private static void handleFnStringForC(Compiler self, String s, StringBuilder global, StringBuilder local) {
		var trimmedS = s.trim();
		if (trimmedS.startsWith("fn ")) {
			var parts = Parser.parseFnDeclaration(self, trimmedS);
			if (parts != null) {
				var name = parts[0];
				var params = parts[1];
				var body = parts[3];
				String norm;
				if (body != null && body.trim().startsWith("{")) {
					norm = Parser.ensureReturnInBracedBlock(self, body, true, params);
				} else {
					norm = self.unwrapBraced(body);
				}
				var cParams = CompilerUtil.paramsToC(params);
				if (norm.startsWith("{")) {
					global.append("int ").append(name).append(cParams).append(" ").append(norm).append("\n");
				} else {
					var implBody = self.convertLeadingIfToTernary(norm);
					global.append("int ").append(name).append(cParams).append(" { return ").append(implBody).append("; }\n");
				}
			} else {
				local.append(s).append("; ");
			}
		} else {
			local.append(s).append("; ");
		}
	}

}
