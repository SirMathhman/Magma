package magma.compiler.ast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Ast {
	private Ast() {
	}

	public static final class Program {
		private final List<IntrinsicDecl> intrinsics;
		private final List<FunctionDecl> functions;
		private final List<Statement> statements;
		private final Optional<Expression> finalExpression;

		public Program(List<IntrinsicDecl> intrinsics,
				List<FunctionDecl> functions,
				List<Statement> statements,
				Optional<Expression> finalExpression) {
			this.intrinsics = List.copyOf(intrinsics);
			this.functions = List.copyOf(functions);
			this.statements = List.copyOf(statements);
			this.finalExpression = Objects.requireNonNull(finalExpression);
		}

		public List<IntrinsicDecl> intrinsics() {
			return intrinsics;
		}

		public List<FunctionDecl> functions() {
			return functions;
		}

		public List<Statement> statements() {
			return statements;
		}

		public Optional<Expression> finalExpression() {
			return finalExpression;
		}
	}

	public record IntrinsicDecl(String name, TypeRef returnType, List<TypeRef> parameterTypes) {
	}

	public record TypeRef(String name) {
	}

	public record Parameter(String name, TypeRef type) {
	}

	public static final class FunctionDecl {
		private final String name;
		private final List<Parameter> parameters;
		private final Optional<TypeRef> returnType;
		private final Block body;

		public FunctionDecl(String name,
				List<Parameter> parameters,
				Optional<TypeRef> returnType,
				Block body) {
			this.name = name;
			this.parameters = List.copyOf(parameters);
			this.returnType = Objects.requireNonNull(returnType);
			this.body = Objects.requireNonNull(body);
		}

		public String name() {
			return name;
		}

		public List<Parameter> parameters() {
			return parameters;
		}

		public Optional<TypeRef> returnType() {
			return returnType;
		}

		public Block body() {
			return body;
		}
	}

	public static final class Block {
		private final List<Statement> statements;
		private final Optional<Expression> result;

		public Block(List<Statement> statements, Optional<Expression> result) {
			this.statements = List.copyOf(statements);
			this.result = Objects.requireNonNull(result);
		}

		public List<Statement> statements() {
			return statements;
		}

		public Optional<Expression> result() {
			return result;
		}
	}

	public sealed interface Statement permits LetStatement, AssignmentStatement,
			IncrementStatement, WhileStatement, BlockStatement, ExpressionStatement, ReturnStatement {
	}

	public record LetStatement(boolean mutable, String name, Optional<TypeRef> typeAnnotation,
			Expression initializer) implements Statement {
	}

	public enum AssignmentOp {
		ASSIGN("="),
		PLUS_ASSIGN("+="),
		MINUS_ASSIGN("-="),
		STAR_ASSIGN("*="),
		SLASH_ASSIGN("/=");

		private final String text;

		AssignmentOp(String text) {
			this.text = text;
		}

		public String text() {
			return text;
		}
	}

	public record AssignmentStatement(String name, AssignmentOp op, Expression expression)
			implements Statement {
	}

	public record IncrementStatement(String name) implements Statement {
	}

	public record WhileStatement(Expression condition, Statement body) implements Statement {
	}

	public record BlockStatement(Block block) implements Statement {
	}

	public record ExpressionStatement(Expression expression) implements Statement {
	}

	public record ReturnStatement(Optional<Expression> expression) implements Statement {
	}

	public sealed interface Expression permits IdentifierExpression, LiteralIntExpression,
			LiteralBoolExpression, UnaryExpression, BinaryExpression, CallExpression,
			IfExpression, BlockExpression {
	}

	public record IdentifierExpression(String name) implements Expression {
	}

	public record LiteralIntExpression(int value) implements Expression {
	}

	public record LiteralBoolExpression(boolean value) implements Expression {
	}

	public enum UnaryOperator {
		NEGATE("-");

		private final String text;

		UnaryOperator(String text) {
			this.text = text;
		}

		public String text() {
			return text;
		}
	}

	public record UnaryExpression(UnaryOperator operator, Expression expression) implements Expression {
	}

	public enum BinaryOperator {
		ADD("+"),
		SUBTRACT("-"),
		MULTIPLY("*"),
		EQUALS("=="),
		LESS("<");

		private final String text;

		BinaryOperator(String text) {
			this.text = text;
		}

		public String text() {
			return text;
		}
	}

	public record BinaryExpression(Expression left, BinaryOperator operator, Expression right)
			implements Expression {
	}

	public record CallExpression(String callee, List<Expression> arguments) implements Expression {
	}

	public record IfExpression(Expression condition, Expression thenBranch, Expression elseBranch)
			implements Expression {
	}

	public record BlockExpression(Block block) implements Expression {
	}
}
