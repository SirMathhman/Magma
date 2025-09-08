package magma;

/** Convert AST to a tiny IR. */
public class SimpleIrBuilder {
    public IrNode build(AstNode ast) {
        if (ast instanceof LiteralAst) {
            return new IrLiteral(((LiteralAst) ast).value);
        }
        return null;
    }
}
