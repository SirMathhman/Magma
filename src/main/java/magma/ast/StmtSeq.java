package magma.ast;

/**
 * Wrapper for a statement string so it can implement SeqItem.
 */
public record StmtSeq(String stmt) implements SeqItem {

	@Override
	public String toString() {
		return stmt;
	}
}
