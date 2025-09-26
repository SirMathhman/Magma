package magma.compile.ast;

import magma.api.Option;

import java.util.List;
import java.util.Objects;

public final class Ast {
	public sealed interface Statement
			permits LetStatement, AssignmentStatement, IncrementStatement, WhileStatement, BlockStatement,
			ExpressionStatement, ReturnStatement, StructDecl {}

	public sealed interface Expression
			permits IdentifierExpression, LiteralIntExpression, LiteralBoolExpression, UnaryExpression, BinaryExpression,
			CallExpression, IfExpression, BlockExpression, StructLiteralExpression, FieldAccessExpression {}

	public record Program(List<IntrinsicDecl> intrinsics, List<FunctionDecl> functions, List<Statement> statements,
												Option<Expression> finalExpression) {
		public Program(List<IntrinsicDecl> intrinsics,
									 List<FunctionDecl> functions,
									 List<Statement> statements,
									 Option<Expression> finalExpression) {
			this.intrinsics = List.copyOf(intrinsics);
			this.functions = List.copyOf(functions);
			this.statements = List.copyOf(statements);
			this.finalExpression = Objects.requireNonNull(finalExpression);
		}


	}

	public record IntrinsicDecl(String name, TypeRef returnType, List<TypeRef> parameterTypes) {}

	public record TypeRef(String name) {}

	public record Parameter(String name, TypeRef type) {}

	public record FunctionDecl(String name, List<Parameter> parameters, Option<TypeRef> returnType, Block body) {
		public FunctionDecl(String name, List<Parameter> parameters, Option<TypeRef> returnType, Block body) {
			this.name = name;
			this.parameters = List.copyOf(parameters);
			this.returnType = Objects.requireNonNull(returnType);
			this.body = Objects.requireNonNull(body);
		}
	}

	public record Block(List<Statement> statements, Option<Expression> result) {
		public Block(List<Statement> statements, Option<Expression> result) {
			this.statements = List.copyOf(statements);
			this.result = Objects.requireNonNull(result);
		}


	}

	public record LetStatement(boolean mutable, String name, Option<TypeRef> typeAnnotation, Expression initializer)
			implements Statement {}

	public record AssignmentStatement(String name, AssignmentOp op, Expression expression) implements Statement {}

	public record IncrementStatement(String name) implements Statement {}

	public record WhileStatement(Expression condition, Statement body) implements Statement {}

	public record BlockStatement(Block block) implements Statement {}

	public record ExpressionStatement(Expression expression) implements Statement {}

	public record ReturnStatement(Option<Expression> expression) implements Statement {}

	public record StructDecl(String name, List<StructField> fields) implements Statement {
		public StructDecl(String name, List<StructField> fields) {
			this.name = name;
			this.fields = List.copyOf(fields);
		}
	}

	public record StructField(String name, TypeRef type) {}

	public record IdentifierExpression(String name) implements Expression {}

	public record LiteralIntExpression(int value) implements Expression {}

	public record LiteralBoolExpression(boolean value) implements Expression {}

	public record UnaryExpression(UnaryOperator operator, Expression expression) implements Expression {}

	public record BinaryExpression(Expression left, BinaryOperator operator, Expression right) implements Expression {}

	public record CallExpression(String callee, List<Expression> arguments) implements Expression {}

	public record IfExpression(Expression condition, Expression thenBranch, Expression elseBranch)
			implements Expression {}

	public record BlockExpression(Block block) implements Expression {}

	public record StructLiteralExpression(String structName, List<Expression> fieldValues) implements Expression {
		public StructLiteralExpression(String structName, List<Expression> fieldValues) {
			this.structName = structName;
			this.fieldValues = List.copyOf(fieldValues);
		}
	}

	public record FieldAccessExpression(Expression object, String fieldName) implements Expression {}

	private Ast() {
	}

	public enum AssignmentOp {
		ASSIGN("="), PLUS_ASSIGN("+="), MINUS_ASSIGN("-="), STAR_ASSIGN("*="), SLASH_ASSIGN("/=");

		private final String text;

		AssignmentOp(String text) {
			this.text = text;
		}

		public String text() {
			return text;
		}
	}

	public enum UnaryOperator {
		NEGATE();

		UnaryOperator() {
		}

	}

	public enum BinaryOperator {
		ADD(), SUBTRACT(), MULTIPLY(), EQUALS(), LESS();

		BinaryOperator() {
		}
	}
}
