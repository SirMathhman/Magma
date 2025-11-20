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

		// Optional type annotation: : Type
		Type typeAnnotation = null;
		if (match(TokenType.COLON)) {
			typeAnnotation = parseType();
		}

		consume(TokenType.ASSIGN, "Expected '=' after variable name");
		Node initializer = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
		return new VariableDeclaration(name, mutable, typeAnnotation, initializer);
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
			} else if (match(TokenType.LESS)) {
				// Generic type arguments: func<Type1, Type2>(...)
				if (expr instanceof Identifier) {
					Identifier identifier = (Identifier) expr;
					List<Type> typeArguments = new ArrayList<>();
					do {
						typeArguments.add(parseType());
					} while (match(TokenType.COMMA));
					consume(TokenType.GREATER, "Expected '>' after generic type arguments");
					// Continue to parse function call with type arguments
					if (match(TokenType.LPAREN)) {
						expr = finishFunctionCall(identifier, typeArguments);
					} else {
						throw new RuntimeException("Expected '(' after generic type arguments");
					}
				} else {
					throw new RuntimeException("Expected identifier before '<' for generic function call");
				}
			} else if (match(TokenType.LPAREN)) {
				if (expr instanceof Identifier) {
					expr = finishFunctionCall((Identifier) expr, null);
				} else {
					throw new RuntimeException("Expected identifier before '(' for function call");
				}
			} else {
				break;
			}
		}

		return expr;
	}

	private FunctionCall finishFunctionCall(Identifier identifier, List<Type> typeArguments) {
		List<Node> arguments = new ArrayList<>();

		if (!check(TokenType.RPAREN)) {
			do {
				arguments.add(parseExpression());
			} while (match(TokenType.COMMA));
		}

		consume(TokenType.RPAREN, "Expected ')' after arguments");
		return new FunctionCall(identifier.getName(), typeArguments, arguments);
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

		if (match(TokenType.SIZEOF)) {
			return parseSizeOfExpression();
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

	private SizeOfExpression parseSizeOfExpression() {
		consume(TokenType.LESS, "Expected '<' after SizeOf");
		Type type = parseType();
		consume(TokenType.GREATER, "Expected '>' after type in SizeOf");
		return new SizeOfExpression(type);
	}

	private Type parseType() {
		// Handle pointer types: *Type
		if (match(TokenType.STAR)) {
			Type baseType = parseType();
			return new PointerType(baseType);
		}

		// Handle array types: [Type; Length] or [Type; Start; End]
		if (match(TokenType.LBRACKET)) {
			return parseArrayType();
		}

		// Handle named types and generic types
		return parseNamedOrGenericType();
	}

	private Type parseNamedOrGenericType() {
		consume(TokenType.IDENTIFIER, "Expected type name");
		String name = previous().lexeme();

		// Check for generic type arguments: Type<Param1, Param2>
		if (match(TokenType.LT)) {
			return parseGenericType(name);
		}

		return new NamedType(name);
	}

	private GenericType parseGenericType(String baseName) {
		List<Type> typeArguments = new ArrayList<>();

		if (!check(TokenType.GREATER)) {
			do {
				typeArguments.add(parseType());
			} while (match(TokenType.COMMA));
		}

		consume(TokenType.GREATER, "Expected '>' after generic type arguments");
		return new GenericType(baseName, typeArguments);
	}

	private ArrayType parseArrayType() {
		Type elementType = parseType();
		consume(TokenType.SEMICOLON, "Expected ';' in array type");

		// Check for [Type; Start; End] syntax
		if (check(TokenType.NUMBER) || check(TokenType.IDENTIFIER)) {
			Node start = parseExpression();
			if (match(TokenType.SEMICOLON)) {
				// [Type; Start; End] syntax
				Node end = parseExpression();
				consume(TokenType.RBRACKET, "Expected ']' after array type");
				return new ArrayType(elementType, end, start);
			} else {
				// [Type; Length] syntax
				consume(TokenType.RBRACKET, "Expected ']' after array type");
				return new ArrayType(elementType, start);
			}
		} else {
			// [Type; Length] where Length might be an identifier
			Node length = parseExpression();
			consume(TokenType.RBRACKET, "Expected ']' after array type");
			return new ArrayType(elementType, length);
		}
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
