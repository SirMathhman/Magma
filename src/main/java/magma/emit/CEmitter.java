package magma.emit;

// C-specific helper class to reduce outer class method count
import magma.ast.StmtSeq;
import magma.ast.Structs;
import magma.compiler.Compiler;
import magma.compiler.CompilerUtil;
import magma.parser.ParseResult;
import magma.ast.VarDecl;
import magma.parser.Parser;

public final class CEmitter {
	private CEmitter() {
	}

	private static void emitTopLevelVar(Compiler self, StringBuilder global, StringBuilder local, VarDecl d) {
		if (d.rhs() == null || d.rhs().isEmpty()) {
			global.append("int ").append(d.name()).append("; ");
			return;
		}
		var parsedStructName = new StringBuilder();
		var rhsOut = prepareRhs(self, d, parsedStructName);
		// If not a struct literal but RHS is a call to a function variable just declared with a
		// pointer type in local (pattern: "Type (*Name)(...)") use that Type as the var's type.
		if (parsedStructName.isEmpty() && rhsOut.matches("[A-Za-z_][A-Za-z0-9_]*\\(.*\\)")) {
			var fname = rhsOut.substring(0, rhsOut.indexOf('('));
			var sig = local.toString();
			var marker = "(*" + fname + ")";
			var sigIdx = sig.indexOf(marker);
			if (sigIdx != -1) {
				// scan backwards to previous space to get return type token
				int i = sigIdx - 2; // before '('
				while (i >= 0 && sig.charAt(i) == ' ') i--;
				int end = i + 1;
				while (i >= 0 && (Character.isLetterOrDigit(sig.charAt(i)) || sig.charAt(i) == '_')) i--;
				var ret = sig.substring(i + 1, end);
				if (!ret.isEmpty()) parsedStructName.append(ret);
			}
		}
			if (!parsedStructName.isEmpty()) {
				var lit = buildLiteralIfStruct(self, rhsOut);
				if (lit == null)
					lit = rhsOut;
				appendVarDeclWithInit(global, local, parsedStructName.toString(), d.name(), lit);
			} else {
				appendVarDeclWithInit(global, local, "int", d.name(), rhsOut);
			}
	}

	private static void appendVarDeclWithInit(StringBuilder global, StringBuilder local, String type, String name, String init) {
		global.append(type).append(" ").append(name).append("; ");
		local.append(name).append(" = ").append(init).append("; ");
	}

	private static String prepareRhs(Compiler self, VarDecl d, StringBuilder outStructName) {
		var rhsOut = self.convertLeadingIfToTernary(d.rhs());
		rhsOut = self.unwrapBraced(rhsOut);
		rhsOut = self.replaceEnumDotAccess(rhsOut);
		var trimmed = rhsOut.trim();
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
							global.append(detectedRetType).append(" ").append(implName).append(cParams).append(" ").append(body)
									.append("\n");
						} else {
							var implBody = self.convertLeadingIfToTernary(body);
							global.append(detectedRetType).append(" ")
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
					emitTopLevelVar(self, global, local, d);
				}
			} else if (o instanceof StmtSeq) {
				var stmt = ((StmtSeq) o).stmt();
				handleFnStringForC(self, stmt, global, local);
			}
		}
		// Prepend typedefs and enum defines now that all structs/enums are registered
		var typedefs = self.structs.emitCTypeDefs() + self.emitEnumDefinesC();
		var finalGlobal = typedefs + global;
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
