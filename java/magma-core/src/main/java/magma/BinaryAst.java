package magma;

/** AST node representing a binary addition of two integer literals. */
public class BinaryAst extends AstNode {
    public final LiteralAst left;
    public final LiteralAst right;

    public BinaryAst(LiteralAst left, LiteralAst right) {
        this.left = left;
        this.right = right;
    }
}
