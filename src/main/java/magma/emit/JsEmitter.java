package magma.emit;

// C/JS emitter helpers moved to nested emitter classes to reduce outer
// Compiler method count.
import magma.compiler.Compiler;
import magma.parser.ParseResult;
import magma.ast.VarDecl;
import magma.ast.Structs;
import magma.parser.Parser;
public final class JsEmitter {
	private JsEmitter() {
	}

	public static String renderSeqPrefix(Compiler self, ParseResult pr) {
		StringBuilder prefix = new StringBuilder();
		for (Object o : pr.seq) {
			if (o instanceof VarDecl d) {
				if (d.rhs != null && d.rhs.contains("=>")) {
					String rhsOut = self.normalizeArrowRhsForJs(d.rhs);
					appendJsVarDecl(prefix, d, rhsOut);
				} else {
					appendVarDeclToBuilder(self, prefix, d, false);
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

	public static void appendJsVarDecl(StringBuilder b, VarDecl d, String rhsOut) {
		b.append(d.mut ? "let " : "const ").append(d.name).append(" = ").append(rhsOut).append("; ");
	}

	public static void appendVarDeclToBuilder(Compiler self, StringBuilder b, VarDecl d, boolean forC) {
		// mimic previous Compiler.appendVarDeclToBuilder behaviour for JS use
		if (d.rhs == null || d.rhs.isEmpty()) {
			b.append("let ").append(d.name).append("; ");
		} else {
			String rhsOut = d.rhs;
			if (rhsOut.contains("=>")) {
				rhsOut = self.normalizeArrowRhsForJs(rhsOut);
			} else {
				rhsOut = self.convertLeadingIfToTernary(rhsOut);
				rhsOut = self.unwrapBraced(rhsOut);
			}
			String trimmed = rhsOut.trim();
			Structs.StructLiteral sl = self.structs.parseStructLiteral(trimmed);
			if (sl != null) {
				rhsOut = self.structs.buildStructLiteral(sl.name(), sl.vals(), sl.fields(), false);
			}
			appendJsVarDecl(b, d, rhsOut);
		}
	}
}
