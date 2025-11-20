package magma.ast;

import java.util.List;

public class Program implements Node {
	private final List<ImportStatement> imports;
	private final List<Statement> statements;

	public Program(List<ImportStatement> imports, List<Statement> statements) {
		this.imports = imports;
		this.statements = statements;
	}

	public List<ImportStatement> getImports() {
		return imports;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitProgram(this);
	}
}

