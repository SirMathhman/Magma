package com.magma.compiler.parser;

import com.magma.compiler.ast.Expr;
import com.magma.compiler.ast.Stmt;
import java.util.List;

/**
 * Interface for parsers in the Magma compiler.
 * A parser converts a stream of tokens into an Abstract Syntax Tree (AST).
 */
public interface Parser {
    
    /**
     * Parses the input tokens into a list of statements.
     *
     * @return A list of statements representing the program
     */
    List<Stmt> parse();
    
    /**
     * Parses a single expression.
     *
     * @return The parsed expression
     */
    Expr parseExpression();
    
    /**
     * Parses a single statement.
     *
     * @return The parsed statement
     */
    Stmt parseStatement();
    
    /**
     * Parses a variable declaration.
     *
     * @return The parsed variable declaration statement
     */
    Stmt parseDeclaration();
}