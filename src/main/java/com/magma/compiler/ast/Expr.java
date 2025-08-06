package com.magma.compiler.ast;

/**
 * Base class for all expression nodes in the Abstract Syntax Tree.
 */
public abstract class Expr {
    /**
     * Visitor interface for implementing the Visitor pattern.
     * This allows operations to be performed on expression nodes without
     * modifying the node classes themselves.
     *
     * @param <R> The return type of the visitor methods
     */
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
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
     * Represents a binary expression (e.g., a + b, a == b).
     */
    public static class Binary extends Expr {
        public final Expr left;
        public final com.magma.compiler.lexer.Token operator;
        public final Expr right;

        public Binary(Expr left, com.magma.compiler.lexer.Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    /**
     * Represents a grouping expression (e.g., (a + b)).
     */
    public static class Grouping extends Expr {
        public final Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    /**
     * Represents a literal value (e.g., 123, "hello", true).
     */
    public static class Literal extends Expr {
        public final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    /**
     * Represents a unary expression (e.g., !a, -b).
     */
    public static class Unary extends Expr {
        public final com.magma.compiler.lexer.Token operator;
        public final Expr right;

        public Unary(com.magma.compiler.lexer.Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    /**
     * Represents a variable reference expression.
     */
    public static class Variable extends Expr {
        public final com.magma.compiler.lexer.Token name;

        public Variable(com.magma.compiler.lexer.Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }
}