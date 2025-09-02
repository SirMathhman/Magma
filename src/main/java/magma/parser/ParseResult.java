package magma.parser;

import java.util.List;
import java.util.Objects;
import magma.ast.VarDecl;
import magma.ast.SeqItem;

/**
 *
 */ // magma.Result of parsing statements: list of var decls and the final
// expression
public record ParseResult(List<VarDecl> decls, List<String> stmts, String last, List<SeqItem> seq) {
	/**
	 * @param stmts non-let statements in order
	 * @param seq   ordered sequence of VarDecl or String (stmts)
	 */
	public ParseResult {
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ParseResult(var decls1, var stmts1, var last1, var seq1))) return false;
		if (!Objects.equals(this.decls, decls1)) return false;
		if (!Objects.equals(this.stmts, stmts1)) return false;
		if (!Objects.equals(this.last, last1)) return false;
		return Objects.equals(this.seq, seq1);
	}

	@Override
	public String toString() {
		return "ParseResult[" + "decls=" + decls + ", " + "stmts=" + stmts + ", " + "last=" + last + ", " + "seq=" + seq +
					 ']';
	}
}
