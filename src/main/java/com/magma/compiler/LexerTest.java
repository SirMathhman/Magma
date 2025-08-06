package com.magma.compiler;

import com.magma.compiler.lexer.Lexer;
import com.magma.compiler.lexer.MagmaLexer;
import com.magma.compiler.lexer.Token;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * A simple test program to debug the lexer.
 */
public class LexerTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java com.magma.compiler.LexerTest <script>");
            System.exit(64);
        }

        String path = args[0];
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String source = new String(bytes, Charset.defaultCharset());

        // Create the lexer
        Lexer lexer = new MagmaLexer(source);

        // Tokenize the source code
        List<Token> tokens = lexer.tokenize();

        // Print the tokens
        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}