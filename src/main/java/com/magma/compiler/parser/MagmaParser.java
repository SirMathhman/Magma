package com.magma.compiler.parser;

import com.magma.compiler.ast.Expr;
import com.magma.compiler.ast.Stmt;
import com.magma.compiler.lexer.Lexer;
import com.magma.compiler.lexer.Token;
import com.magma.compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Parser interface for the Magma language.
 * This parser implements a recursive descent parser for a simple grammar.
 */
public class MagmaParser implements Parser {
    private final Lexer lexer;
    private Token currentToken;
    private Token previousToken;
    private boolean hadError = false;

    /**
     * Creates a new parser for the given lexer.
     *
     * @param lexer The lexer to get tokens from
     */
    public MagmaParser(Lexer lexer) {
        this.lexer = lexer;
        // Tokenize the input
        lexer.tokenize();
        // Prime the pump with the first token
        advance();
    }

    @Override
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        
        while (!isAtEnd()) {
            statements.add(parseDeclaration());
        }
        
        return statements;
    }

    @Override
    public Expr parseExpression() {
        return equality();
    }

    @Override
    public Stmt parseStatement() {
        if (match(TokenType.PRINT)) {
            return printStatement();
        }
        
        if (match(TokenType.LEFT_BRACE)) {
            return new Stmt.Block(block());
        }
        
        if (match(TokenType.IF)) {
            return ifStatement();
        }
        
        if (match(TokenType.WHILE)) {
            return whileStatement();
        }
        
        return expressionStatement();
    }

    @Override
    public Stmt parseDeclaration() {
        try {
            if (match(TokenType.VAR)) {
                return varDeclaration();
            }
            
            return parseStatement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    /**
     * Parses a variable declaration.
     *
     * @return The parsed variable declaration statement
     */
    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        
        Expr initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = parseExpression();
        }
        
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    /**
     * Parses a print statement.
     *
     * @return The parsed print statement
     */
    private Stmt printStatement() {
        Expr value = parseExpression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    /**
     * Parses an if statement.
     *
     * @return The parsed if statement
     */
    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");
        
        Stmt thenBranch = parseStatement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = parseStatement();
        }
        
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    /**
     * Parses a while statement.
     *
     * @return The parsed while statement
     */
    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = parseExpression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = parseStatement();
        
        return new Stmt.While(condition, body);
    }

    /**
     * Parses a block of statements.
     *
     * @return The list of statements in the block
     */
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(parseDeclaration());
        }
        
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    /**
     * Parses an expression statement.
     *
     * @return The parsed expression statement
     */
    private Stmt expressionStatement() {
        Expr expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    /**
     * Parses an equality expression.
     *
     * @return The parsed expression
     */
    private Expr equality() {
        Expr expr = comparison();
        
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previousToken;
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    /**
     * Parses a comparison expression.
     *
     * @return The parsed expression
     */
    private Expr comparison() {
        Expr expr = term();
        
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previousToken;
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    /**
     * Parses a term expression.
     *
     * @return The parsed expression
     */
    private Expr term() {
        Expr expr = factor();
        
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previousToken;
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    /**
     * Parses a factor expression.
     *
     * @return The parsed expression
     */
    private Expr factor() {
        Expr expr = unary();
        
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previousToken;
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    /**
     * Parses a unary expression.
     *
     * @return The parsed expression
     */
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previousToken;
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        
        return primary();
    }

    /**
     * Parses a primary expression.
     *
     * @return The parsed expression
     */
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);
        
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previousToken.getLiteral());
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previousToken);
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        
        throw error(currentToken, "Expect expression.");
    }

    /**
     * Checks if the current token is of the given type.
     *
     * @param type The token type to check
     * @return true if the current token is of the given type, false otherwise
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return currentToken.getType() == type;
    }

    /**
     * Consumes the current token if it's of the given type, otherwise throws an error.
     *
     * @param type    The expected token type
     * @param message The error message if the token doesn't match
     * @return The consumed token
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        
        throw error(currentToken, message);
    }

    /**
     * Creates a parse error.
     *
     * @param token   The token that caused the error
     * @param message The error message
     * @return A new ParseError
     */
    private ParseError error(Token token, String message) {
        // In a real compiler, we would report the error
        hadError = true;
        return new ParseError();
    }

    /**
     * Advances to the next token.
     *
     * @return The previous token
     */
    private Token advance() {
        previousToken = currentToken;
        currentToken = lexer.nextToken();
        return previousToken;
    }

    /**
     * Checks if the current token is one of the given types, and if so, consumes it.
     *
     * @param types The token types to check
     * @return true if the current token was consumed, false otherwise
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        
        return false;
    }

    /**
     * Checks if we have reached the end of the token stream.
     *
     * @return true if we are at the end, false otherwise
     */
    private boolean isAtEnd() {
        return currentToken.getType() == TokenType.EOF;
    }

    /**
     * Synchronizes the parser after an error.
     * This discards tokens until the beginning of the next statement.
     */
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previousToken.getType() == TokenType.SEMICOLON) return;
            
            switch (currentToken.getType()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            
            advance();
        }
    }

    /**
     * Exception class for parse errors.
     */
    private static class ParseError extends RuntimeException {
    }
}