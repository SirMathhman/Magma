package magma.emit;

// C/JS emitter helpers moved to nested emitter classes to reduce outer
// Compiler method count.
import magma.ast.SeqItem;
import magma.ast.StmtSeq;
import magma.ast.StructLiteral;
import magma.ast.Structs;
import magma.compiler.Compiler;
import magma.parser.ParseResult;
import magma.ast.VarDecl;
import magma.parser.Parser;

import java.util.Collections;

public final class JsEmitter {
	private JsEmitter() {
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
				if (!first) prefix.append(", ");
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
					prefix.append(stmt).append("; ");
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
					var withMethods = new StringBuilder();
					withMethods.append("(() => { const obj = ").append(rhsOut).append("; ");
					for (var e : methods.entrySet()) {
						withMethods.append("obj.").append(e.getKey()).append(" = ").append(e.getValue()).append("; ");
					}
					withMethods.append("return obj; })()");
					rhsOut = withMethods.toString();
				}
			}
			appendJsVarDecl(b, d, rhsOut);
		}
	}
}
