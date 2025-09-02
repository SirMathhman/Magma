package magma;

import java.util.List;
import java.util.Objects;

/**
 *
 */ // magma.Result of parsing statements: list of var decls and the final
// expression
final class ParseResult {
	public final List<VarDecl> decls;
	public final List<String> stmts;
	public final String last;
	public final List<Object> seq;

	/**
	 * @param stmts non-let statements in order
	 * @param seq   ordered sequence of VarDecl or String (stmts)
	 */
	ParseResult(List<VarDecl> decls, List<String> stmts, String last, List<Object> seq) {
		this.decls = decls;
		this.stmts = stmts;
		this.last = last;
		this.seq = seq;
	}

	public List<VarDecl> decls() {return decls;}

	public List<String> stmts() {return stmts;}

	public String last() {return last;}

	public List<Object> seq() {return seq;}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ParseResult) obj;
		return Objects.equals(this.decls, that.decls) && Objects.equals(this.stmts, that.stmts) &&
					 Objects.equals(this.last, that.last) && Objects.equals(this.seq, that.seq);
	}

	@Override
	public int hashCode() {
		return Objects.hash(decls, stmts, last, seq);
	}

	@Override
	public String toString() {
		return "ParseResult[" + "decls=" + decls + ", " + "stmts=" + stmts + ", " + "last=" + last + ", " + "seq=" + seq +
					 ']';
	}
}
