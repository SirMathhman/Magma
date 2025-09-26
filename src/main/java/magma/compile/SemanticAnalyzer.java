package magma.compile;

import magma.api.Option;
import magma.compile.ast.Ast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SemanticAnalyzer {
	public record AnalysisResult(Ast.Program program, IdentityHashMap<Ast.Expression, Type> expressionTypes,
																					 IdentityHashMap<Ast.IdentifierExpression, VariableSymbol> identifierBindings,
																					 IdentityHashMap<Ast.LetStatement, VariableSymbol> letBindings,
																					 LinkedHashMap<String, VariableSymbol> globalVariables,
																					 LinkedHashMap<String, FunctionSymbol> functions,
																					 LinkedHashMap<String, Type.StructType> structTypes, Type finalType,
																					 Option<Ast.Expression> finalExpression, List<String> errors) {}	private static final class State {
		final Ast.Program program;
		final IdentityHashMap<Ast.Expression, Type> expressionTypes = new IdentityHashMap<>();
		final IdentityHashMap<Ast.IdentifierExpression, VariableSymbol> identifierBindings = new IdentityHashMap<>();
		final IdentityHashMap<Ast.LetStatement, VariableSymbol> letBindings = new IdentityHashMap<>();
		final LinkedHashMap<String, VariableSymbol> globalVariables = new LinkedHashMap<>();
		final LinkedHashMap<String, FunctionSymbol> functions = new LinkedHashMap<>();
		final LinkedHashMap<String, Type.StructType> structTypes = new LinkedHashMap<>();
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
		Type finalType = Type.PrimitiveType.I32;
		Option<Ast.Expression> finalExpr = program.finalExpression();
		if (finalExpr instanceof Option.Some<Ast.Expression>) {
			finalType = analyzeExpression(finalExpr.get(), state);
			if (finalType == Type.PrimitiveType.VOID) {
				state.errors.add("Final expression cannot be void");
				finalType = Type.PrimitiveType.I32;
			}
		}

		analyzeFunctionBodies(state);

		state.popScope();

		return new AnalysisResult(program, state.expressionTypes, state.identifierBindings, state.letBindings,
															state.globalVariables, state.functions, state.structTypes, finalType, finalExpr, state.errors);
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
			FunctionSymbol symbol =
					new FunctionSymbol(decl.name(), params, paramNames, returnType, true, Option.empty(), cName);
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
			Type returnType = function.returnType().map(typeRef -> resolveTypeRef(typeRef, true, state)).orElse(Type.PrimitiveType.I32);
			FunctionSymbol symbol =
					new FunctionSymbol(function.name(), parameterTypes, parameterNames, returnType, false, Option.of(function),
														 function.name());
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
		Option<Ast.FunctionDecl> declarationOpt = symbol.declaration();
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
		Option<Ast.Expression> finalExpr = declaration.body().result();
		if (symbol.returnType() == Type.PrimitiveType.VOID) {
			if (finalExpr instanceof Option.Some<Ast.Expression>) {
				state.errors.add("Void function '" + symbol.name() + "' cannot have a final expression");
			}
		} else {
			if (finalExpr instanceof Option.Some<Ast.Expression>) {
				Type resultType = state.expressionTypes.getOrDefault(finalExpr.get(), Type.PrimitiveType.I32);
				if (!resultType.equals(symbol.returnType())) {
					state.errors.add(
							"Function '" + symbol.name() + "' final expression has type " + resultType + " but expected " +
							symbol.returnType());
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
		} else if (statement instanceof Ast.BlockStatement(Ast.Block block)) {
			analyzeBlock(block, state);
		} else if (statement instanceof Ast.ExpressionStatement(Ast.Expression expression)) {
			analyzeExpression(expression, state);
		} else if (statement instanceof Ast.ReturnStatement returnStatement) {
			analyzeReturn(returnStatement, state);
		} else if (statement instanceof Ast.StructDecl structDecl) {
			analyzeStructDecl(structDecl, state);
		}
	}

	private void analyzeLet(Ast.LetStatement let, State state) {
		if (state.currentScope().containsKey(let.name())) {
			state.errors.add("Variable '" + let.name() + "' is already defined in this scope");
		}
		Type initializerType = analyzeExpression(let.initializer(), state);
		Type declaredType =
				let.typeAnnotation().map(typeRef -> resolveTypeRef(typeRef, false, state)).orElse(initializerType);
		if (declaredType == Type.PrimitiveType.VOID) {
			state.errors.add("Variable '" + let.name() + "' cannot have type Void");
			declaredType = Type.PrimitiveType.I32;
		}
		if (!initializerType.equals(declaredType)) {
			state.errors.add(
					"Type mismatch: cannot assign " + initializerType + " to variable '" + let.name() + "' of type " +
					declaredType);
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
		Option<VariableSymbol> symbolOpt =
				variableOrReport(assignment.name(), state, "Unknown variable '" + assignment.name() + "'");
		if (symbolOpt.isEmpty()) {
			analyzeExpression(assignment.expression(), state);
			return;
		}
		VariableSymbol symbol = symbolOpt.get();
		if (symbol.isImmutable()) {
			state.errors.add("Cannot assign to immutable variable '" + assignment.name() + "'");
		}
		Type expressionType = analyzeExpression(assignment.expression(), state);
		switch (assignment.op()) {
			case ASSIGN -> {
				if (!expressionType.equals(symbol.type())) {
					state.errors.add("Type mismatch assigning to '" + assignment.name() + "'");
				}
			}
			case PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, SLASH_ASSIGN -> {
				if (symbol.type().isNonNumeric() || expressionType.isNonNumeric()) {
					state.errors.add("Compound assignment requires numeric types for '" + assignment.name() + "'");
				}
			}
		}
	}

	private void analyzeIncrement(Ast.IncrementStatement inc, State state) {
		Option<VariableSymbol> symbolOpt = variableOrReport(inc.name(), state, "Unknown variable '" + inc.name() + "'");
		if (symbolOpt.isEmpty()) {
			return;
		}
		VariableSymbol symbol = symbolOpt.get();
		if (symbol.isImmutable()) {
			state.errors.add("Cannot increment immutable variable '" + inc.name() + "'");
		}
		if (symbol.type().isNonNumeric()) {
			state.errors.add("Increment requires numeric variable for '" + inc.name() + "'");
		}
	}

	private void analyzeWhile(Ast.WhileStatement whileStmt, State state) {
		Type condType = analyzeExpression(whileStmt.condition(), state);
		if (condType.isNonBoolean()) {
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
		if (returnStatement.expression() instanceof Option.Some<Ast.Expression>) {
			Type exprType = analyzeExpression(returnStatement.expression().get(), state);
			if (state.currentReturnType == Type.PrimitiveType.VOID) {
				state.errors.add("Void function cannot return a value");
			} else if (!exprType.equals(state.currentReturnType)) {
				state.errors.add("Return type mismatch: expected " + state.currentReturnType + " but found " + exprType);
			}
		} else {
			if (state.currentReturnType != Type.PrimitiveType.VOID) {
				state.errors.add("Non-void function must return a value");
			}
		}
	}

	private Type analyzeExpression(Ast.Expression expression, State state) {
		Type type;
		switch (expression) {
			case Ast.LiteralIntExpression _ -> type = Type.PrimitiveType.I32;
			case Ast.LiteralBoolExpression _ -> type = Type.PrimitiveType.BOOL;
			case Ast.IdentifierExpression identifier -> {
				Option<VariableSymbol> symbolOpt = resolveVariable(identifier.name(), state);
				if (symbolOpt instanceof Option.Some<VariableSymbol>(VariableSymbol symbol)) {
					state.identifierBindings.put(identifier, symbol);
					type = symbol.type();
				} else {
					// Check if it's a function reference
					FunctionSymbol functionSymbol = state.functions.get(identifier.name());
					if (functionSymbol != null) {
						// Create function type
						type = new Type.FunctionType(functionSymbol.parameterTypes(), functionSymbol.returnType());
					} else {
						state.errors.add("Unknown variable or function '" + identifier.name() + "'");
						type = Type.PrimitiveType.I32;
					}
				}
			}
			case Ast.UnaryExpression unary -> {
				Type operand = analyzeExpression(unary.expression(), state);
				if (operand.isNonNumeric()) {
					state.errors.add("Unary '-' requires numeric operand");
				}
				type = Type.PrimitiveType.I32;
			}
			case Ast.BinaryExpression binary -> {
				Type left = analyzeExpression(binary.left(), state);
				Type right = analyzeExpression(binary.right(), state);
				type = analyzeBinary(binary, left, right, state);
			}
			case Ast.CallExpression call -> type = analyzeCall(call, state);
			case Ast.IfExpression ifExpr -> {
				Type condType = analyzeExpression(ifExpr.condition(), state);
				if (condType.isNonBoolean()) {
					state.errors.add("If condition must be boolean");
				}
				Type thenType = analyzeExpression(ifExpr.thenBranch(), state);
				Type elseType = analyzeExpression(ifExpr.elseBranch(), state);
				if (!thenType.equals(elseType)) {
					state.errors.add("If branches must have the same type");
				}
				type = thenType;
			}
			case Ast.BlockExpression blockExpression -> type = analyzeBlockExpression(blockExpression.block(), state);
			case Ast.StructLiteralExpression structLiteral -> type = analyzeStructLiteral(structLiteral, state);
			case Ast.FieldAccessExpression fieldAccess -> type = analyzeFieldAccess(fieldAccess, state);
			case Ast.ReferenceExpression reference -> type = analyzeReference(reference, state);
			case Ast.DereferenceExpression dereference -> type = analyzeDereference(dereference, state);
			case null, default -> type = Type.PrimitiveType.I32;
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
		Type resultType = Type.PrimitiveType.VOID;
		if (block.result() instanceof Option.Some<Ast.Expression>) {
			resultType = analyzeExpression(block.result().get(), state);
		} else if (requireResult) {
			state.errors.add("Block expression requires a final expression");
		}
		state.popScope();
		return resultType;
	}

	private Type analyzeCall(Ast.CallExpression call, State state) {
		// First check if it's a direct function call
		if (state.functions.containsKey(call.callee())) {
			FunctionSymbol symbol = state.functions.get(call.callee());
			return analyzeCallWithSignature(call, symbol.parameterTypes(), symbol.returnType(), state);
		}
		
		// Then check if it's a function variable call
		Option<VariableSymbol> variableOpt = resolveVariable(call.callee(), state);
		if (variableOpt instanceof Option.Some<VariableSymbol>(VariableSymbol variable)) {
			if (variable.type() instanceof Type.FunctionType functionType) {
				return analyzeCallWithSignature(call, functionType.parameterTypes(), functionType.returnType(), state);
			} else {
				state.errors.add("'" + call.callee() + "' is not a function");
				for (Ast.Expression argument : call.arguments()) {
					analyzeExpression(argument, state);
				}
				return Type.PrimitiveType.I32;
			}
		}
		
		state.errors.add("Unknown function or function variable '" + call.callee() + "'");
		for (Ast.Expression argument : call.arguments()) {
			analyzeExpression(argument, state);
		}
		return Type.PrimitiveType.I32;
	}
	
	private Type analyzeCallWithSignature(Ast.CallExpression call, List<Type> parameterTypes, Type returnType, State state) {
		if (parameterTypes.size() != call.arguments().size()) {
			state.errors.add(
					"Function '" + call.callee() + "' expects " + parameterTypes.size() + " arguments but got " +
					call.arguments().size());
		}
		int limit = Math.min(parameterTypes.size(), call.arguments().size());
		for (int i = 0; i < limit; i++) {
			Type argType = analyzeExpression(call.arguments().get(i), state);
			if (!argType.equals(parameterTypes.get(i))) {
				state.errors.add(
						"Argument " + (i + 1) + " for function '" + call.callee() + "' has type " + argType + " but expected " +
						parameterTypes.get(i));
			}
		}
		for (int i = limit; i < call.arguments().size(); i++) {
			analyzeExpression(call.arguments().get(i), state);
		}
		return returnType;
	}

	private Type analyzeBinary(Ast.BinaryExpression binary, Type left, Type right, State state) {
		return switch (binary.operator()) {
			case ADD, SUBTRACT, MULTIPLY -> {
				if (left.isNonNumeric() || right.isNonNumeric()) {
					state.errors.add("Arithmetic operators require numeric operands");
				}
				yield Type.PrimitiveType.I32;
			}
			case EQUALS -> {
				if (!left.equals(right)) {
					state.errors.add("Equality operands must have the same type");
				}
				yield Type.PrimitiveType.BOOL;
			}
			case LESS -> {
				if (left.isNonNumeric() || right.isNonNumeric()) {
					state.errors.add("Comparison '<' requires numeric operands");
				}
				yield Type.PrimitiveType.BOOL;
			}
		};
	}

	private Option<VariableSymbol> resolveVariable(String name, State state) {
		for (Map<String, VariableSymbol> scope : state.scopes) {
			if (scope.containsKey(name)) {
				return Option.some(scope.get(name));
			}
		}
		if (state.globalVariables.containsKey(name)) {
			return Option.some(state.globalVariables.get(name));
		}
		return Option.none();
	}

	private Option<VariableSymbol> variableOrReport(String name, State state, String errorMessage) {
		Option<VariableSymbol> resolved = resolveVariable(name, state);
		if (resolved instanceof Option.Some<VariableSymbol>(VariableSymbol value)) {
			return Option.of(value);
		}
		state.errors.add(errorMessage);
		return Option.empty();
	}

	private Type resolveTypeRef(Ast.TypeRef typeRef, boolean allowVoid, State state) {
		String typeName = typeRef.name();
		
		// Handle pointer types
		if (typeName.startsWith("*")) {
			String pointeeTypeName = typeName.substring(1);
			// Resolve pointee type directly to ensure we get the same instances
			Type pointeeType = switch (pointeeTypeName) {
				case "I32" -> Type.PrimitiveType.I32;
				case "Bool" -> Type.PrimitiveType.BOOL;
				case "Void" -> Type.PrimitiveType.VOID;
				default -> {
					if (state.structTypes.containsKey(pointeeTypeName)) {
						yield state.structTypes.get(pointeeTypeName);
					}
					state.errors.add("Unknown type '" + pointeeTypeName + "'");
					yield Type.PrimitiveType.I32;
				}
			};
			return new Type.PointerType(pointeeType);
		}
		
		// Handle function types: (Type, Type) => ReturnType
		if (typeName.startsWith("(") && typeName.contains(" => ")) {
			return parseFunctionType(typeName, allowVoid, state);
		}
		
		return switch (typeName) {
			case "I32" -> Type.PrimitiveType.I32;
			case "Bool" -> Type.PrimitiveType.BOOL;
			case "Void" -> {
				if (!allowVoid) {
					state.errors.add("Void type not allowed here");
					yield Type.PrimitiveType.I32;
				}
				yield Type.PrimitiveType.VOID;
			}
			default -> {
				if (state.structTypes.containsKey(typeName)) {
					yield state.structTypes.get(typeName);
				}
				state.errors.add("Unknown type '" + typeName + "'");
				yield Type.PrimitiveType.I32;
			}
		};
	}

	private Type parseFunctionType(String functionTypeString, boolean allowVoid, State state) {
		// Parse function type string like "(I32, Bool) => Void"
		int arrowIndex = functionTypeString.indexOf(" => ");
		if (arrowIndex == -1) {
			state.errors.add("Invalid function type syntax: " + functionTypeString);
			return Type.PrimitiveType.I32;
		}
		
		String paramsPart = functionTypeString.substring(1, arrowIndex).trim();
		if (paramsPart.endsWith(")")) {
			paramsPart = paramsPart.substring(0, paramsPart.length() - 1);
		}
		String returnTypePart = functionTypeString.substring(arrowIndex + 4);
		
		List<Type> parameterTypes = new ArrayList<>();
		if (!paramsPart.trim().isEmpty()) {
			String[] paramTypeNames = paramsPart.split(",");
			for (String paramTypeName : paramTypeNames) {
				String trimmedName = paramTypeName.trim();
				Type paramType = switch (trimmedName) {
					case "I32" -> Type.PrimitiveType.I32;
					case "Bool" -> Type.PrimitiveType.BOOL;
					case "Void" -> Type.PrimitiveType.VOID;
					default -> {
						if (state.structTypes.containsKey(trimmedName)) {
							yield state.structTypes.get(trimmedName);
						} else if (trimmedName.startsWith("*")) {
							// Handle pointer parameters
							yield resolveTypeRef(new Ast.TypeRef(trimmedName), false, state);
						} else {
							state.errors.add("Unknown parameter type '" + trimmedName + "' in function type");
							yield Type.PrimitiveType.I32;
						}
					}
				};
				parameterTypes.add(paramType);
			}
		}
		
		Type returnType = switch (returnTypePart.trim()) {
			case "I32" -> Type.PrimitiveType.I32;
			case "Bool" -> Type.PrimitiveType.BOOL;
			case "Void" -> allowVoid ? Type.PrimitiveType.VOID : Type.PrimitiveType.I32;
			default -> {
				if (state.structTypes.containsKey(returnTypePart.trim())) {
					yield state.structTypes.get(returnTypePart.trim());
				} else if (returnTypePart.trim().startsWith("*")) {
					yield resolveTypeRef(new Ast.TypeRef(returnTypePart.trim()), allowVoid, state);
				} else {
					state.errors.add("Unknown return type '" + returnTypePart.trim() + "' in function type");
					yield Type.PrimitiveType.I32;
				}
			}
		};
		
		return new Type.FunctionType(parameterTypes, returnType);
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
	
	private void analyzeStructDecl(Ast.StructDecl structDecl, State state) {
		if (state.structTypes.containsKey(structDecl.name())) {
			state.errors.add("Struct '" + structDecl.name() + "' is already defined");
			return;
		}
		
		List<Type.StructField> fields = new ArrayList<>();
		for (Ast.StructField field : structDecl.fields()) {
			Type fieldType = resolveTypeRef(field.type(), false, state);
			fields.add(new Type.StructField(field.name(), fieldType));
		}
		
		Type.StructType structType = new Type.StructType(structDecl.name(), fields);
		state.structTypes.put(structDecl.name(), structType);
	}
	
	private Type analyzeStructLiteral(Ast.StructLiteralExpression structLiteral, State state) {
		if (!state.structTypes.containsKey(structLiteral.structName())) {
			state.errors.add("Unknown struct '" + structLiteral.structName() + "'");
			// Still analyze field values for error checking
			for (Ast.Expression fieldValue : structLiteral.fieldValues()) {
				analyzeExpression(fieldValue, state);
			}
			return Type.PrimitiveType.I32;
		}
		
		Type.StructType structType = state.structTypes.get(structLiteral.structName());
		if (structType.fields().size() != structLiteral.fieldValues().size()) {
			state.errors.add("Struct '" + structLiteral.structName() + "' expects " + 
							structType.fields().size() + " fields but got " + structLiteral.fieldValues().size());
		}
		
		int limit = Math.min(structType.fields().size(), structLiteral.fieldValues().size());
		for (int i = 0; i < limit; i++) {
			Type expectedType = structType.fields().get(i).type();
			Type actualType = analyzeExpression(structLiteral.fieldValues().get(i), state);
			if (!actualType.equals(expectedType)) {
				state.errors.add("Field " + (i + 1) + " of struct '" + structLiteral.structName() + 
								"' has type " + actualType + " but expected " + expectedType);
			}
		}
		
		// Analyze remaining field values for error checking
		for (int i = limit; i < structLiteral.fieldValues().size(); i++) {
			analyzeExpression(structLiteral.fieldValues().get(i), state);
		}
		
		return structType;
	}
	
	private Type analyzeFieldAccess(Ast.FieldAccessExpression fieldAccess, State state) {
		Type objectType = analyzeExpression(fieldAccess.object(), state);
		
		if (!(objectType instanceof Type.StructType structType)) {
			state.errors.add("Cannot access field '" + fieldAccess.fieldName() + "' on non-struct type " + objectType);
			return Type.PrimitiveType.I32;
		}
		
		for (Type.StructField field : structType.fields()) {
			if (field.name().equals(fieldAccess.fieldName())) {
				return field.type();
			}
		}
		
		state.errors.add("Struct '" + structType.name() + "' has no field named '" + fieldAccess.fieldName() + "'");
		return Type.PrimitiveType.I32;
	}
	
	private Type analyzeReference(Ast.ReferenceExpression reference, State state) {
		Type exprType = analyzeExpression(reference.expression(), state);
		return new Type.PointerType(exprType);
	}
	
	private Type analyzeDereference(Ast.DereferenceExpression dereference, State state) {
		Type exprType = analyzeExpression(dereference.expression(), state);
		
		if (!(exprType instanceof Type.PointerType pointerType)) {
			state.errors.add("Cannot dereference non-pointer type " + exprType);
			return Type.PrimitiveType.I32;
		}
		
		return pointerType.pointeeType();
	}
}
