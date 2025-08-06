package com.magma.compiler.lexer;

import java.util.List;

/**
 * Interface for lexical analyzers in the Magma compiler.
 * A lexer converts source code into a stream of tokens.
 */
public interface Lexer {
    
    /**
     * Tokenizes the input source code.
     *
     * @return A list of tokens representing the source code
     */
    List<Token> tokenize();
    
    /**
     * Returns the next token from the source without consuming it.
     *
     * @return The next token
     */
    Token peek();
    
    /**
     * Returns the next token from the source and advances the lexer.
     *
     * @return The next token
     */
    Token nextToken();
    
    /**
     * Checks if there are more tokens available.
     *
     * @return true if there are more tokens, false otherwise
     */
    boolean hasMoreTokens();
}