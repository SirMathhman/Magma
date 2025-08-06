package com.magma.compiler.parser;

import com.magma.compiler.ast.Expr;
import com.magma.compiler.ast.Stmt;
import com.magma.compiler.lexer.MagmaLexer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the MagmaParser class.
 */
public class MagmaParserTest {

    @Test
    public void testParseSimpleExpression() {
        MagmaLexer lexer = new MagmaLexer("1 + 2");
        MagmaParser parser = new MagmaParser(lexer);
        
        Expr expr = parser.parseExpression();
        assertNotNull(expr);
        assertTrue(expr instanceof Expr.Binary);
        
        Expr.Binary binary = (Expr.Binary) expr;
        assertTrue(binary.left instanceof Expr.Literal);
        assertTrue(binary.right instanceof Expr.Literal);
        assertEquals(1.0, ((Expr.Literal) binary.left).value);
        assertEquals(2.0, ((Expr.Literal) binary.right).value);
    }
    
    @Test
    public void testParseSimpleStatement() {
        MagmaLexer lexer = new MagmaLexer("print \"Hello, World!\";");
        MagmaParser parser = new MagmaParser(lexer);
        
        Stmt stmt = parser.parseStatement();
        assertNotNull(stmt);
        assertTrue(stmt instanceof Stmt.Print);
        
        Stmt.Print print = (Stmt.Print) stmt;
        assertTrue(print.expression instanceof Expr.Literal);
        assertEquals("Hello, World!", ((Expr.Literal) print.expression).value);
    }
    
    @Test
    public void testParseVariableDeclaration() {
        MagmaLexer lexer = new MagmaLexer("var x = 10;");
        MagmaParser parser = new MagmaParser(lexer);
        
        Stmt stmt = parser.parseDeclaration();
        assertNotNull(stmt);
        assertTrue(stmt instanceof Stmt.Var);
        
        Stmt.Var var = (Stmt.Var) stmt;
        assertEquals("x", var.name.getLexeme());
        assertTrue(var.initializer instanceof Expr.Literal);
        assertEquals(10.0, ((Expr.Literal) var.initializer).value);
    }
    
    @Test
    public void testParseIfStatement() {
        MagmaLexer lexer = new MagmaLexer("if (true) print \"Yes\"; else print \"No\";");
        MagmaParser parser = new MagmaParser(lexer);
        
        Stmt stmt = parser.parseStatement();
        assertNotNull(stmt);
        assertTrue(stmt instanceof Stmt.If);
        
        Stmt.If ifStmt = (Stmt.If) stmt;
        assertTrue(ifStmt.condition instanceof Expr.Literal);
        assertEquals(true, ((Expr.Literal) ifStmt.condition).value);
        
        assertTrue(ifStmt.thenBranch instanceof Stmt.Print);
        Stmt.Print thenPrint = (Stmt.Print) ifStmt.thenBranch;
        assertEquals("Yes", ((Expr.Literal) thenPrint.expression).value);
        
        assertTrue(ifStmt.elseBranch instanceof Stmt.Print);
        Stmt.Print elsePrint = (Stmt.Print) ifStmt.elseBranch;
        assertEquals("No", ((Expr.Literal) elsePrint.expression).value);
    }
    
    @Test
    public void testParseWhileStatement() {
        MagmaLexer lexer = new MagmaLexer("while (true) print \"Loop\";");
        MagmaParser parser = new MagmaParser(lexer);
        
        Stmt stmt = parser.parseStatement();
        assertNotNull(stmt);
        assertTrue(stmt instanceof Stmt.While);
        
        Stmt.While whileStmt = (Stmt.While) stmt;
        assertTrue(whileStmt.condition instanceof Expr.Literal);
        assertEquals(true, ((Expr.Literal) whileStmt.condition).value);
        
        assertTrue(whileStmt.body instanceof Stmt.Print);
        Stmt.Print print = (Stmt.Print) whileStmt.body;
        assertEquals("Loop", ((Expr.Literal) print.expression).value);
    }
    
    @Test
    public void testParseBlock() {
        MagmaLexer lexer = new MagmaLexer("{ var x = 1; print x; }");
        MagmaParser parser = new MagmaParser(lexer);
        
        Stmt stmt = parser.parseStatement();
        assertNotNull(stmt);
        assertTrue(stmt instanceof Stmt.Block);
        
        Stmt.Block block = (Stmt.Block) stmt;
        assertEquals(2, block.statements.size());
        
        assertTrue(block.statements.get(0) instanceof Stmt.Var);
        Stmt.Var var = (Stmt.Var) block.statements.get(0);
        assertEquals("x", var.name.getLexeme());
        
        assertTrue(block.statements.get(1) instanceof Stmt.Print);
        Stmt.Print print = (Stmt.Print) block.statements.get(1);
        assertTrue(print.expression instanceof Expr.Variable);
        assertEquals("x", ((Expr.Variable) print.expression).name.getLexeme());
    }
}