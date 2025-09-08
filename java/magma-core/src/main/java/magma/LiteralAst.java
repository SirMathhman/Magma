package magma;

/** AST node representing an integer literal. */
public class LiteralAst extends AstNode {
    public final int value;
    public final String typeName;

    public LiteralAst(int value, String typeName) {
        this.value = value;
        this.typeName = typeName;
    }
}
