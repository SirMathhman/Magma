package magma.compiler;

import magma.compiler.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private final List<Token> tokens;
	private int pos = 0;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	public magma.compiler.ast.Program parse() {
		List<Stmt> stmts = new ArrayList<>();
		while (!match(TokenType.EOF)) {
			Option<Stmt> sOpt = parseStatement();
			if (sOpt.isPresent()) {
				stmts.add(sOpt.get());
			}
		}
		return new magma.compiler.ast.Program(stmts);
	}

	private Option<Stmt> parseStatement() {
		if (match(TokenType.PRINT)) {
			// consume the 'print' token
			advance();
			Token t = consume(TokenType.STRING, "Expected string after print");
			consume(TokenType.SEMICOLON, "Expected ';' after print statement");
			return Option.some(new PrintStmt(new StringLiteral(t.lexeme)));
		}
		// unknown or skip
		advance();
		return Option.none();
	}

	private boolean match(TokenType type) {
		if (peek().type == type)
			return true;
		return false;
	}

	private Token consume(TokenType type, String msg) {
		Token t = peek();
		if (t.type != type)
			throw new RuntimeException(msg + ", got: " + t);
		advance();
		return t;
	}

	private Token peek() {
		return tokens.get(Math.min(pos, tokens.size() - 1));
	}

	private Token advance() {
		return tokens.get(pos++);
	}
}
