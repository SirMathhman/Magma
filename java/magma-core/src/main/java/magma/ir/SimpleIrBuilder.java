package magma.ir;

import magma.AstNode;
import magma.BinaryAst;
import magma.LiteralAst;

/** Convert AST to a tiny IR. */
public class SimpleIrBuilder {
    public IrNode build(AstNode ast) {
        if (ast instanceof LiteralAst) {
            return new IrLiteral(((LiteralAst) ast).value);
        }
        if (ast instanceof BinaryAst) {
            BinaryAst ba = (BinaryAst) ast;
            IrLiteral left = new IrLiteral(ba.left.value);
            IrLiteral right = new IrLiteral(ba.right.value);
            return new IrBinary(left, right);
        }
        return null;
    }
}
