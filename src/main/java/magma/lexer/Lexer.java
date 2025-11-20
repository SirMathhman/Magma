package magma.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
	private final String source;
	private int position;
	private int line;
	private int column;

	private static final Map<String, TokenType> keywords = new HashMap<>();

	static {
		keywords.put("let", TokenType.LET);
		keywords.put("mut", TokenType.MUT);
		keywords.put("for", TokenType.FOR);
		keywords.put("in", TokenType.IN);
		keywords.put("import", TokenType.IMPORT);
		keywords.put("extern", TokenType.EXTERN);
		keywords.put("fn", TokenType.FN);
		keywords.put("type", TokenType.TYPE);
		keywords.put("impl", TokenType.IMPL);
		keywords.put("trait", TokenType.TRAIT);
		keywords.put("intrinsic", TokenType.INTRINSIC);
		keywords.put("if", TokenType.IF);
		keywords.put("else", TokenType.ELSE);
		keywords.put("return", TokenType.RETURN);
		keywords.put("Void", TokenType.VOID);
	}

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

			// Handle comments
			if (c == '/' && position + 1 < source.length()) {
				char next = source.charAt(position + 1);
				if (next == '/') {
					scanLineComment();
					continue;
				} else if (next == '*') {
					scanBlockComment();
					continue;
				}
			}

			if (Character.isDigit(c)) {
				tokens.add(scanNumber());
				continue;
			}

			if (Character.isLetter(c) || c == '_') {
				tokens.add(scanIdentifier());
				continue;
			}

			if (c == '"') {
				tokens.add(scanString());
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
				case '=':
					if (position + 1 < source.length()) {
						char next = source.charAt(position + 1);
						if (next == '=') {
							tokens.add(new Token(TokenType.EQUAL, "==", line, column));
							position += 2;
							column += 2;
						} else if (next == '>') {
							tokens.add(new Token(TokenType.ARROW, "=>", line, column));
							position += 2;
							column += 2;
						} else {
							tokens.add(new Token(TokenType.ASSIGN, "=", line, column));
							position++;
							column++;
						}
					} else {
						tokens.add(new Token(TokenType.ASSIGN, "=", line, column));
						position++;
						column++;
					}
					break;
				case '!':
					if (position + 1 < source.length() && source.charAt(position + 1) == '=') {
						tokens.add(new Token(TokenType.NOT_EQUAL, "!=", line, column));
						position += 2;
						column += 2;
					} else {
						throw new RuntimeException("Unexpected character: " + c + " at line " + line + ", column " + column);
					}
					break;
				case '<':
					if (position + 1 < source.length() && source.charAt(position + 1) == '=') {
						tokens.add(new Token(TokenType.LESS_EQUAL, "<=", line, column));
						position += 2;
						column += 2;
					} else {
						tokens.add(new Token(TokenType.LESS, "<", line, column));
						position++;
						column++;
					}
					break;
				case '>':
					if (position + 1 < source.length() && source.charAt(position + 1) == '=') {
						tokens.add(new Token(TokenType.GREATER_EQUAL, ">=", line, column));
						position += 2;
						column += 2;
					} else {
						tokens.add(new Token(TokenType.GREATER, ">", line, column));
						position++;
						column++;
					}
					break;
				case '|':
					tokens.add(new Token(TokenType.PIPE, "|", line, column));
					position++;
					column++;
					break;
				case '&':
					tokens.add(new Token(TokenType.AMPERSAND, "&", line, column));
					position++;
					column++;
					break;
				case '[':
					tokens.add(new Token(TokenType.LBRACKET, "[", line, column));
					position++;
					column++;
					break;
				case ']':
					tokens.add(new Token(TokenType.RBRACKET, "]", line, column));
					position++;
					column++;
					break;
				case '{':
					tokens.add(new Token(TokenType.LBRACE, "{", line, column));
					position++;
					column++;
					break;
				case '}':
					tokens.add(new Token(TokenType.RBRACE, "}", line, column));
					position++;
					column++;
					break;
				case '(':
					tokens.add(new Token(TokenType.LPAREN, "(", line, column));
					position++;
					column++;
					break;
				case ')':
					tokens.add(new Token(TokenType.RPAREN, ")", line, column));
					position++;
					column++;
					break;
				case ':':
					tokens.add(new Token(TokenType.COLON, ":", line, column));
					position++;
					column++;
					break;
				case ';':
					tokens.add(new Token(TokenType.SEMICOLON, ";", line, column));
					position++;
					column++;
					break;
				case ',':
					tokens.add(new Token(TokenType.COMMA, ",", line, column));
					position++;
					column++;
					break;
				case '.':
					if (position + 1 < source.length() && source.charAt(position + 1) == '.') {
						tokens.add(new Token(TokenType.RANGE, "..", line, column));
						position += 2;
						column += 2;
					} else {
						tokens.add(new Token(TokenType.DOT, ".", line, column));
						position++;
						column++;
					}
					break;
				default:
					throw new RuntimeException("Unexpected character: " + c + " at line " + line + ", column " + column);
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

	private Token scanIdentifier() {
		int startColumn = column;
		StringBuilder builder = new StringBuilder();

		while (position < source.length() && (Character.isLetterOrDigit(source.charAt(position)) || source.charAt(position) == '_')) {
			builder.append(source.charAt(position));
			position++;
			column++;
		}

		String text = builder.toString();
		TokenType type = keywords.get(text);
		if (type != null) {
			return new Token(type, text, line, startColumn);
		}

		return new Token(TokenType.IDENTIFIER, text, line, startColumn);
	}

	private Token scanString() {
		int startColumn = column;
		StringBuilder builder = new StringBuilder();
		position++; // Skip opening quote
		column++;

		while (position < source.length()) {
			char c = source.charAt(position);
			if (c == '"') {
				position++;
				column++;
				break;
			}
			if (c == '\\' && position + 1 < source.length()) {
				char next = source.charAt(position + 1);
				switch (next) {
					case 'n':
						builder.append('\n');
						break;
					case 't':
						builder.append('\t');
						break;
					case 'r':
						builder.append('\r');
						break;
					case '\\':
						builder.append('\\');
						break;
					case '"':
						builder.append('"');
						break;
					default:
						builder.append(next);
						break;
				}
				position += 2;
				column += 2;
			} else {
				if (c == '\n') {
					line++;
					column = 1;
				} else {
					column++;
				}
				builder.append(c);
				position++;
			}
		}

		return new Token(TokenType.STRING, builder.toString(), line, startColumn);
	}

	private void scanLineComment() {
		while (position < source.length() && source.charAt(position) != '\n') {
			position++;
			column++;
		}
	}

	private void scanBlockComment() {
		position += 2; // Skip /*
		column += 2;

		while (position < source.length()) {
			if (position + 1 < source.length() && source.charAt(position) == '*' && source.charAt(position + 1) == '/') {
				position += 2;
				column += 2;
				return;
			}
			if (source.charAt(position) == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
			position++;
		}
	}
}
