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
		List<TypeDefinition> typeDefinitions = new ArrayList<>();
		List<TraitDefinition> traits = new ArrayList<>();
		List<TraitImplementation> traitImplementations = new ArrayList<>();
		List<FunctionDefinition> functions = new ArrayList<>();
		List<ExternFunctionDeclaration> externFunctions = new ArrayList<>();
		List<Statement> statements = new ArrayList<>();

		// Parse imports
		while (match(TokenType.IMPORT)) {
			imports.add(parseImport());
		}

		// Parse type definitions (after imports, before traits)
		while (match(TokenType.TYPE)) {
			typeDefinitions.add(parseTypeDefinition());
		}

		// Parse traits (after type definitions, before implementations)
		while (check(TokenType.INTRINSIC) && position + 1 < tokens.size() && tokens.get(position + 1).type() == TokenType.TRAIT) {
			advance(); // consume INTRINSIC
			advance(); // consume TRAIT
			traits.add(parseTraitDefinition(true));
		}
		while (check(TokenType.TRAIT)) {
			advance(); // consume TRAIT
			traits.add(parseTraitDefinition(false));
		}

		// Parse trait implementations (after traits, before functions)
		while (match(TokenType.IMPL)) {
			traitImplementations.add(parseTraitImplementation());
		}

		// Parse functions, extern functions, and statements
		while (!isAtEnd()) {
			if (check(TokenType.EXTERN) && position + 1 < tokens.size() && tokens.get(position + 1).type() == TokenType.FN) {
				// extern fn ...
				advance(); // consume EXTERN
				advance(); // consume FN
				externFunctions.add(parseExternFunctionDeclaration());
			} else if (match(TokenType.FN)) {
				functions.add(parseFunctionDefinition());
			} else {
				statements.add(parseStatement());
			}
		}

		return new Program(imports, typeDefinitions, traits, traitImplementations, functions, externFunctions, statements);
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
		if (match(TokenType.RETURN)) {
			return parseReturnStatement();
		}
		if (match(TokenType.IF)) {
			return parseIfStatement();
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

	private Statement parseReturnStatement() {
		// return expr; or return;
		if (check(TokenType.SEMICOLON)) {
			consume(TokenType.SEMICOLON, "Expected ';' after return");
			return new ReturnStatement();
		}
		Node expr = parseExpression();
		consume(TokenType.SEMICOLON, "Expected ';' after return statement");
		return new ReturnStatement(expr);
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

		if (match(TokenType.IF)) {
			return parseIfExpression();
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
		// Parse base type first
		Type baseType = parseBaseType();
		
		// Check for union types: Type | Type | Type
		if (match(TokenType.PIPE)) {
			List<Type> variants = new ArrayList<>();
			variants.add(baseType);
			do {
				variants.add(parseBaseType());
			} while (match(TokenType.PIPE));
			return new UnionType(variants);
		}
		
		return baseType;
	}

	private Type parseBaseType() {
		// Handle pointer types: *Type
		if (match(TokenType.STAR)) {
			Type baseType = parseBaseType();
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
		// Accept IDENTIFIER or VOID keyword as type name
		if (!match(TokenType.IDENTIFIER) && !match(TokenType.VOID)) {
			throw new RuntimeException("Expected type name at line " + peek().line() + ", column " + peek().column());
		}
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

	private FunctionDefinition parseFunctionDefinition() {
		// fn name<TypeArgs>(params) : ReturnType { body } or fn name(params) => expr;
		consume(TokenType.IDENTIFIER, "Expected function name");
		String name = previous().lexeme();

		// Parse generic type arguments: <Type1, Type2> or <Type1, Param : Constraint>
		List<Type> typeArguments = null;
		if (match(TokenType.LESS)) {
			typeArguments = new ArrayList<>();
			if (!check(TokenType.GREATER)) {
				do {
					// Parse either a type or a generic parameter with constraint
					if (check(TokenType.IDENTIFIER) && position + 1 < tokens.size() && tokens.get(position + 1).type() == TokenType.COLON) {
						// Generic parameter with constraint: Param : Constraint
						consume(TokenType.IDENTIFIER, "Expected parameter name");
						String paramName = previous().lexeme();
						consume(TokenType.COLON, "Expected ':' after parameter name");
						Type constraint = parseType();
						// Store as NamedType for now (constraint is ignored in AST)
						typeArguments.add(new NamedType(paramName));
					} else {
						// Regular type
						typeArguments.add(parseType());
					}
				} while (match(TokenType.COMMA));
			}
			consume(TokenType.GREATER, "Expected '>' after generic type arguments");
		}

		// Parse parameters: (param1 : Type1, param2 : Type2)
		consume(TokenType.LPAREN, "Expected '(' after function name");
		List<FunctionParameter> parameters = parseFunctionParameters();
		consume(TokenType.RPAREN, "Expected ')' after parameters");

		// Parse optional return type: : ReturnType
		Type returnType = null;
		if (match(TokenType.COLON)) {
			returnType = parseType();
		}

		// Parse function body: { body } or => expr;
		Node body;
		if (match(TokenType.ARROW)) {
			// Arrow syntax: => expr; or => { statements }
			if (match(TokenType.LBRACE)) {
				body = parseBlock();
			} else {
				Node expr = parseExpression();
				consume(TokenType.SEMICOLON, "Expected ';' after arrow expression");
				body = new Block(List.of(new ExpressionStatement(expr)));
			}
		} else {
			// Block syntax: { body }
			body = parseBlock();
		}

		if (typeArguments != null) {
			return new FunctionDefinition(name, typeArguments, parameters, returnType, body);
		} else {
			return new FunctionDefinition(name, parameters, returnType, body);
		}
	}

	private ExternFunctionDeclaration parseExternFunctionDeclaration() {
		// extern fn name<TypeArgs>(params) : ReturnType;
		consume(TokenType.IDENTIFIER, "Expected function name");
		String name = previous().lexeme();

		// Parse generic type arguments: <Type1, Type2> or <Type1, Param : Constraint>
		List<Type> typeArguments = null;
		if (match(TokenType.LESS)) {
			typeArguments = new ArrayList<>();
			if (!check(TokenType.GREATER)) {
				do {
					// Parse either a type or a generic parameter with constraint
					if (check(TokenType.IDENTIFIER) && position + 1 < tokens.size() && tokens.get(position + 1).type() == TokenType.COLON) {
						// Generic parameter with constraint: Param : Constraint
						consume(TokenType.IDENTIFIER, "Expected parameter name");
						String paramName = previous().lexeme();
						consume(TokenType.COLON, "Expected ':' after parameter name");
						Type constraint = parseType();
						// Store as NamedType for now (constraint is ignored in AST)
						typeArguments.add(new NamedType(paramName));
					} else {
						// Regular type
						typeArguments.add(parseType());
					}
				} while (match(TokenType.COMMA));
			}
			consume(TokenType.GREATER, "Expected '>' after generic type arguments");
		}

		// Parse parameters: (param1 : Type1, param2 : Type2, ...args : Type)
		consume(TokenType.LPAREN, "Expected '(' after function name");
		List<FunctionParameter> parameters = parseFunctionParameters();
		consume(TokenType.RPAREN, "Expected ')' after parameters");

		// Parse optional return type: : ReturnType
		Type returnType = null;
		if (match(TokenType.COLON)) {
			returnType = parseType();
		}

		consume(TokenType.SEMICOLON, "Expected ';' after extern function declaration");

		if (typeArguments != null) {
			return new ExternFunctionDeclaration(name, typeArguments, parameters, returnType);
		} else {
			return new ExternFunctionDeclaration(name, parameters, returnType);
		}
	}

	private List<FunctionParameter> parseFunctionParameters() {
		List<FunctionParameter> parameters = new ArrayList<>();

		if (!check(TokenType.RPAREN)) {
			do {
				// Handle varargs: ...args (check for three consecutive dots)
				boolean isVarargs = check(TokenType.DOT) && 
					position + 1 < tokens.size() && tokens.get(position + 1).type() == TokenType.DOT &&
					position + 2 < tokens.size() && tokens.get(position + 2).type() == TokenType.DOT;
				if (isVarargs) {
					advance(); // consume first DOT
					advance(); // consume second DOT
					advance(); // consume third DOT
					consume(TokenType.IDENTIFIER, "Expected parameter name after '...'");
					String name = previous().lexeme();
					consume(TokenType.COLON, "Expected ':' after varargs parameter name");
					Type type = parseType();
					parameters.add(new FunctionParameter(name, type));
				} else {
					parameters.add(parseFunctionParameter());
				}
			} while (match(TokenType.COMMA));
		}

		return parameters;
	}

	private FunctionParameter parseFunctionParameter() {
		// name : Type or just name
		consume(TokenType.IDENTIFIER, "Expected parameter name");
		String name = previous().lexeme();

		// Optional type annotation
		if (match(TokenType.COLON)) {
			Type type = parseType();
			return new FunctionParameter(name, type);
		}

		return new FunctionParameter(name);
	}

	private TypeDefinition parseTypeDefinition() {
		// type name<params> = Type;
		consume(TokenType.IDENTIFIER, "Expected type name");
		String name = previous().lexeme();

		// Parse generic parameters: <Param1, Param2 : Constraint, Param3>
		List<GenericParameter> genericParameters = null;
		if (match(TokenType.LESS)) {
			genericParameters = parseGenericParameters();
			consume(TokenType.GREATER, "Expected '>' after generic parameters");
		}

		// Parse = Type
		consume(TokenType.ASSIGN, "Expected '=' after type name");
		Type aliasedType = parseType();
		consume(TokenType.SEMICOLON, "Expected ';' after type definition");

		if (genericParameters != null) {
			return new TypeDefinition(name, genericParameters, aliasedType);
		} else {
			return new TypeDefinition(name, aliasedType);
		}
	}

	private List<GenericParameter> parseGenericParameters() {
		List<GenericParameter> parameters = new ArrayList<>();

		if (!check(TokenType.GREATER)) {
			do {
				parameters.add(parseGenericParameter());
			} while (match(TokenType.COMMA));
		}

		return parameters;
	}

	private GenericParameter parseGenericParameter() {
		// name : Constraint or just name
		consume(TokenType.IDENTIFIER, "Expected generic parameter name");
		String name = previous().lexeme();

		// Optional constraint: : Constraint
		if (match(TokenType.COLON)) {
			Type constraint = parseType();
			return new GenericParameter(name, constraint);
		}

		return new GenericParameter(name);
	}

	private IfExpression parseIfExpression() {
		// if (condition) expr else expr
		consume(TokenType.LPAREN, "Expected '(' after 'if'");
		Node condition = parseExpression();
		consume(TokenType.RPAREN, "Expected ')' after condition");
		Node thenExpr = parseExpression();
		consume(TokenType.ELSE, "Expected 'else' in if expression");
		Node elseExpr = parseExpression();
		return new IfExpression(condition, thenExpr, elseExpr);
	}

	private IfStatement parseIfStatement() {
		// if (condition) statement else statement
		consume(TokenType.LPAREN, "Expected '(' after 'if'");
		Node condition = parseExpression();
		consume(TokenType.RPAREN, "Expected ')' after condition");
		Statement thenStmt = parseStatement();
		Statement elseStmt = null;
		if (match(TokenType.ELSE)) {
			elseStmt = parseStatement();
		}
		if (elseStmt != null) {
			return new IfStatement(condition, thenStmt, elseStmt);
		} else {
			return new IfStatement(condition, thenStmt);
		}
	}

	private TraitDefinition parseTraitDefinition(boolean isIntrinsic) {
		// trait Name<Params> { methods } or intrinsic trait Name<Params> { methods }
		consume(TokenType.IDENTIFIER, "Expected trait name");
		String name = previous().lexeme();

		// Parse generic parameters: <Param1, Param2 : Constraint, Param3>
		List<GenericParameter> genericParameters = null;
		if (match(TokenType.LESS)) {
			genericParameters = parseGenericParameters();
			consume(TokenType.GREATER, "Expected '>' after generic parameters");
		}

		// Parse trait body: { method signatures }
		consume(TokenType.LBRACE, "Expected '{' after trait name");
		List<FunctionDefinition> methods = new ArrayList<>();
		while (!check(TokenType.RBRACE) && !isAtEnd()) {
			consume(TokenType.FN, "Expected 'fn' in trait definition");
			methods.add(parseTraitMethodSignature());
		}
		consume(TokenType.RBRACE, "Expected '}' after trait definition");

		if (genericParameters != null) {
			return new TraitDefinition(name, genericParameters, methods, isIntrinsic);
		} else {
			return new TraitDefinition(name, null, methods, isIntrinsic);
		}
	}

	private FunctionDefinition parseTraitMethodSignature() {
		// fn name(params) : ReturnType; (no body, just signature)
		consume(TokenType.IDENTIFIER, "Expected method name");
		String name = previous().lexeme();

		// Parse generic type arguments: <Type1, Type2>
		List<Type> typeArguments = null;
		if (match(TokenType.LESS)) {
			typeArguments = new ArrayList<>();
			if (!check(TokenType.GREATER)) {
				do {
					typeArguments.add(parseType());
				} while (match(TokenType.COMMA));
			}
			consume(TokenType.GREATER, "Expected '>' after generic type arguments");
		}

		// Parse parameters: (param1 : Type1, param2 : Type2)
		consume(TokenType.LPAREN, "Expected '(' after method name");
		List<FunctionParameter> parameters = parseFunctionParameters();
		consume(TokenType.RPAREN, "Expected ')' after parameters");

		// Parse optional return type: : ReturnType
		Type returnType = null;
		if (match(TokenType.COLON)) {
			returnType = parseType();
		}

		consume(TokenType.SEMICOLON, "Expected ';' after trait method signature");

		// Trait method signatures have no body
		if (typeArguments != null) {
			return new FunctionDefinition(name, typeArguments, parameters, returnType, null);
		} else {
			return new FunctionDefinition(name, parameters, returnType, null);
		}
	}

	private TraitImplementation parseTraitImplementation() {
		// impl TraitName for Type { methods }
		consume(TokenType.IDENTIFIER, "Expected trait name");
		String traitName = previous().lexeme();

		consume(TokenType.FOR, "Expected 'for' after trait name");
		Type implementingType = parseType();

		// Parse implementation body: { method implementations }
		consume(TokenType.LBRACE, "Expected '{' after implementing type");
		List<FunctionDefinition> methods = new ArrayList<>();
		while (!check(TokenType.RBRACE) && !isAtEnd()) {
			consume(TokenType.FN, "Expected 'fn' in trait implementation");
			methods.add(parseFunctionDefinition());
		}
		consume(TokenType.RBRACE, "Expected '}' after trait implementation");

		return new TraitImplementation(traitName, implementingType, methods);
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
