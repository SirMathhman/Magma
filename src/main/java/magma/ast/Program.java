package magma.ast;

import java.util.List;

public class Program implements Node {
	private final List<ImportStatement> imports;
	private final List<TypeDefinition> typeDefinitions;
	private final List<TraitDefinition> traits;
	private final List<TraitImplementation> traitImplementations;
	private final List<FunctionDefinition> functions;
	private final List<ExternFunctionDeclaration> externFunctions;
	private final List<Statement> statements;

	public Program(List<ImportStatement> imports, List<TypeDefinition> typeDefinitions, List<TraitDefinition> traits, List<TraitImplementation> traitImplementations, List<FunctionDefinition> functions, List<ExternFunctionDeclaration> externFunctions, List<Statement> statements) {
		this.imports = imports;
		this.typeDefinitions = typeDefinitions;
		this.traits = traits;
		this.traitImplementations = traitImplementations;
		this.functions = functions;
		this.externFunctions = externFunctions;
		this.statements = statements;
	}

	public List<ImportStatement> getImports() {
		return imports;
	}

	public List<TypeDefinition> getTypeDefinitions() {
		return typeDefinitions;
	}

	public List<TraitDefinition> getTraits() {
		return traits;
	}

	public List<TraitImplementation> getTraitImplementations() {
		return traitImplementations;
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

