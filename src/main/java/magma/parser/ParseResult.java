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
		if (!(obj instanceof ParseResult that)) return false;
		if (!Objects.equals(this.decls, that.decls)) return false;
		if (!Objects.equals(this.stmts, that.stmts)) return false;
		if (!Objects.equals(this.last, that.last)) return false;
		return Objects.equals(this.seq, that.seq);
	}

	@Override
	public String toString() {
		return "ParseResult[" + "decls=" + decls + ", " + "stmts=" + stmts + ", " + "last=" + last + ", " + "seq=" + seq +
					 ']';
	}
}
