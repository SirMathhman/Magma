package magma.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
	private final String source;
	private int position;
	private int line;
	private int column;

	public Lexer(String source) {
		this.source = source;
		this.position = 0;
		this.line = 1;
		this.column = 1;
	}

	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<>();

		while (position < source.length()) {
			char c = source.charAt(position);

			if (Character.isWhitespace(c)) {
				if (c == '\n') {
					line++;
					column = 1;
				} else {
					column++;
				}
				position++;
				continue;
			}

			if (Character.isDigit(c)) {
				tokens.add(scanNumber());
				continue;
			}

			switch (c) {
				case '+':
					tokens.add(new Token(TokenType.PLUS, "+", line, column));
					position++;
					column++;
					break;
				case '-':
					tokens.add(new Token(TokenType.MINUS, "-", line, column));
					position++;
					column++;
					break;
				case '*':
					tokens.add(new Token(TokenType.STAR, "*", line, column));
					position++;
					column++;
					break;
				case '/':
					tokens.add(new Token(TokenType.SLASH, "/", line, column));
					position++;
					column++;
					break;
				default:
					throw new RuntimeException("Unexpected character: " + c);
			}
		}

		tokens.add(new Token(TokenType.EOF, "", line, column));
		return tokens;
	}

	private Token scanNumber() {
		int startColumn = column;
		StringBuilder builder = new StringBuilder();

		while (position < source.length() && Character.isDigit(source.charAt(position))) {
			builder.append(source.charAt(position));
			position++;
			column++;
		}

		return new Token(TokenType.NUMBER, builder.toString(), line, startColumn);
	}
}
