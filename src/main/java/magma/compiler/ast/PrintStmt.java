package magma.compiler.ast;

public class PrintStmt implements Stmt {
	private final Expr expr;

	public PrintStmt(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}
}
