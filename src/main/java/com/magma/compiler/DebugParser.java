package com.magma.compiler;

import com.magma.compiler.ast.Expr;
import com.magma.compiler.ast.Stmt;
import com.magma.compiler.lexer.Lexer;
import com.magma.compiler.lexer.MagmaLexer;
import com.magma.compiler.lexer.Token;
import com.magma.compiler.lexer.TokenType;
import com.magma.compiler.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A debug version of the parser that adds detailed error reporting.
 */
public class DebugParser implements Parser {
	private final Lexer lexer;
	private Token currentToken;
	private Token previousToken;

	public DebugParser(Lexer lexer) {
		this.lexer = lexer;
		// Prime the pump with the first token
		advance();
	}

	/**
	 * Main method for testing the debug parser.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: java com.magma.compiler.DebugParser <script>");
			System.exit(64);
		}

		String path = args[0];
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		String source = new String(bytes, Charset.defaultCharset());

		System.out.println("Source code:");
		System.out.println(source);
		System.out.println();

		// Create the lexer and tokenize the source
		MagmaLexer lexer = new MagmaLexer(source);
		List<Token> tokens = lexer.tokenize();

		// Print the tokens
		System.out.println("Tokens:");
		for (Token token : tokens) {
			System.out.println(token);
		}
		System.out.println();

		// Create a new lexer for the parser (since the first one has already consumed all tokens)
		Lexer parserLexer = new MagmaLexer(source);

		// Create the debug parser
		DebugParser parser = new DebugParser(parserLexer);

		try {
			// Parse the source code
			List<Stmt> statements = parser.parse();

			// Print the number of statements
			System.out.println("Parsed " + statements.size() + " statements.");

			// Print the statements (using toString)
			if (!statements.isEmpty()) {
				System.out.println("Statements:");
				for (Stmt stmt : statements) {
					System.out.println(stmt);
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing source: " + e.getMessage());
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	@Override
	public List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();

		System.out.println("Starting to parse...");

		while (isNotAtEnd()) {
			System.out.println("Current token: " + currentToken);
			try {
				Stmt stmt = parseDeclaration();
				if (stmt != null) {
					statements.add(stmt);
					System.out.println("Successfully parsed statement: " + stmt);
				} else {
					System.out.println("Failed to parse statement, got null");
				}
			} catch (Exception e) {
				System.err.println("Error parsing statement: " + e.getMessage());
				//noinspection CallToPrintStackTrace
				e.printStackTrace();
				synchronize();
			}
		}

		return statements;
	}

	@Override
	public Expr parseExpression() {
		// This is a stub implementation, as we're only interested in debugging the parser
		System.out.println("parseExpression() called, but not implemented in DebugParser");
		return null;
	}

	@Override
	public Stmt parseStatement() {
		// This is a stub implementation, as we're only interested in debugging the parser
		System.out.println("parseStatement() called, but not implemented in DebugParser");
		return null;
	}

	@Override
	public Stmt parseDeclaration() {
		// This is a stub implementation, as we're only interested in debugging the parser
		System.out.println("parseDeclaration() called, but not implemented in DebugParser");
		return null;
	}

	private boolean isNotAtEnd() {
		return currentToken.getType() != TokenType.EOF;
	}

	private void advance() {
		previousToken = currentToken;
		currentToken = lexer.nextToken();
		System.out.println("Advanced to token: " + currentToken);
	}

	private void synchronize() {
		System.out.println("Synchronizing after error...");
		advance();

		while (isNotAtEnd()) {
			if (previousToken.getType() == TokenType.SEMICOLON) {
				System.out.println("Found semicolon, synchronization complete");
				return;
			}

			switch (currentToken.getType()) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					System.out.println("Found statement start token: " + currentToken.getType() + ", synchronization complete");
					return;
			}

			advance();
		}

		System.out.println("Reached end of file during synchronization");
	}
}