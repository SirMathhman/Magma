package magma.compiler.ast;

import java.util.List;

public class Program {
	private final List<Stmt> statements;

	public Program(List<Stmt> statements) {
		this.statements = statements;
	}

	public List<Stmt> getStatements() {
		return statements;
	}
}
