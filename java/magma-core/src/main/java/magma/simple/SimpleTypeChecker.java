package magma.simple;

import magma.AstNode;
import magma.BinaryAst;
import magma.LiteralAst;
import magma.TypeChecker;

/** Minimal type checker: accepts LiteralAst and returns true if type is i32. */
public class SimpleTypeChecker implements TypeChecker {
    @Override
    public boolean check(AstNode ast) {
        if (ast instanceof LiteralAst) {
            LiteralAst la = (LiteralAst) ast;
            return "i32".equals(la.typeName);
        }
        if (ast instanceof BinaryAst) {
            BinaryAst ba = (BinaryAst) ast;
            return check(ba.left) && check(ba.right);
        }
        return false;
    }
}
