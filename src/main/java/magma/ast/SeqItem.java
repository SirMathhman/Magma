package magma.ast;

/** Marker interface for items in a ParseResult sequence (either a VarDecl or a statement).
 *  Replaces the previous use of raw Object in `ParseResult.seq`.
 */
public sealed interface SeqItem permits VarDecl, StmtSeq {
}
