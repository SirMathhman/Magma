package com.magma.compiler;

import com.magma.compiler.ast.Stmt;
import com.magma.compiler.lexer.Lexer;
import com.magma.compiler.lexer.MagmaLexer;
import com.magma.compiler.lexer.Token;
import com.magma.compiler.parser.MagmaParser;
import com.magma.compiler.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * A simple test program to debug the parser.
 */
public class ParserTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java com.magma.compiler.ParserTest <script>");
            System.exit(64);
        }

        String path = args[0];
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String source = new String(bytes, Charset.defaultCharset());

        System.out.println("Source code:");
        System.out.println(source);
        System.out.println();

        // Create the lexer and tokenize the source
        MagmaLexer lexer = new MagmaLexer(source);
        List<Token> tokens = lexer.tokenize();

        // Print the tokens
        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println();

        // Create a new lexer for the parser (since the first one has already consumed all tokens)
        Lexer parserLexer = new MagmaLexer(source);
        
        // Create the parser
        Parser parser = new MagmaParser(parserLexer);

        try {
            // Parse the source code
            List<Stmt> statements = parser.parse();

            // Print the number of statements
            System.out.println("Parsed " + statements.size() + " statements.");

            // Print the statements (using toString)
            if (!statements.isEmpty()) {
                System.out.println("Statements:");
                for (Stmt stmt : statements) {
                    System.out.println(stmt);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing source: " + e.getMessage());
            e.printStackTrace();
        }
    }
}