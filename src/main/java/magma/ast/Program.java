package magma.ast;

import java.util.List;

public class Program implements Node {
	private final List<ImportStatement> imports;
	private final List<FunctionDefinition> functions;
	private final List<ExternFunctionDeclaration> externFunctions;
	private final List<Statement> statements;

	public Program(List<ImportStatement> imports, List<FunctionDefinition> functions, List<ExternFunctionDeclaration> externFunctions, List<Statement> statements) {
		this.imports = imports;
		this.functions = functions;
		this.externFunctions = externFunctions;
		this.statements = statements;
	}

	public List<ImportStatement> getImports() {
		return imports;
	}

	public List<FunctionDefinition> getFunctions() {
		return functions;
	}

	public List<ExternFunctionDeclaration> getExternFunctions() {
		return externFunctions;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitProgram(this);
	}
}

