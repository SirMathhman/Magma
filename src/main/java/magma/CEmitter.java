package magma;

// C-specific helper class to reduce outer class method count
final class CEmitter {
	private CEmitter() {
	}

	public static String[] renderSeqPrefixC(Compiler self, ParseResult pr) {
		StringBuilder global = new StringBuilder();
		StringBuilder local = new StringBuilder();
		// Emit typedefs for any parsed structs so C code can use the short name
		global.append(self.structs.emitCTypeDefs());
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
							body = self.ensureReturnInBracedBlock(body);
						} else {
							body = self.unwrapBraced(body);
						}
						String cParams = CompilerUtil.paramsToC(params);
						String implName = d.name + "_impl";
						if (body.startsWith("{")) {
							global.append("int ").append(implName).append(cParams).append(" ").append(body).append("\n");
						} else {
							String implBody = self.convertLeadingIfToTernary(body);
							global.append("int ")
										.append(implName)
										.append(cParams)
										.append(" { return ")
										.append(implBody)
										.append("; }\n");
						}
						String ptrSig = "(" + "*" + d.name + ")" + cParams;
						local.append("int ").append(ptrSig).append(" = ").append(implName).append("; ");
					} else {
						String rhsOutF = self.unwrapBraced(rhs);
						local.append("int (*").append(d.name).append(")() = ").append(rhsOutF).append("; ");
					}
				} else {
					self.appendVarDeclToBuilder(local, d, true);
				}
			} else if (o instanceof String s) {
				handleFnStringForC(self, s, global, local);
			}
		}
		return new String[]{global.toString(), local.toString()};
	}

	private static void handleFnStringForC(Compiler self, String s, StringBuilder global, StringBuilder local) {
		String trimmedS = s.trim();
		if (trimmedS.startsWith("fn ")) {
			String[] parts = Parser.parseFnDeclaration(self, trimmedS);
			if (parts != null) {
				String name = parts[0];
				String params = parts[1];
				String body = parts[3];
				String norm = self.normalizeBodyForC(body);
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
}
