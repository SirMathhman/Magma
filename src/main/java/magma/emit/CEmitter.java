package magma.emit;

// C-specific helper class to reduce outer class method count
import magma.compiler.Compiler;
import magma.compiler.CompilerUtil;
import magma.parser.ParseResult;
import magma.ast.VarDecl;
import magma.ast.Structs;
import magma.parser.Parser;

public final class CEmitter {
	private CEmitter() {
	}

	public static String[] renderSeqPrefixC(Compiler self, ParseResult pr) {
		StringBuilder global = new StringBuilder();
		StringBuilder local = new StringBuilder();
		// We'll emit typedefs and enum defines after processing seq so any anonymous
		// structs registered while handling function bodies are included.
		for (Object o : pr.seq) {
			if (o instanceof VarDecl d) {
				if (d.type != null && d.type.contains("=>")) {
					// function-typed declaration
					String rhs = d.rhs == null ? "" : d.rhs.trim();
					if (rhs.isEmpty()) {
						// no initializer: emit pointer declaration without init
						local.append("int ").append(d.name).append("; ");
					} else if (rhs.contains("=>")) {
						int arrowIdx = rhs.indexOf("=>");
						int parenStart = rhs.lastIndexOf('(', arrowIdx);
						int parenEnd = parenStart == -1 ? -1 : self.advanceNestedGeneric(rhs, parenStart + 1, '(', ')');
						String params = parenStart != -1 && parenEnd != -1 ? rhs.substring(parenStart, parenEnd) : "()";
						String body = rhs.substring(arrowIdx + 2).trim();
						if (body.startsWith("{")) {
							body = Parser.ensureReturnInBracedBlock(self, body, true, params);
						} else {
							body = self.unwrapBraced(body);
						}
						// detect if body returns a compound literal like (AnonStructN){...}
						String detectedRetType = "int";
						// detect `return (StructName){ ... }` or direct compound literal `(StructName){ ... }`
						int retPos = body.indexOf("return (");
						if (retPos == -1 && body.startsWith("(")) {
							retPos = 0;
						}
						if (retPos != -1) {
							int ps = body.indexOf('(', retPos);
							int pe = ps == -1 ? -1 : self.advanceNestedGeneric(body, ps + 1, '(', ')');
							if (ps != -1 && pe != -1) {
								// find the token between '(' and ')' — that's the type name
								String maybeName = body.substring(ps + 1, pe - 1).trim();
								if (!maybeName.isEmpty())
									detectedRetType = maybeName;
							}
						}
						String cParams = CompilerUtil.paramsToC(params);
						String implName = d.name + "_impl";
						if (body.startsWith("{")) {
							global.append(detectedRetType).append(" ").append(implName).append(cParams).append(" ").append(body)
									.append("\n");
						} else {
							String implBody = self.convertLeadingIfToTernary(body);
							global.append(detectedRetType).append(" ")
									.append(implName)
									.append(cParams)
									.append(" { return ")
									.append(implBody)
									.append("; }\n");
						}
						String ptrSig = "(" + "*" + d.name + ")" + cParams;
						local.append(detectedRetType).append(" ").append(ptrSig).append(" = ").append(implName).append("; ");
					} else {
						String rhsOutF = self.unwrapBraced(rhs);
						local.append("int (*").append(d.name).append(")() = ").append(rhsOutF).append("; ");
					}
				} else {
					appendVarDeclToBuilder(self, local, d, true);
				}
			} else if (o instanceof String s) {
				handleFnStringForC(self, s, global, local);
			}
		}
		// Prepend typedefs and enum defines now that all structs/enums are registered
		StringBuilder typedefs = new StringBuilder();
	typedefs.append(self.structs.emitCTypeDefs());
	typedefs.append(self.emitEnumDefinesC());
		String finalGlobal = typedefs.toString() + global.toString();
		return new String[] { finalGlobal, local.toString() };
	}

	private static void handleFnStringForC(Compiler self, String s, StringBuilder global, StringBuilder local) {
		String trimmedS = s.trim();
		if (trimmedS.startsWith("fn ")) {
			String[] parts = Parser.parseFnDeclaration(self, trimmedS);
			if (parts != null) {
				String name = parts[0];
				String params = parts[1];
				String body = parts[3];
				String norm;
				if (body != null && body.trim().startsWith("{")) {
					norm = Parser.ensureReturnInBracedBlock(self, body, true, params);
				} else {
					norm = self.unwrapBraced(body);
				}
				String cParams = CompilerUtil.paramsToC(params);
				if (norm.startsWith("{")) {
					global.append("int ").append(name).append(cParams).append(" ").append(norm).append("\n");
				} else {
					String implBody = self.convertLeadingIfToTernary(norm);
					global.append("int ").append(name).append(cParams).append(" { return ").append(implBody).append("; }\n");
				}
			} else {
				local.append(s).append("; ");
			}
		} else {
			local.append(s).append("; ");
		}
	}

	public static void appendVarDeclToBuilder(Compiler self, StringBuilder b, VarDecl d, boolean forC) {
		if (forC) {
			if (d.rhs == null || d.rhs.isEmpty()) {
				b.append("int ").append(d.name).append("; ");
			} else {
				String rhsOut = self.convertLeadingIfToTernary(d.rhs);
				rhsOut = self.unwrapBraced(rhsOut);
				// replace enum member access like Name.Member with Name_Member for C
				rhsOut = self.replaceEnumDotAccess(rhsOut);
				String trimmed = rhsOut.trim();
				Structs.StructLiteral sl = self.structs.parseStructLiteral(trimmed);
				boolean emitted = false;
				if (sl != null) {
					String lit = self.structs.buildStructLiteral(sl.name(), sl.vals(), sl.fields(), true);
					b.append(sl.name()).append(" ").append(d.name).append(" = ").append(lit).append("; ");
					emitted = true;
				}
				if (!emitted) {
					b.append("int ").append(d.name).append(" = ").append(rhsOut).append("; ");
				}
			}
		} else {
			// fallback to JS behaviour if needed
			JsEmitter.appendVarDeclToBuilder(self, b, d, false);
		}
	}
}
