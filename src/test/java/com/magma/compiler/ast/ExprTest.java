package com.magma.compiler.ast;

import com.magma.compiler.lexer.Token;
import com.magma.compiler.lexer.TokenType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Expr class and its subclasses.
 */
public class ExprTest {

    @Test
    public void testLiteralExpr() {
        Expr.Literal literal = new Expr.Literal("test");
        assertEquals("test", literal.value);
        
        Expr.Literal numberLiteral = new Expr.Literal(42.0);
        assertEquals(42.0, numberLiteral.value);
    }
    
    @Test
    public void testBinaryExpr() {
        Expr.Literal left = new Expr.Literal(5.0);
        Token operator = new Token(TokenType.PLUS, "+", null, 1, 1);
        Expr.Literal right = new Expr.Literal(3.0);
        
        Expr.Binary binary = new Expr.Binary(left, operator, right);
        
        assertSame(left, binary.left);
        assertSame(operator, binary.operator);
        assertSame(right, binary.right);
    }
    
    @Test
    public void testGroupingExpr() {
        Expr.Literal expression = new Expr.Literal(true);
        Expr.Grouping grouping = new Expr.Grouping(expression);
        
        assertSame(expression, grouping.expression);
    }
    
    @Test
    public void testUnaryExpr() {
        Token operator = new Token(TokenType.MINUS, "-", null, 1, 1);
        Expr.Literal right = new Expr.Literal(10.0);
        
        Expr.Unary unary = new Expr.Unary(operator, right);
        
        assertSame(operator, unary.operator);
        assertSame(right, unary.right);
    }
    
    @Test
    public void testVariableExpr() {
        Token name = new Token(TokenType.IDENTIFIER, "x", null, 1, 1);
        Expr.Variable variable = new Expr.Variable(name);
        
        assertSame(name, variable.name);
    }
    
    @Test
    public void testVisitorPattern() {
        // Create a simple visitor implementation for testing
        class TestVisitor implements Expr.Visitor<String> {
            @Override
            public String visitBinaryExpr(Expr.Binary expr) {
                return "binary";
            }
            
            @Override
            public String visitGroupingExpr(Expr.Grouping expr) {
                return "grouping";
            }
            
            @Override
            public String visitLiteralExpr(Expr.Literal expr) {
                return "literal";
            }
            
            @Override
            public String visitUnaryExpr(Expr.Unary expr) {
                return "unary";
            }
            
            @Override
            public String visitVariableExpr(Expr.Variable expr) {
                return "variable";
            }
        }
        
        TestVisitor visitor = new TestVisitor();
        
        // Test each expression type with the visitor
        Expr.Literal literal = new Expr.Literal(42.0);
        assertEquals("literal", literal.accept(visitor));
        
        Expr.Grouping grouping = new Expr.Grouping(literal);
        assertEquals("grouping", grouping.accept(visitor));
        
        Token operator = new Token(TokenType.PLUS, "+", null, 1, 1);
        Expr.Binary binary = new Expr.Binary(literal, operator, literal);
        assertEquals("binary", binary.accept(visitor));
        
        Expr.Unary unary = new Expr.Unary(operator, literal);
        assertEquals("unary", unary.accept(visitor));
        
        Token name = new Token(TokenType.IDENTIFIER, "x", null, 1, 1);
        Expr.Variable variable = new Expr.Variable(name);
        assertEquals("variable", variable.accept(visitor));
    }
}