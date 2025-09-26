package magma.compile;

import java.util.List;
import java.util.Option;

import magma.compile.ast.Ast;

public record FunctionSymbol(String name, List<Type> parameterTypes, List<String> parameterNames, Type returnType,
														 boolean intrinsic, Option<Ast.FunctionDecl> declaration, String cName) {
	public FunctionSymbol(String name,
												List<Type> parameterTypes,
												List<String> parameterNames,
												Type returnType,
												boolean intrinsic,
												Option<Ast.FunctionDecl> declaration,
												String cName) {
		this.name = name;
		this.parameterTypes = List.copyOf(parameterTypes);
		this.parameterNames = List.copyOf(parameterNames);
		this.returnType = returnType;
		this.intrinsic = intrinsic;
		this.declaration = declaration;
		this.cName = cName;
	}


}
