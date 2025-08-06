package com.magma.compiler.ast;

import com.magma.compiler.lexer.Token;
import com.magma.compiler.lexer.TokenType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the Stmt class and its subclasses.
 */
public class StmtTest {

    @Test
    public void testBlockStmt() {
        List<Stmt> statements = new ArrayList<>();
        statements.add(new Stmt.Expression(new Expr.Literal(1.0)));
        statements.add(new Stmt.Expression(new Expr.Literal(2.0)));
        
        Stmt.Block block = new Stmt.Block(statements);
        
        assertEquals(2, block.statements.size());
        assertEquals(statements, block.statements);
    }
    
    @Test
    public void testExpressionStmt() {
        Expr.Literal literal = new Expr.Literal("test");
        Stmt.Expression expression = new Stmt.Expression(literal);
        
        assertSame(literal, expression.expression);
    }
    
    @Test
    public void testIfStmt() {
        Expr.Literal condition = new Expr.Literal(true);
        Stmt.Expression thenBranch = new Stmt.Expression(new Expr.Literal("then"));
        Stmt.Expression elseBranch = new Stmt.Expression(new Expr.Literal("else"));
        
        Stmt.If ifStmt = new Stmt.If(condition, thenBranch, elseBranch);
        
        assertSame(condition, ifStmt.condition);
        assertSame(thenBranch, ifStmt.thenBranch);
        assertSame(elseBranch, ifStmt.elseBranch);
    }
    
    @Test
    public void testPrintStmt() {
        Expr.Literal expression = new Expr.Literal("print me");
        Stmt.Print print = new Stmt.Print(expression);
        
        assertSame(expression, print.expression);
    }
    
    @Test
    public void testVarStmt() {
        Token name = new Token(TokenType.IDENTIFIER, "x", null, 1, 1);
        Expr.Literal initializer = new Expr.Literal(42.0);
        
        Stmt.Var var = new Stmt.Var(name, null, initializer);
        
        assertSame(name, var.name);
        assertSame(initializer, var.initializer);
    }
    
    @Test
    public void testWhileStmt() {
        Expr.Literal condition = new Expr.Literal(true);
        Stmt.Expression body = new Stmt.Expression(new Expr.Literal("body"));
        
        Stmt.While whileStmt = new Stmt.While(condition, body);
        
        assertSame(condition, whileStmt.condition);
        assertSame(body, whileStmt.body);
    }
    
    @Test
    public void testVisitorPattern() {
        // Create a simple visitor implementation for testing
        class TestVisitor implements Stmt.Visitor<String> {
            @Override
            public String visitBlockStmt(Stmt.Block stmt) {
                return "block";
            }
            
            @Override
            public String visitExpressionStmt(Stmt.Expression stmt) {
                return "expression";
            }
            
            @Override
            public String visitIfStmt(Stmt.If stmt) {
                return "if";
            }
            
            @Override
            public String visitPrintStmt(Stmt.Print stmt) {
                return "print";
            }
            
            @Override
            public String visitVarStmt(Stmt.Var stmt) {
                return "var";
            }
            
            @Override
            public String visitWhileStmt(Stmt.While stmt) {
                return "while";
            }
        }
        
        TestVisitor visitor = new TestVisitor();
        
        // Test each statement type with the visitor
        List<Stmt> statements = new ArrayList<>();
        Stmt.Block block = new Stmt.Block(statements);
        assertEquals("block", block.accept(visitor));
        
        Expr.Literal literal = new Expr.Literal(42.0);
        Stmt.Expression expression = new Stmt.Expression(literal);
        assertEquals("expression", expression.accept(visitor));
        
        Stmt.If ifStmt = new Stmt.If(literal, expression, expression);
        assertEquals("if", ifStmt.accept(visitor));
        
        Stmt.Print print = new Stmt.Print(literal);
        assertEquals("print", print.accept(visitor));
        
        Token name = new Token(TokenType.IDENTIFIER, "x", null, 1, 1);
        Stmt.Var var = new Stmt.Var(name, null, literal);
        assertEquals("var", var.accept(visitor));
        
        Stmt.While whileStmt = new Stmt.While(literal, expression);
        assertEquals("while", whileStmt.accept(visitor));
    }
}