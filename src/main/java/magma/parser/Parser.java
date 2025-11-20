package magma.parser;

import magma.ast.BinaryExpression;
import magma.ast.Node;
import magma.ast.NumberLiteral;
import magma.lexer.Token;
import magma.lexer.TokenType;

import java.util.List;

public class Parser {
	private final List<Token> tokens;
	private int position;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		this.position = 0;
	}

	public Node parse() {
		return parseExpression();
	}

	private Node parseExpression() {
		return parseAdditive();
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
		Node left = parsePrimary();

		while (match(TokenType.STAR, TokenType.SLASH)) {
			Token operator = previous();
			Node right = parsePrimary();
			left = new BinaryExpression(left, operator, right);
		}

		return left;
	}

	private Node parsePrimary() {
		if (match(TokenType.NUMBER)) {
			Token token = previous();
			int value = Integer.parseInt(token.lexeme());
			return new NumberLiteral(value);
		}

		throw new RuntimeException("Expected expression");
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
