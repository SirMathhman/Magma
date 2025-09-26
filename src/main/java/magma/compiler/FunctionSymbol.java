package magma.compiler;

import java.util.List;
import java.util.Optional;

import magma.compiler.ast.Ast;

public final class FunctionSymbol {
	private final String name;
	private final List<Type> parameterTypes;
	private final List<String> parameterNames;
	private final Type returnType;
	private final boolean intrinsic;
	private final Optional<Ast.FunctionDecl> declaration;
	private final String cName;

	public FunctionSymbol(String name,
			List<Type> parameterTypes,
			List<String> parameterNames,
			Type returnType,
			boolean intrinsic,
			Optional<Ast.FunctionDecl> declaration,
			String cName) {
		this.name = name;
		this.parameterTypes = List.copyOf(parameterTypes);
		this.parameterNames = List.copyOf(parameterNames);
		this.returnType = returnType;
		this.intrinsic = intrinsic;
		this.declaration = declaration;
		this.cName = cName;
	}

	public String name() {
		return name;
	}

	public List<Type> parameterTypes() {
		return parameterTypes;
	}

	public List<String> parameterNames() {
		return parameterNames;
	}

	public Type returnType() {
		return returnType;
	}

	public boolean intrinsic() {
		return intrinsic;
	}

	public Optional<Ast.FunctionDecl> declaration() {
		return declaration;
	}

	public String cName() {
		return cName;
	}
}
