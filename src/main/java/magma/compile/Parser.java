package magma.compile;

import magma.api.Option;
import magma.compile.ast.Ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Parser {
	public record Result(Ast.Program program, List<String> errors) {}

	private record StatementOrFinal(Option<Ast.Statement> statement, Option<Ast.Expression> finalExpression) {
		private static StatementOrFinal ofStatement(Ast.Statement statement) {
			return new StatementOrFinal(Option.of(statement), Option.empty());
		}

		private static StatementOrFinal ofFinal(Ast.Expression expression) {
			return new StatementOrFinal(Option.empty(), Option.of(expression));
		}
	}

	private final List<Token> tokens;
	private final List<String> errors = new ArrayList<>();
	private int current = 0;

	public Parser(List<Token> tokens) {
		this.tokens = Objects.requireNonNullElse(tokens, List.of());
	}

	public Result parseProgram() {
		List<Ast.IntrinsicDecl> intrinsics = new ArrayList<>();
		List<Ast.FunctionDecl> functions = new ArrayList<>();
		List<Ast.Statement> statements = new ArrayList<>();
		Option<Ast.Expression> finalExpression = Option.empty();

		while (!isAtEnd()) {
			if (match(TokenType.INTRINSIC)) {
				intrinsics.add(parseIntrinsic());
			} else if (match(TokenType.FN)) {
				functions.add(parseFunction());
			} else {
				StatementOrFinal sof = parseStatementOrFinal();
				if (sof.statement instanceof Option.Some<Ast.Statement>(Ast.Statement value)) {
					statements.add(value);
				}

				if (sof.finalExpression instanceof Option.Some<Ast.Expression>) {
					finalExpression = sof.finalExpression;
					if (!check(TokenType.EOF) && !check(TokenType.RIGHT_BRACE)) {
						error(peek(), "Unexpected tokens after final expression");
					}
					break;
				}
			}
		}

		return new Result(new Ast.Program(intrinsics, functions, statements, finalExpression), errors);
	}

	private Ast.IntrinsicDecl parseIntrinsic() {
		consume(TokenType.FN, "Expected 'fn' after 'intrinsic'");
		Token identifier = consume(TokenType.IDENTIFIER, "Expected intrinsic function name");
		consume(TokenType.LEFT_PAREN, "Expected '(' after intrinsic name");
		List<Ast.TypeRef> params = new ArrayList<>();
		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				params.add(parseTypeRef());
			} while (match(TokenType.COMMA));
		}
		consume(TokenType.RIGHT_PAREN, "Expected ')' after intrinsic parameters");
		Option<Ast.TypeRef> ret = Option.empty();
		if (match(TokenType.COLON)) {
			ret = Option.of(parseTypeRef());
		}
		consume(TokenType.SEMICOLON, "Expected ';' after intrinsic declaration");
		return new Ast.IntrinsicDecl(identifier.lexeme(), switch (ret) {
			case Option.Some(Ast.TypeRef value) -> value;
			case Option.None() -> new Ast.TypeRef("Void");
		}, params);
	}

	private Ast.FunctionDecl parseFunction() {
		String name = consume(TokenType.IDENTIFIER, "Expected function name").lexeme();
		consume(TokenType.LEFT_PAREN, "Expected '(' after function name");
		List<Ast.Parameter> parameters = new ArrayList<>();
		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				parameters.add(parseParameter());
			} while (match(TokenType.COMMA));
		}
		consume(TokenType.RIGHT_PAREN, "Expected ')' after parameter list");

		Option<Ast.TypeRef> returnType = Option.empty();
		if (match(TokenType.COLON)) {
			returnType = Option.of(parseTypeRef());
		}
		consume(TokenType.ARROW, "Expected '=>' before function body");

		Ast.Block body;
		if (match(TokenType.LEFT_BRACE)) {
			body = parseBlockContents();
		} else if (match(TokenType.RETURN)) {
			Ast.Statement stmt = parseReturnStatement();
			body = new Ast.Block(List.of(stmt), Option.empty());
		} else {
			Ast.Expression expr = parseExpression();
			consume(TokenType.SEMICOLON, "Expected ';' after function expression body");
			body = new Ast.Block(List.of(), Option.of(expr));
		}
		return new Ast.FunctionDecl(name, parameters, returnType, body);
	}

	private Ast.Parameter parseParameter() {
		String name = consume(TokenType.IDENTIFIER, "Expected parameter name").lexeme();
		consume(TokenType.COLON, "Expected ':' after parameter name");
		Ast.TypeRef type = parseTypeRef();
		return new Ast.Parameter(name, type);
	}

	private Ast.TypeRef parseTypeRef() {
		Token token = advance();
		return switch (token.type()) {
			case I32 -> new Ast.TypeRef("I32");
			case BOOL -> new Ast.TypeRef("Bool");
			case VOID -> new Ast.TypeRef("Void");
			case IDENTIFIER -> new Ast.TypeRef(token.lexeme());
			default -> {
				error(token, "Expected type");
				yield new Ast.TypeRef("I32");
			}
		};
	}

	private StatementOrFinal parseStatementOrFinal() {
		if (match(TokenType.LET)) {
			return StatementOrFinal.ofStatement(parseLet());
		}
		if (match(TokenType.RETURN)) {
			return StatementOrFinal.ofStatement(parseReturnStatement());
		}
		if (match(TokenType.WHILE)) {
			return StatementOrFinal.ofStatement(parseWhileStatement());
		}
		if (isIncrementStart()) {
			return StatementOrFinal.ofStatement(parseIncrementStatement());
		}
		if (isAssignmentStart()) {
			return StatementOrFinal.ofStatement(parseAssignmentStatement());
		}

		Ast.Expression expr = parseExpression();
		if (match(TokenType.SEMICOLON)) {
			return StatementOrFinal.ofStatement(new Ast.ExpressionStatement(expr));
		}
		return StatementOrFinal.ofFinal(expr);
	}

	private Ast.Statement parseLet() {
		boolean mutable = match(TokenType.MUT);
		String name = consume(TokenType.IDENTIFIER, "Expected identifier after let").lexeme();
		Option<Ast.TypeRef> type = Option.empty();
		if (match(TokenType.COLON)) {
			type = Option.of(parseTypeRef());
		}
		consume(TokenType.EQUAL, "Expected '=' in let statement");
		Ast.Expression initializer = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after let statement");
		return new Ast.LetStatement(mutable, name, type, initializer);
	}

	private Ast.Statement parseReturnStatement() {
		if (match(TokenType.SEMICOLON)) {
			return new Ast.ReturnStatement(Option.empty());
		}
		Ast.Expression expr = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after return statement");
		return new Ast.ReturnStatement(Option.of(expr));
	}

	private Ast.Statement parseWhileStatement() {
		consume(TokenType.LEFT_PAREN, "Expected '(' after while");
		Ast.Expression condition = parseExpression();
		consume(TokenType.RIGHT_PAREN, "Expected ')' after while condition");
		Ast.Statement body = parseLoopBody();
		return new Ast.WhileStatement(condition, body);
	}

	private Ast.Statement parseLoopBody() {
		if (match(TokenType.LEFT_BRACE)) {
			Ast.Block block = parseBlockContents();
			return new Ast.BlockStatement(block);
		}
		if (match(TokenType.LET)) {
			return parseLet();
		}
		if (match(TokenType.RETURN)) {
			return parseReturnStatement();
		}
		if (isIncrementStart()) {
			return parseIncrementStatement();
		}
		if (isAssignmentStart()) {
			return parseAssignmentStatement();
		}
		Ast.Expression expr = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after loop body expression");
		return new Ast.ExpressionStatement(expr);
	}

	private Ast.Statement parseAssignmentStatement() {
		String name = advance().lexeme();
		Token opToken = advance();
		Ast.AssignmentOp op = switch (opToken.type()) {
			case EQUAL -> Ast.AssignmentOp.ASSIGN;
			case PLUS_EQUAL -> Ast.AssignmentOp.PLUS_ASSIGN;
			case MINUS_EQUAL -> Ast.AssignmentOp.MINUS_ASSIGN;
			case STAR_EQUAL -> Ast.AssignmentOp.STAR_ASSIGN;
			case SLASH_EQUAL -> Ast.AssignmentOp.SLASH_ASSIGN;
			default -> {
				error(opToken, "Invalid assignment operator");
				yield Ast.AssignmentOp.ASSIGN;
			}
		};
		Ast.Expression expr = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after assignment");
		return new Ast.AssignmentStatement(name, op, expr);
	}

	private Ast.Statement parseIncrementStatement() {
		String name = advance().lexeme();
		consume(TokenType.INCREMENT, "Expected '++'");
		consume(TokenType.SEMICOLON, "Expected ';' after increment");
		return new Ast.IncrementStatement(name);
	}

	private Ast.Block parseBlockContents() {
		List<Ast.Statement> statements = new ArrayList<>();
		Option<Ast.Expression> result = Option.empty();
		while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
			StatementOrFinal sof = parseStatementOrFinal();
			if(sof.statement instanceof Option.Some<Ast.Statement>(Ast.Statement statement)) {
				statements.add(statement);
			}
			if (sof.finalExpression instanceof Option.Some<Ast.Expression>) {
				result = sof.finalExpression;
				break;
			}
		}
		consume(TokenType.RIGHT_BRACE, "Expected '}' to close block");
		return new Ast.Block(statements, result);
	}

	private boolean isAssignmentStart() {
		if (!check(TokenType.IDENTIFIER)) {
			return false;
		}
		TokenType next = peekNext().type();
		return next == TokenType.EQUAL || next == TokenType.PLUS_EQUAL || next == TokenType.MINUS_EQUAL ||
					 next == TokenType.STAR_EQUAL || next == TokenType.SLASH_EQUAL;
	}

	private boolean isIncrementStart() {
		if (!check(TokenType.IDENTIFIER)) {
			return false;
		}
		return peekNext().type() == TokenType.INCREMENT;
	}

	private Ast.Expression parseExpression() {
		return parseEquality();
	}

	private Ast.Expression parseEquality() {
		Ast.Expression expr = parseComparison();
		while (match(TokenType.DOUBLE_EQUAL)) {
			Ast.Expression right = parseComparison();
			expr = new Ast.BinaryExpression(expr, Ast.BinaryOperator.EQUALS, right);
		}
		return expr;
	}

	private Ast.Expression parseComparison() {
		Ast.Expression expr = parseTerm();
		while (match(TokenType.LESS)) {
			Ast.Expression right = parseTerm();
			expr = new Ast.BinaryExpression(expr, Ast.BinaryOperator.LESS, right);
		}
		return expr;
	}

	private Ast.Expression parseTerm() {
		Ast.Expression expr = parseFactor();
		while (true) {
			if (match(TokenType.PLUS)) {
				Ast.Expression right = parseFactor();
				expr = new Ast.BinaryExpression(expr, Ast.BinaryOperator.ADD, right);
			} else if (match(TokenType.MINUS)) {
				Ast.Expression right = parseFactor();
				expr = new Ast.BinaryExpression(expr, Ast.BinaryOperator.SUBTRACT, right);
			} else {
				break;
			}
		}
		return expr;
	}

	private Ast.Expression parseFactor() {
		Ast.Expression expr = parseUnary();
		while (match(TokenType.STAR)) {
			Ast.Expression right = parseUnary();
			expr = new Ast.BinaryExpression(expr, Ast.BinaryOperator.MULTIPLY, right);
		}
		return expr;
	}

	private Ast.Expression parseUnary() {
		if (match(TokenType.MINUS)) {
			Ast.Expression right = parseUnary();
			return new Ast.UnaryExpression(Ast.UnaryOperator.NEGATE, right);
		}
		return parseCall();
	}

	private Ast.Expression parseCall() {
		Ast.Expression expr = parsePrimary();
		while (true) {
			if (match(TokenType.LEFT_PAREN)) {
				expr = finishCall(expr);
			} else {
				break;
			}
		}
		return expr;
	}

	private Ast.Expression finishCall(Ast.Expression callee) {
		List<Ast.Expression> args = new ArrayList<>();
		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				args.add(parseExpression());
			} while (match(TokenType.COMMA));
		}
		consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments");
		if (callee instanceof Ast.IdentifierExpression(String name)) {
			return new Ast.CallExpression(name, args);
		}
		error(previous(), "Only function names can be called");
		return callee;
	}

	private Ast.Expression parsePrimary() {
		if (match(TokenType.NUMBER)) {
			String lexeme = previous().lexeme();
			int value = 0;
			try {
				value = Integer.parseInt(lexeme);
			} catch (NumberFormatException ex) {
				error(previous(), "Invalid integer literal: " + lexeme);
			}
			return new Ast.LiteralIntExpression(value);
		}
		if (match(TokenType.TRUE)) {
			return new Ast.LiteralBoolExpression(true);
		}
		if (match(TokenType.FALSE)) {
			return new Ast.LiteralBoolExpression(false);
		}
		if (match(TokenType.IDENTIFIER)) {
			return new Ast.IdentifierExpression(previous().lexeme());
		}
		if (match(TokenType.LEFT_PAREN)) {
			Ast.Expression expr = parseExpression();
			consume(TokenType.RIGHT_PAREN, "Expected ')' after expression");
			return expr;
		}
		if (match(TokenType.IF)) {
			return parseIfExpression();
		}
		if (match(TokenType.LEFT_BRACE)) {
			Ast.Block block = parseBlockContents();
			return new Ast.BlockExpression(block);
		}
		error(peek(), "Unexpected token in expression");
		return new Ast.LiteralIntExpression(0);
	}

	private Ast.Expression parseIfExpression() {
		consume(TokenType.LEFT_PAREN, "Expected '(' after if");
		Ast.Expression condition = parseExpression();
		consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");
		Ast.Expression thenExpr = parseExpression();
		consume(TokenType.ELSE, "Expected 'else' clause");
		Ast.Expression elseExpr = parseExpression();
		return new Ast.IfExpression(condition, thenExpr, elseExpr);
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) {
			return advance();
		}
		error(peek(), message);
		return new Token(type, "", peek().position());
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) {
			return type == TokenType.EOF;
		}
		return peek().type() == type;
	}

	private Token advance() {
		if (!isAtEnd()) {
			current++;
		}
		return previous();
	}

	private boolean isAtEnd() {
		return peek().type() == TokenType.EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token peekNext() {
		if (current + 1 >= tokens.size()) {
			return tokens.getLast();
		}
		return tokens.get(current + 1);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

	private void error(Token token, String message) {
		errors.add(message + " at position " + token.position());
	}
}
