package magma.compiler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import magma.compiler.ast.Ast;
import magma.Option;

public final class SemanticAnalyzer {
	public record AnalysisResult(Ast.Program program,
			IdentityHashMap<Ast.Expression, Type> expressionTypes,
			IdentityHashMap<Ast.IdentifierExpression, VariableSymbol> identifierBindings,
			IdentityHashMap<Ast.LetStatement, VariableSymbol> letBindings,
			LinkedHashMap<String, VariableSymbol> globalVariables,
			LinkedHashMap<String, FunctionSymbol> functions,
			Type finalType,
			Optional<Ast.Expression> finalExpression,
			List<String> errors) {
	}

	private static final class State {
		final Ast.Program program;
		final IdentityHashMap<Ast.Expression, Type> expressionTypes = new IdentityHashMap<>();
		final IdentityHashMap<Ast.IdentifierExpression, VariableSymbol> identifierBindings = new IdentityHashMap<>();
		final IdentityHashMap<Ast.LetStatement, VariableSymbol> letBindings = new IdentityHashMap<>();
		final LinkedHashMap<String, VariableSymbol> globalVariables = new LinkedHashMap<>();
		final LinkedHashMap<String, FunctionSymbol> functions = new LinkedHashMap<>();
		final Deque<Map<String, VariableSymbol>> scopes = new ArrayDeque<>();
		final List<String> errors = new ArrayList<>();
		Type currentReturnType;
		boolean insideFunction;
		boolean functionSawReturn;
		int globalIndex = 0;

		State(Ast.Program program) {
			this.program = program;
		}

		void pushScope() {
			scopes.push(new LinkedHashMap<>());
		}

		void popScope() {
			scopes.pop();
		}

		Map<String, VariableSymbol> currentScope() {
			return scopes.peek();
		}

		boolean inGlobalScope() {
			return scopes.size() == 1;
		}
		}

		public AnalysisResult analyze(Ast.Program program) {
			State state = new State(program);
			registerIntrinsics(state);
			registerFunctionSignatures(state);

			state.pushScope(); // global scope
			for (Ast.Statement statement : program.statements()) {
				analyzeStatement(statement, state);
			}
			Type finalType = Type.I32;
			Optional<Ast.Expression> finalExpr = program.finalExpression();
			if (finalExpr.isPresent()) {
				finalType = analyzeExpression(finalExpr.get(), state);
				if (finalType == Type.VOID) {
					state.errors.add("Final expression cannot be void");
					finalType = Type.I32;
				}
			}

			analyzeFunctionBodies(state);

			state.popScope();

			return new AnalysisResult(program,
					state.expressionTypes,
					state.identifierBindings,
					state.letBindings,
					state.globalVariables,
					state.functions,
					finalType,
					finalExpr,
					state.errors);
		}

	private void registerIntrinsics(State state) {
		for (Ast.IntrinsicDecl decl : state.program.intrinsics()) {
			Type returnType = resolveTypeRef(decl.returnType(), true, state);
			List<Type> params = new ArrayList<>();
			List<String> paramNames = new ArrayList<>();
			for (int i = 0; i < decl.parameterTypes().size(); i++) {
				Ast.TypeRef typeRef = decl.parameterTypes().get(i);
				Type type = resolveTypeRef(typeRef, false, state);
				params.add(type);
				paramNames.add("arg" + i);
			}
			String cName = intrinsicCName(decl.name());
			FunctionSymbol symbol = new FunctionSymbol(decl.name(), params, paramNames, returnType, true, Optional.empty(), cName);
			if (state.functions.containsKey(decl.name())) {
				state.errors.add("Duplicate intrinsic declaration for function '" + decl.name() + "'");
			} else {
				state.functions.put(decl.name(), symbol);
			}
		}
	}

	private void registerFunctionSignatures(State state) {
		for (Ast.FunctionDecl function : state.program.functions()) {
			if (state.functions.containsKey(function.name())) {
				state.errors.add("Function '" + function.name() + "' is already defined");
				continue;
			}
			List<Type> parameterTypes = new ArrayList<>();
			List<String> parameterNames = new ArrayList<>();
			Map<String, Boolean> seenParams = new LinkedHashMap<>();
			for (Ast.Parameter parameter : function.parameters()) {
				if (seenParams.containsKey(parameter.name())) {
					state.errors.add("Duplicate parameter name '" + parameter.name() + "' in function '" + function.name() + "'");
				} else {
					seenParams.put(parameter.name(), Boolean.TRUE);
				}
				Type paramType = resolveTypeRef(parameter.type(), false, state);
				parameterTypes.add(paramType);
				parameterNames.add(parameter.name());
			}
			Type returnType = function.returnType().map(typeRef -> resolveTypeRef(typeRef, true, state))
					.orElse(Type.I32);
			FunctionSymbol symbol = new FunctionSymbol(function.name(), parameterTypes, parameterNames, returnType, false,
					Optional.of(function), function.name());
			state.functions.put(function.name(), symbol);
		}
	}

	private void analyzeFunctionBodies(State state) {
		for (FunctionSymbol symbol : state.functions.values()) {
			if (symbol.declaration().isPresent()) {
				analyzeFunction(symbol, state);
			}
		}
	}

	private void analyzeFunction(FunctionSymbol symbol, State state) {
		Optional<Ast.FunctionDecl> declarationOpt = symbol.declaration();
		if (declarationOpt.isEmpty()) {
			return;
		}
		Ast.FunctionDecl declaration = declarationOpt.get();
		state.pushScope();
		state.insideFunction = true;
		Type previousReturnType = state.currentReturnType;
		boolean previousSawReturn = state.functionSawReturn;
		state.currentReturnType = symbol.returnType();
		state.functionSawReturn = false;
		for (int i = 0; i < symbol.parameterNames().size(); i++) {
			String paramName = symbol.parameterNames().get(i);
			Type paramType = symbol.parameterTypes().get(i);
			VariableSymbol variableSymbol = new VariableSymbol(paramName, paramType, false, false, paramName);
			state.currentScope().put(paramName, variableSymbol);
		}
		analyzeBlock(declaration.body(), state);
		Optional<Ast.Expression> finalExpr = declaration.body().result();
		if (symbol.returnType() == Type.VOID) {
			if (finalExpr.isPresent()) {
				state.errors.add("Void function '" + symbol.name() + "' cannot have a final expression");
			}
		} else {
			if (finalExpr.isPresent()) {
				Type resultType = state.expressionTypes.getOrDefault(finalExpr.get(), Type.I32);
				if (resultType != symbol.returnType()) {
					state.errors.add("Function '" + symbol.name() + "' final expression has type " + resultType
							+ " but expected " + symbol.returnType());
				}
			} else if (!state.functionSawReturn) {
				state.errors.add("Function '" + symbol.name() + "' must return a value");
			}
		}
		state.currentReturnType = previousReturnType;
		state.functionSawReturn = previousSawReturn;
		state.insideFunction = false;
		state.popScope();
	}

	private void analyzeBlock(Ast.Block block, State state) {
		analyzeBlockContents(block, state, false);
	}

	private void analyzeStatement(Ast.Statement statement, State state) {
		if (statement instanceof Ast.LetStatement let) {
			analyzeLet(let, state);
		} else if (statement instanceof Ast.AssignmentStatement assignment) {
			analyzeAssignment(assignment, state);
		} else if (statement instanceof Ast.IncrementStatement inc) {
			analyzeIncrement(inc, state);
		} else if (statement instanceof Ast.WhileStatement whileStmt) {
			analyzeWhile(whileStmt, state);
		} else if (statement instanceof Ast.BlockStatement blockStatement) {
			analyzeBlock(blockStatement.block(), state);
		} else if (statement instanceof Ast.ExpressionStatement expressionStatement) {
			analyzeExpression(expressionStatement.expression(), state);
		} else if (statement instanceof Ast.ReturnStatement returnStatement) {
			analyzeReturn(returnStatement, state);
		}
	}

	private void analyzeLet(Ast.LetStatement let, State state) {
		if (state.currentScope().containsKey(let.name())) {
			state.errors.add("Variable '" + let.name() + "' is already defined in this scope");
		}
		Type initializerType = analyzeExpression(let.initializer(), state);
		Type declaredType = let.typeAnnotation().map(typeRef -> resolveTypeRef(typeRef, false, state))
				.orElse(initializerType);
		if (declaredType == Type.VOID) {
			state.errors.add("Variable '" + let.name() + "' cannot have type Void");
			declaredType = Type.I32;
		}
		if (initializerType != declaredType) {
			state.errors.add("Type mismatch: cannot assign " + initializerType + " to variable '" + let.name() + "' of type "
					+ declaredType);
		}
		boolean global = state.inGlobalScope();
		String cName = global ? generateGlobalName(let.name(), state) : let.name();
		VariableSymbol symbol = new VariableSymbol(let.name(), declaredType, let.mutable(), global, cName);
		state.currentScope().put(let.name(), symbol);
		state.letBindings.put(let, symbol);
		if (global) {
			if (state.globalVariables.containsKey(let.name())) {
				state.errors.add("Global variable '" + let.name() + "' is already defined");
			} else {
				state.globalVariables.put(let.name(), symbol);
			}
		}
	}

	private void analyzeAssignment(Ast.AssignmentStatement assignment, State state) {
		Optional<VariableSymbol> symbolOpt = variableOrReport(assignment.name(), state,
				"Unknown variable '" + assignment.name() + "'");
		if (symbolOpt.isEmpty()) {
			analyzeExpression(assignment.expression(), state);
			return;
		}
		VariableSymbol symbol = symbolOpt.get();
		if (!symbol.mutable()) {
			state.errors.add("Cannot assign to immutable variable '" + assignment.name() + "'");
		}
		Type expressionType = analyzeExpression(assignment.expression(), state);
		switch (assignment.op()) {
			case ASSIGN -> {
				if (expressionType != symbol.type()) {
					state.errors.add("Type mismatch assigning to '" + assignment.name() + "'");
				}
			}
			case PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, SLASH_ASSIGN -> {
				if (!symbol.type().isNumeric() || !expressionType.isNumeric()) {
					state.errors.add("Compound assignment requires numeric types for '" + assignment.name() + "'");
				}
			}
		}
	}

	private void analyzeIncrement(Ast.IncrementStatement inc, State state) {
		Optional<VariableSymbol> symbolOpt = variableOrReport(inc.name(), state,
				"Unknown variable '" + inc.name() + "'");
		if (symbolOpt.isEmpty()) {
			return;
		}
		VariableSymbol symbol = symbolOpt.get();
		if (!symbol.mutable()) {
			state.errors.add("Cannot increment immutable variable '" + inc.name() + "'");
		}
		if (!symbol.type().isNumeric()) {
			state.errors.add("Increment requires numeric variable for '" + inc.name() + "'");
		}
	}

	private void analyzeWhile(Ast.WhileStatement whileStmt, State state) {
		Type condType = analyzeExpression(whileStmt.condition(), state);
		if (!condType.isBoolean()) {
			state.errors.add("While condition must be boolean");
		}
		analyzeStatement(whileStmt.body(), state);
	}

	private void analyzeReturn(Ast.ReturnStatement returnStatement, State state) {
		if (!state.insideFunction) {
			state.errors.add("Return statement not allowed at top level");
			return;
		}
		state.functionSawReturn = true;
		if (returnStatement.expression().isPresent()) {
			Type exprType = analyzeExpression(returnStatement.expression().get(), state);
			if (state.currentReturnType == Type.VOID) {
				state.errors.add("Void function cannot return a value");
			} else if (exprType != state.currentReturnType) {
				state.errors.add("Return type mismatch: expected " + state.currentReturnType + " but found " + exprType);
			}
		} else {
			if (state.currentReturnType != Type.VOID) {
				state.errors.add("Non-void function must return a value");
			}
		}
	}

	private Type analyzeExpression(Ast.Expression expression, State state) {
		Type type;
		if (expression instanceof Ast.LiteralIntExpression) {
			type = Type.I32;
		} else if (expression instanceof Ast.LiteralBoolExpression) {
			type = Type.BOOL;
		} else if (expression instanceof Ast.IdentifierExpression identifier) {
			Optional<VariableSymbol> symbolOpt = variableOrReport(identifier.name(), state,
					"Unknown variable '" + identifier.name() + "'");
			if (symbolOpt.isEmpty()) {
				type = Type.I32;
			} else {
				VariableSymbol symbol = symbolOpt.get();
				state.identifierBindings.put(identifier, symbol);
				type = symbol.type();
			}
		} else if (expression instanceof Ast.UnaryExpression unary) {
			Type operand = analyzeExpression(unary.expression(), state);
			if (!operand.isNumeric()) {
				state.errors.add("Unary '-' requires numeric operand");
			}
			type = Type.I32;
		} else if (expression instanceof Ast.BinaryExpression binary) {
			Type left = analyzeExpression(binary.left(), state);
			Type right = analyzeExpression(binary.right(), state);
			type = analyzeBinary(binary, left, right, state);
		} else if (expression instanceof Ast.CallExpression call) {
			type = analyzeCall(call, state);
		} else if (expression instanceof Ast.IfExpression ifExpr) {
			Type condType = analyzeExpression(ifExpr.condition(), state);
			if (!condType.isBoolean()) {
				state.errors.add("If condition must be boolean");
			}
			Type thenType = analyzeExpression(ifExpr.thenBranch(), state);
			Type elseType = analyzeExpression(ifExpr.elseBranch(), state);
			if (thenType != elseType) {
				state.errors.add("If branches must have the same type");
			}
			type = thenType;
		} else if (expression instanceof Ast.BlockExpression blockExpression) {
			type = analyzeBlockExpression(blockExpression.block(), state);
		} else {
			type = Type.I32;
		}
		state.expressionTypes.put(expression, type);
		return type;
	}

	private Type analyzeBlockExpression(Ast.Block block, State state) {
		return analyzeBlockContents(block, state, true);
	}

	private Type analyzeBlockContents(Ast.Block block, State state, boolean requireResult) {
		state.pushScope();
		for (Ast.Statement statement : block.statements()) {
			analyzeStatement(statement, state);
		}
		Type resultType = Type.VOID;
		if (block.result().isPresent()) {
			resultType = analyzeExpression(block.result().get(), state);
		} else if (requireResult) {
			state.errors.add("Block expression requires a final expression");
		}
		state.popScope();
		return resultType;
	}

	private Type analyzeCall(Ast.CallExpression call, State state) {
		if (!state.functions.containsKey(call.callee())) {
			state.errors.add("Unknown function '" + call.callee() + "'");
			for (Ast.Expression argument : call.arguments()) {
				analyzeExpression(argument, state);
			}
			return Type.I32;
		}
		FunctionSymbol symbol = state.functions.get(call.callee());
		if (symbol.parameterTypes().size() != call.arguments().size()) {
			state.errors.add("Function '" + call.callee() + "' expects " + symbol.parameterTypes().size()
					+ " arguments but got " + call.arguments().size());
		}
		int limit = Math.min(symbol.parameterTypes().size(), call.arguments().size());
		for (int i = 0; i < limit; i++) {
			Type argType = analyzeExpression(call.arguments().get(i), state);
			if (argType != symbol.parameterTypes().get(i)) {
				state.errors.add("Argument " + (i + 1) + " for function '" + call.callee() + "' has type " + argType
						+ " but expected " + symbol.parameterTypes().get(i));
			}
		}
		for (int i = limit; i < call.arguments().size(); i++) {
			analyzeExpression(call.arguments().get(i), state);
		}
		return symbol.returnType();
	}

	private Type analyzeBinary(Ast.BinaryExpression binary, Type left, Type right, State state) {
		return switch (binary.operator()) {
			case ADD, SUBTRACT, MULTIPLY -> {
				if (!left.isNumeric() || !right.isNumeric()) {
					state.errors.add("Arithmetic operators require numeric operands");
				}
				yield Type.I32;
			}
			case EQUALS -> {
				if (left != right) {
					state.errors.add("Equality operands must have the same type");
				}
				yield Type.BOOL;
			}
			case LESS -> {
				if (!left.isNumeric() || !right.isNumeric()) {
					state.errors.add("Comparison '<' requires numeric operands");
				}
				yield Type.BOOL;
			}
		};
	}

	private Option<VariableSymbol> resolveVariable(String name, State state) {
		for (Map<String, VariableSymbol> scope : state.scopes) {
			if (scope.containsKey(name)) {
				return Option.ok(scope.get(name));
			}
		}
		if (state.globalVariables.containsKey(name)) {
			return Option.ok(state.globalVariables.get(name));
		}
		return Option.err();
	}

	private Optional<VariableSymbol> variableOrReport(String name, State state, String errorMessage) {
		Option<VariableSymbol> resolved = resolveVariable(name, state);
		if (resolved instanceof Option.Ok<VariableSymbol> ok) {
			return Optional.of(ok.value());
		}
		state.errors.add(errorMessage);
		return Optional.empty();
	}

	private Type resolveTypeRef(Ast.TypeRef typeRef, boolean allowVoid, State state) {
		return switch (typeRef.name()) {
			case "I32" -> Type.I32;
			case "Bool" -> Type.BOOL;
			case "Void" -> {
				if (!allowVoid) {
					state.errors.add("Void type not allowed here");
					yield Type.I32;
				}
				yield Type.VOID;
			}
			default -> {
				state.errors.add("Unknown type '" + typeRef.name() + "'");
				yield Type.I32;
			}
		};
	}

	private String generateGlobalName(String name, State state) {
		state.globalIndex++;
		return "g_" + name + "_" + state.globalIndex;
	}

	private String intrinsicCName(String name) {
		if ("readInt".equals(name)) {
			return "magma_readInt";
		}
		return name;
	}
}
