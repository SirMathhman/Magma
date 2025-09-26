package magma.compile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Option;
import java.util.StringJoiner;
import java.util.function.Consumer;

import magma.compile.ast.Ast;

public final class CodeGenerator {
	private SemanticAnalyzer.AnalysisResult analysis;
	private final Deque<Map<String, VariableSymbol>> scopeStack = new ArrayDeque<>();
	private final CodeBuilder builder = new CodeBuilder();
	private int tempCounter = 0;

	public String generate(SemanticAnalyzer.AnalysisResult analysis) {
		this.analysis = analysis;
		scopeStack.clear();
		builder.clear();
		tempCounter = 0;

		emitPreamble();
		emitIntrinsics();
		emitGlobalDeclarations();
		emitFunctionPrototypes();
		emitFunctionDefinitions();
		emitMain();

		return builder.toString();
	}

	private void emitPreamble() {
		builder.appendLine("#include <stdio.h>");
		builder.appendLine("#include <stdlib.h>");
		builder.newLine();
	}

	private void emitIntrinsics() {
		for (FunctionSymbol symbol : analysis.functions().values()) {
			if (!symbol.intrinsic()) {
				continue;
			}
			if ("readInt".equals(symbol.name())) {
				builder.appendLine("static int magma_readInt(void) {");
				builder.increaseIndent();
				builder.appendLine("int value;");
				builder.appendLine("if (scanf(\"%d\", &value) != 1) {");
				builder.increaseIndent();
				builder.appendLine("exit(1);");
				builder.decreaseIndent();
				builder.appendLine("}");
				builder.appendLine("return value;");
				builder.decreaseIndent();
				builder.appendLine("}");
				builder.newLine();
			}
		}
	}

	private void emitGlobalDeclarations() {
		if (analysis.globalVariables().isEmpty()) {
			return;
		}
		for (VariableSymbol symbol : analysis.globalVariables().values()) {
			builder.appendLine("static " + cType(symbol.type()) + " " + symbol.cName() + ";");
		}
		builder.newLine();
	}

	private void emitFunctionPrototypes() {
		boolean any = false;
		for (FunctionSymbol symbol : analysis.functions().values()) {
			if (symbol.intrinsic()) {
				continue;
			}
			String prototype = "static " + cType(symbol.returnType()) + " " + symbol.cName() + "(" + parameterList(symbol)
					+ ");";
			builder.appendLine(prototype);
			any = true;
		}
		if (any) {
			builder.newLine();
		}
	}

	private void emitFunctionDefinitions() {
		for (FunctionSymbol symbol : analysis.functions().values()) {
			if (symbol.intrinsic()) {
				continue;
			}
			Ast.FunctionDecl declaration = symbol.declaration().orElseThrow();
			builder.appendLine(
					"static " + cType(symbol.returnType()) + " " + symbol.cName() + "(" + parameterList(symbol) + ") {");
			builder.increaseIndent();

			resetScopes();
			pushScope(new LinkedHashMap<>(analysis.globalVariables()));
			Map<String, VariableSymbol> params = new LinkedHashMap<>();
			for (int i = 0; i < symbol.parameterNames().size(); i++) {
				String name = symbol.parameterNames().get(i);
				Type type = symbol.parameterTypes().get(i);
				VariableSymbol paramSymbol = new VariableSymbol(name, type, false, false, name);
				params.put(name, paramSymbol);
			}
			pushScope(params);
			pushScope(new LinkedHashMap<>());

			emitStatements(declaration.body().statements());
			declaration.body().result().ifPresent(expr -> {
				ExpressionResult result = emitExpression(expr);
				result.emitPrefix();
				builder.appendLine("return " + valueForType(result) + ";");
			});

			if (symbol.returnType() == Type.VOID && declaration.body().result().isEmpty()) {
				builder.appendLine("return;");
			}

			popScope();
			popScope();
			popScope();

			builder.decreaseIndent();
			builder.appendLine("}");
			builder.newLine();
		}
	}

	private void emitMain() {
		builder.appendLine("int main(void) {");
		builder.increaseIndent();

		resetScopes();
		pushScope(new LinkedHashMap<>(analysis.globalVariables()));
		pushScope(new LinkedHashMap<>());

		emitStatements(analysis.program().statements());

		if (analysis.finalExpression() instanceof magma.api.Option.Some<Ast.Expression>) {
			ExpressionResult result = emitExpression(analysis.finalExpression().get());
			result.emitPrefix();
			builder.appendLine("exit(" + valueForType(result) + ");");
		} else {
			builder.appendLine("exit(0);");
		}
		builder.appendLine("return 0;");

		popScope();
		popScope();

		builder.decreaseIndent();
		builder.appendLine("}");
	}

	private void emitStatements(List<Ast.Statement> statements) {
		for (Ast.Statement statement : statements) {
			emitStatement(statement);
		}
	}

	private void emitStatement(Ast.Statement statement) {
		if (statement instanceof Ast.LetStatement let) {
			emitLet(let);
		} else if (statement instanceof Ast.AssignmentStatement assignment) {
			emitAssignment(assignment);
		} else if (statement instanceof Ast.IncrementStatement increment) {
			emitIncrement(increment);
		} else if (statement instanceof Ast.WhileStatement whileStmt) {
			emitWhile(whileStmt);
		} else if (statement instanceof Ast.BlockStatement(Ast.Block block)) {
			emitBlock(block);
		} else if (statement instanceof Ast.ExpressionStatement(Ast.Expression expression)) {
			emitExpressionStatement(expression);
		} else if (statement instanceof Ast.ReturnStatement returnStatement) {
			emitReturn(returnStatement);
		}
	}

	private void emitLet(Ast.LetStatement let) {
		if (!analysis.letBindings().containsKey(let)) {
			return;
		}
		VariableSymbol symbol = analysis.letBindings().get(let);
		ExpressionResult initializer = emitExpression(let.initializer());
		initializer.emitPrefix();
		String value = valueForType(initializer);
		if (symbol.global()) {
			builder.appendLine(symbol.cName() + " = " + value + ";");
		} else {
			builder.appendLine(cType(symbol.type()) + " " + symbol.cName() + " = " + value + ";");
			currentScope().put(symbol.name(), symbol);
		}
	}

	private void emitAssignment(Ast.AssignmentStatement assignment) {
		Option<VariableSymbol> symbolOpt = resolveForEmission(assignment.name());
		if (symbolOpt.isEmpty()) {
			return;
		}
		VariableSymbol symbol = symbolOpt.get();
		ExpressionResult rhs = emitExpression(assignment.expression());
		rhs.emitPrefix();
		String value = valueForType(rhs);
		if (assignment.op() == Ast.AssignmentOp.ASSIGN) {
			builder.appendLine(symbol.cName() + " = " + value + ";");
		} else {
			builder.appendLine(symbol.cName() + " " + assignment.op().text() + " " + value + ";");
		}
	}

	private void emitIncrement(Ast.IncrementStatement increment) {
		Option<VariableSymbol> symbolOpt = resolveForEmission(increment.name());
		if (symbolOpt.isEmpty()) {
			return;
		}
		VariableSymbol symbol = symbolOpt.get();
		builder.appendLine(symbol.cName() + "++;");
	}

	private void emitWhile(Ast.WhileStatement whileStmt) {
		ExpressionResult condition = emitExpression(whileStmt.condition());
		condition.emitPrefix();
		builder.appendLine("while (" + condValue(condition) + ") {");
		builder.increaseIndent();
		pushScope(new LinkedHashMap<>());
		if (whileStmt.body() instanceof Ast.BlockStatement(Ast.Block block)) {
			emitBlock(block);
		} else {
			emitStatement(whileStmt.body());
		}
		popScope();
		builder.decreaseIndent();
		builder.appendLine("}");
	}

	private void emitBlock(Ast.Block block) {
		emitBlockStructure(block, result -> builder.appendLine(valueForType(result) + ";"));
	}

	private void emitExpressionStatement(Ast.Expression expression) {
		ExpressionResult result = emitExpression(expression);
		result.emitPrefix();
		builder.appendLine(valueForType(result) + ";");
	}

	private void emitReturn(Ast.ReturnStatement returnStatement) {
		if (returnStatement.expression() instanceof magma.api.Option.Some<Ast.Expression>) {
			ExpressionResult expr = emitExpression(returnStatement.expression().get());
			expr.emitPrefix();
			builder.appendLine("return " + valueForType(expr) + ";");
		} else {
			builder.appendLine("return;");
		}
	}

	private ExpressionResult emitExpression(Ast.Expression expression) {
		Type type = analysis.expressionTypes().getOrDefault(expression, Type.I32);
		List<Runnable> prefix = new ArrayList<>();
		String expressionText;

		switch (expression) {
			case Ast.LiteralIntExpression literalInt -> expressionText = Integer.toString(literalInt.value());
			case Ast.LiteralBoolExpression literalBool -> expressionText = literalBool.value() ? "1" : "0";
			case Ast.IdentifierExpression identifier -> {
				if (analysis.identifierBindings().containsKey(identifier)) {
					VariableSymbol symbol = analysis.identifierBindings().get(identifier);
					expressionText = symbol.cName();
				} else {
					expressionText = identifier.name();
				}
			}
			case Ast.UnaryExpression unary -> {
				ExpressionResult operand = emitExpression(unary.expression());
				prefix.addAll(operand.prefix);
				expressionText = "(-(" + operand.expression + "))";
			}
			case Ast.BinaryExpression binary -> {
				ExpressionResult left = emitExpression(binary.left());
				ExpressionResult right = emitExpression(binary.right());
				prefix.addAll(left.prefix);
				prefix.addAll(right.prefix);
				expressionText =
						emitBinaryExpression(binary.operator(), left.expression, right.expression);
			}
			case Ast.CallExpression call -> {
				List<ExpressionResult> args = new ArrayList<>();
				for (Ast.Expression argExpr : call.arguments()) {
					ExpressionResult arg = emitExpression(argExpr);
					prefix.addAll(arg.prefix);
					args.add(arg);
				}
				StringJoiner joiner = new StringJoiner(", ");
				for (ExpressionResult arg : args) {
					joiner.add(valueForType(arg));
				}
				String calleeName;
				if (analysis.functions().containsKey(call.callee())) {
					calleeName = analysis.functions().get(call.callee()).cName();
				} else {
					calleeName = call.callee();
				}
				expressionText = calleeName + "(" + joiner + ")";
			}
			case Ast.IfExpression ifExpression -> {
				ExpressionResult cond = emitExpression(ifExpression.condition());
				ExpressionResult thenExpr = emitExpression(ifExpression.thenBranch());
				ExpressionResult elseExpr = emitExpression(ifExpression.elseBranch());
				prefix.addAll(cond.prefix);
				prefix.addAll(thenExpr.prefix);
				prefix.addAll(elseExpr.prefix);
				expressionText =
						"((" + condValue(cond) + ") ? (" + valueForType(thenExpr) + ") : (" + valueForType(elseExpr) + "))";
			}
			case Ast.BlockExpression blockExpression ->
					expressionText = emitBlockExpression(blockExpression.block(), type, prefix);
			case null, default -> expressionText = "0";
		}

		return new ExpressionResult(prefix, expressionText, type);
	}

	private String emitBlockExpression(Ast.Block block, Type type, List<Runnable> prefix) {
		final String tempName = freshTempName();
		prefix.add(() -> {
			builder.appendLine(cType(type) + " " + tempName + ";");
			emitBlockStructure(block, inner -> builder.appendLine(tempName + " = " + valueForType(inner) + ";"));
		});
		return tempName;
	}

	private void emitBlockStructure(Ast.Block block, Consumer<ExpressionResult> resultConsumer) {
		builder.appendLine("{");
		builder.increaseIndent();
		pushScope(new LinkedHashMap<>());
		emitStatements(block.statements());
		block.result().ifPresent(expr -> {
			ExpressionResult result = emitExpression(expr);
			result.emitPrefix();
			resultConsumer.accept(result);
		});
		popScope();
		builder.decreaseIndent();
		builder.appendLine("}");
	}

	private String emitBinaryExpression(Ast.BinaryOperator operator, String left, String right) {
		return switch (operator) {
			case ADD -> "((" + left + ") + (" + right + "))";
			case SUBTRACT -> "((" + left + ") - (" + right + "))";
			case MULTIPLY -> "((" + left + ") * (" + right + "))";
			case EQUALS -> "(((" + left + ") == (" + right + ")) ? 1 : 0)";
			case LESS -> "(((" + left + ") < (" + right + ")) ? 1 : 0)";
		};
	}

	private Option<VariableSymbol> lookupVariable(String name) {
		for (Map<String, VariableSymbol> scope : scopeStack) {
			if (scope.containsKey(name)) {
				return Option.some(scope.get(name));
			}
		}
		if (analysis.globalVariables().containsKey(name)) {
			return Option.some(analysis.globalVariables().get(name));
		}
		return Option.none();
	}

	private Option<VariableSymbol> resolveForEmission(String name) {
		Option<VariableSymbol> resolved = lookupVariable(name);
		if (resolved instanceof Option.Some<VariableSymbol> some) {
			return Option.of(some.value());
		}
		return Option.empty();
	}

	private Map<String, VariableSymbol> currentScope() {
		return scopeStack.peek();
	}

	private void pushScope(Map<String, VariableSymbol> scope) {
		scopeStack.push(scope);
	}

	private void popScope() {
		scopeStack.pop();
	}

	private void resetScopes() {
		scopeStack.clear();
	}

	private String freshTempName() {
		tempCounter++;
		return "tmp_" + tempCounter;
	}

	private String cType(Type type) {
		return switch (type) {
			case I32, BOOL -> "int";
			case VOID -> "void";
		};
	}

	private String parameterList(FunctionSymbol symbol) {
		if (symbol.parameterTypes().isEmpty()) {
			return "void";
		}
		StringJoiner joiner = new StringJoiner(", ");
		for (int i = 0; i < symbol.parameterTypes().size(); i++) {
			joiner.add(cType(symbol.parameterTypes().get(i)) + " " + symbol.parameterNames().get(i));
		}
		return joiner.toString();
	}

	private String valueForType(ExpressionResult result) {
		if (result.type == Type.BOOL) {
			return "((" + result.expression + ") ? 1 : 0)";
		}
		return result.expression;
	}

	private String condValue(ExpressionResult result) {
		if (result.type == Type.BOOL) {
			return "(" + valueForType(result) + ")";
		}
		return "(" + result.expression + ")";
	}

	private static final class CodeBuilder {
		private final StringBuilder sb = new StringBuilder();
		private int indent = 0;

		void clear() {
			sb.setLength(0);
			indent = 0;
		}

		void appendLine(String line) {
			sb.append("    ".repeat(Math.max(0, indent))).append(line).append('\n');
		}

		void increaseIndent() {
			indent++;
		}

		void decreaseIndent() {
			indent = Math.max(0, indent - 1);
		}

		void newLine() {
			sb.append('\n');
		}

		@Override
		public String toString() {
			return sb.toString();
		}
	}

	private record ExpressionResult(List<Runnable> prefix, String expression, Type type) {

		void emitPrefix() {
				for (Runnable runnable : prefix) {
					runnable.run();
				}
			}
		}
}
