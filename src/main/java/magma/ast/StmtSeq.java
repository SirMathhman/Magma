package magma.ast;

/** Wrapper for a statement string so it can implement SeqItem. */
public final class StmtSeq implements SeqItem {
  public final String stmt;

  public StmtSeq(String stmt) {
    this.stmt = stmt;
  }

  @Override
  public String toString() {
    return stmt;
  }
}
