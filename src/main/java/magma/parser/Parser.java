package magma.parser;

import magma.ast.*;
import magma.lexer.Token;
import magma.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private final List<Token> tokens;
	private int position;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.position = 0;
	}

	public Program parse() {
		List<ImportStatement> imports = new ArrayList<>();
		List<Statement> statements = new ArrayList<>();

		// Parse imports
		while (match(TokenType.IMPORT)) {
			imports.add(parseImport());
		}

		// Parse statements
		while (!isAtEnd()) {
			statements.add(parseStatement());
		}

		return new Program(imports, statements);
	}

	private ImportStatement parseImport() {
		boolean isExtern = match(TokenType.EXTERN);
		consume(TokenType.IDENTIFIER, "Expected module name");
		String module = previous().lexeme();
		consume(TokenType.SEMICOLON, "Expected ';' after import");
		return new ImportStatement(module, isExtern);
	}

	private Statement parseStatement() {
		if (match(TokenType.LET)) {
			return parseVariableDeclaration();
		}
		if (match(TokenType.FOR)) {
			return parseForLoop();
		}
		if (match(TokenType.LBRACE)) {
			return parseBlock();
		}

		// Try to parse as expression statement (assignment, function call, etc.)
		Node expr = parseExpression();
		if (expr instanceof Assignment) {
			consume(TokenType.SEMICOLON, "Expected ';' after statement");
			return (Assignment) expr;
		}
		if (expr instanceof FunctionCall) {
			consume(TokenType.SEMICOLON, "Expected ';' after statement");
			return new ExpressionStatement((FunctionCall) expr);
		}

		consume(TokenType.SEMICOLON, "Expected ';' after statement");
		return new ExpressionStatement(expr);
	}

	private VariableDeclaration parseVariableDeclaration() {
		boolean mutable = match(TokenType.MUT);
		consume(TokenType.IDENTIFIER, "Expected variable name");
		String name = previous().lexeme();
		consume(TokenType.ASSIGN, "Expected '=' after variable name");
		Node initializer = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
		return new VariableDeclaration(name, mutable, initializer);
	}

	private ForLoop parseForLoop() {
		consume(TokenType.LPAREN, "Expected '(' after 'for'");
		consume(TokenType.LET, "Expected 'let' in for loop");
		boolean mutable = match(TokenType.MUT);
		consume(TokenType.IDENTIFIER, "Expected variable name in for loop");
		String variableName = previous().lexeme();
		consume(TokenType.IN, "Expected 'in' in for loop");
		Node start = parseExpression();
		consume(TokenType.RANGE, "Expected '..' in for loop");
		Node end = parseExpression();
		consume(TokenType.RPAREN, "Expected ')' after for loop header");
		Block body = parseBlock();
		return new ForLoop(variableName, mutable, start, end, body);
	}

	private Block parseBlock() {
		if (!check(TokenType.LBRACE)) {
			// If we're already past the opening brace, we need to handle it
			// This is a bit of a hack, but it works for the for loop case
		} else {
			consume(TokenType.LBRACE, "Expected '{'");
		}

		List<Statement> statements = new ArrayList<>();
		while (!check(TokenType.RBRACE) && !isAtEnd()) {
			statements.add(parseStatement());
		}

		consume(TokenType.RBRACE, "Expected '}' after block");
		return new Block(statements);
	}

	private Node parseExpression() {
		return parseAssignment();
	}

	private Node parseAssignment() {
		Node expr = parseAdditive();

		if (match(TokenType.ASSIGN)) {
			Node value = parseAssignment(); // Right-associative
			return new Assignment(expr, value);
		}

		return expr;
	}

	private Node parseAdditive() {
		Node left = parseMultiplicative();

		while (match(TokenType.PLUS, TokenType.MINUS)) {
			Token operator = previous();
			Node right = parseMultiplicative();
			left = new BinaryExpression(left, operator, right);
		}

		return left;
	}

	private Node parseMultiplicative() {
		Node left = parsePostfix();

		while (match(TokenType.STAR, TokenType.SLASH)) {
			Token operator = previous();
			Node right = parsePostfix();
			left = new BinaryExpression(left, operator, right);
		}

		return left;
	}

	private Node parsePostfix() {
		Node expr = parsePrimary();

		while (true) {
			if (match(TokenType.LBRACKET)) {
				Node index = parseExpression();
				consume(TokenType.RBRACKET, "Expected ']' after array index");
				expr = new ArrayIndex(expr, index);
			} else if (match(TokenType.LPAREN)) {
				if (expr instanceof Identifier) {
					expr = finishFunctionCall((Identifier) expr);
				} else {
					throw new RuntimeException("Expected identifier before '(' for function call");
				}
			} else {
				break;
			}
		}

		return expr;
	}

	private FunctionCall finishFunctionCall(Identifier identifier) {
		List<Node> arguments = new ArrayList<>();

		if (!check(TokenType.RPAREN)) {
			do {
				arguments.add(parseExpression());
			} while (match(TokenType.COMMA));
		}

		consume(TokenType.RPAREN, "Expected ')' after arguments");
		return new FunctionCall(identifier.getName(), arguments);
	}

	private Node parsePrimary() {
		if (match(TokenType.NUMBER)) {
			Token token = previous();
			int value = Integer.parseInt(token.lexeme());
			return new NumberLiteral(value);
		}

		if (match(TokenType.STRING)) {
			Token token = previous();
			return new StringLiteral(token.lexeme());
		}

		if (match(TokenType.IDENTIFIER)) {
			Token token = previous();
			return new Identifier(token.lexeme());
		}

		if (match(TokenType.LPAREN)) {
			Node expr = parseExpression();
			consume(TokenType.RPAREN, "Expected ')' after expression");
			return expr;
		}

		throw new RuntimeException("Expected expression at line " + peek().line() + ", column " + peek().column());
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) {
			return advance();
		}
		throw new RuntimeException(message + " at line " + peek().line() + ", column " + peek().column());
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

	private boolean check(TokenType type) {
		if (isAtEnd()) {
			return false;
		}
		return peek().type() == type;
	}

	private Token advance() {
		if (!isAtEnd()) {
			position++;
		}
		return previous();
	}

	private boolean isAtEnd() {
		return peek().type() == TokenType.EOF;
	}

	private Token peek() {
		return tokens.get(position);
	}

	private Token previous() {
		return tokens.get(position - 1);
	}
}
