package com.magma.compiler.ast;

import java.util.Collections;
import java.util.List;

/**
 * Base class for all statement nodes in the Abstract Syntax Tree.
 */
public abstract class Stmt {
    /**
     * Visitor interface for implementing the Visitor pattern.
     * This allows operations to be performed on statement nodes without
     * modifying the node classes themselves.
     *
     * @param <R> The return type of the visitor methods
     */
    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitWhileStmt(While stmt);
    }

    /**
     * Accept method for the visitor pattern.
     *
     * @param visitor The visitor to accept
     * @param <R>     The return type of the visitor
     * @return The result of the visitor's visit method
     */
    public abstract <R> R accept(Visitor<R> visitor);

    /**
     * Represents a block of statements enclosed in braces.
     */
    public static class Block extends Stmt {
        public final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = Collections.unmodifiableList(statements);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    /**
     * Represents an expression statement.
     */
    public static class Expression extends Stmt {
        public final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    /**
     * Represents an if statement.
     */
    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    /**
     * Represents a print statement.
     */
    public static class Print extends Stmt {
        public final Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    /**
     * Represents a variable declaration statement.
     */
    public static class Var extends Stmt {
        public final com.magma.compiler.lexer.Token name;
        public final com.magma.compiler.lexer.Token type;
        public final Expr initializer;

        public Var(com.magma.compiler.lexer.Token name, com.magma.compiler.lexer.Token type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    /**
     * Represents a while statement.
     */
    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }
}