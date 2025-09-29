package magma.compiler;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
	private final String input;
	private int pos = 0;

	public Lexer(String input) {
		this.input = input;
	}

	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<>();
		while (true) {
			skipWhitespace();
			if (eof()) {
				tokens.add(new Token(TokenType.EOF, ""));
				break;
			}
			char c = peek();
			if (c == ';') {
				tokens.add(new Token(TokenType.SEMICOLON, ";"));
				pos++;
				continue;
			}
			if (c == '"') {
				tokens.add(readString());
				continue;
			}
			if (Character.isDigit(c)) {
				tokens.add(readNumber());
				continue;
			}
			if (isAlpha(c)) {
				tokens.add(readIdentifier());
				continue;
			}
			// unknown
			tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c)));
			pos++;
		}
		return tokens;
	}

	private Token readIdentifier() {
		int start = pos;
		while (!eof() && isAlphaNumeric(peek()))
			pos++;
		String s = input.substring(start, pos);
		if (s.equals("print"))
			return new Token(TokenType.PRINT, s);
		return new Token(TokenType.UNKNOWN, s);
	}

	private Token readNumber() {
		int start = pos;
		while (!eof() && Character.isDigit(peek()))
			pos++;
		return new Token(TokenType.NUMBER, input.substring(start, pos));
	}

	private Token readString() {
		// consume opening quote
		pos++;
		int start = pos;
		while (!eof() && peek() != '"')
			pos++;
		String s = input.substring(start, pos);
		if (!eof() && peek() == '"')
			pos++; // consume closing
		return new Token(TokenType.STRING, s);
	}

	private void skipWhitespace() {
		while (!eof() && Character.isWhitespace(peek()))
			pos++;
	}

	private char peek() {
		return input.charAt(pos);
	}

	private boolean eof() {
		return pos >= input.length();
	}

	private boolean isAlpha(char c) {
		return Character.isLetter(c);
	}

	private boolean isAlphaNumeric(char c) {
		return Character.isLetterOrDigit(c);
	}
}
