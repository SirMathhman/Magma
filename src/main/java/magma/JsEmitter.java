package magma;

// C/JS emitter helpers moved to nested emitter classes to reduce outer
// Compiler method count.
final class JsEmitter {
	private JsEmitter() {
	}

	public static String renderSeqPrefix(Compiler self, ParseResult pr) {
		StringBuilder prefix = new StringBuilder();
		for (Object o : pr.seq) {
			if (o instanceof VarDecl d) {
				if (d.rhs != null && d.rhs.contains("=>")) {
					String rhsOut = self.normalizeArrowRhsForJs(d.rhs);
					self.appendJsVarDecl(prefix, d, rhsOut);
				} else {
					self.appendVarDeclToBuilder(prefix, d, false);
				}
			} else if (o instanceof String stmt) {
				String trimmedS = stmt.trim();
				if (trimmedS.startsWith("fn ")) {
					String convertedFn = Parser.convertFnToJs(self, trimmedS);
					prefix.append(convertedFn).append("; ");
				} else {
					prefix.append(stmt).append("; ");
				}
			}
		}
		return prefix.toString();
	}
}
