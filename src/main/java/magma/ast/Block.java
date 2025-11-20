package magma.ast;

import java.util.List;

public class Block implements Statement {
	private final List<Statement> statements;

	public Block(List<Statement> statements) {
		this.statements = statements;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitBlock(this);
	}
}

