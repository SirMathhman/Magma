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

	public Result<magma.compiler.ast.Program, String> parse() {
		List<Stmt> stmts = new ArrayList<>();
		while (!match(TokenType.EOF)) {
			Result<Option<Stmt>, String> r = parseStatement();
			if (r.asErrorOptional().isPresent()) {
				return Result.err(r.asErrorOptional().get());
			}
			r.asOptional().ifPresent(opt -> opt.asOptional().ifPresent(stmts::add));
		}
		return Result.ok(new magma.compiler.ast.Program(stmts));
	}

	private Result<Option<Stmt>, String> parseStatement() {
		if (match(TokenType.PRINT)) {
			// consume the 'print' token
			advance();
			Result<Token, String> tRes = consume(TokenType.STRING, "Expected string after print");
			if (tRes.asErrorOptional().isPresent()) {
				return Result.err(tRes.asErrorOptional().get());
			}
			Token t = tRes.asOptional().get();
			Result<Token, String> semiRes = consume(TokenType.SEMICOLON, "Expected ';' after print statement");
			if (semiRes.asErrorOptional().isPresent()) {
				return Result.err(semiRes.asErrorOptional().get());
			}
			return Result.ok(Option.some(new PrintStmt(new StringLiteral(t.lexeme))));
		}
		// unknown or skip
		advance();
		return Result.ok(Option.none());
	}

	private boolean match(TokenType type) {
		if (peek().type == type)
			return true;
		return false;
	}

	private Result<Token, String> consume(TokenType type, String msg) {
		Token t = peek();
		if (t.type != type)
			return Result.err(msg + ", got: " + t);
		advance();
		return Result.ok(t);
	}

	private Token peek() {
		return tokens.get(Math.min(pos, tokens.size() - 1));
	}

	private Token advance() {
		return tokens.get(pos++);
	}
}
