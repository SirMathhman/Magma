package com.magma.compiler.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the MagmaLexer class.
 */
public class MagmaLexerTest {

    @Test
    public void testEmptySource() {
        MagmaLexer lexer = new MagmaLexer("");
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).getType());
    }
    
    @Test
    public void testSingleCharacterTokens() {
        MagmaLexer lexer = new MagmaLexer("(){},.-+;*");
        List<Token> tokens = lexer.tokenize();
        
        // Plus one for EOF token
        assertEquals(10, tokens.size());
        assertEquals(TokenType.LEFT_PAREN, tokens.get(0).getType());
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(1).getType());
        assertEquals(TokenType.LEFT_BRACE, tokens.get(2).getType());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(3).getType());
        assertEquals(TokenType.COMMA, tokens.get(4).getType());
        assertEquals(TokenType.DOT, tokens.get(5).getType());
        assertEquals(TokenType.MINUS, tokens.get(6).getType());
        assertEquals(TokenType.PLUS, tokens.get(7).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(8).getType());
        assertEquals(TokenType.STAR, tokens.get(9).getType());
    }
    
    @Test
    public void testOneOrTwoCharacterTokens() {
        MagmaLexer lexer = new MagmaLexer("! != = == > >= < <=");
        List<Token> tokens = lexer.tokenize();
        
        // Plus one for EOF token
        assertEquals(9, tokens.size());
        assertEquals(TokenType.BANG, tokens.get(0).getType());
        assertEquals(TokenType.BANG_EQUAL, tokens.get(1).getType());
        assertEquals(TokenType.EQUAL, tokens.get(2).getType());
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(3).getType());
        assertEquals(TokenType.GREATER, tokens.get(4).getType());
        assertEquals(TokenType.GREATER_EQUAL, tokens.get(5).getType());
        assertEquals(TokenType.LESS, tokens.get(6).getType());
        assertEquals(TokenType.LESS_EQUAL, tokens.get(7).getType());
    }
    
    @Test
    public void testStringLiteral() {
        MagmaLexer lexer = new MagmaLexer("\"Hello, World!\"");
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(2, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("Hello, World!", tokens.get(0).getLiteral());
    }
    
    @Test
    public void testNumberLiteral() {
        MagmaLexer lexer = new MagmaLexer("123 123.456");
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(3, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals(123.0, tokens.get(0).getLiteral());
        assertEquals(TokenType.NUMBER, tokens.get(1).getType());
        assertEquals(123.456, tokens.get(1).getLiteral());
    }
    
    @Test
    public void testIdentifierAndKeywords() {
        MagmaLexer lexer = new MagmaLexer("var name = \"John\"; if true { print name; }");
        List<Token> tokens = lexer.tokenize();
        
        assertEquals(12, tokens.size());
        assertEquals(TokenType.VAR, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("name", tokens.get(1).getLexeme());
        assertEquals(TokenType.EQUAL, tokens.get(2).getType());
        assertEquals(TokenType.STRING, tokens.get(3).getType());
        assertEquals("John", tokens.get(3).getLiteral());
        assertEquals(TokenType.SEMICOLON, tokens.get(4).getType());
        assertEquals(TokenType.IF, tokens.get(5).getType());
        assertEquals(TokenType.TRUE, tokens.get(6).getType());
        assertEquals(TokenType.LEFT_BRACE, tokens.get(7).getType());
        assertEquals(TokenType.PRINT, tokens.get(8).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(9).getType());
        assertEquals("name", tokens.get(9).getLexeme());
        assertEquals(TokenType.SEMICOLON, tokens.get(10).getType());
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(11).getType());
    }
}