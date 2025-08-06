package com.magma.compiler;

import com.magma.compiler.ast.Expr;
import com.magma.compiler.ast.Stmt;
import com.magma.compiler.lexer.Lexer;
import com.magma.compiler.lexer.MagmaLexer;
import com.magma.compiler.parser.MagmaParser;
import com.magma.compiler.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for the Magma compiler.
 * This class provides the entry point for the compiler and handles
 * running code from files or interactively.
 * 
 * The Magma compiler translates Magma programming language code to C.
 */
public class Magma {
    private static ErrorState errorState = ErrorState.noError();

    /**
     * Main entry point for the Magma compiler.
     *
     * @param args Command line arguments
     * @throws IOException If an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: magma [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Runs the Magma compiler on a file.
     *
     * @param path The path to the file to run
     * @throws IOException If an I/O error occurs
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (errorState.hadError()) System.exit(65);
    }

    /**
     * Runs the Magma compiler in interactive mode.
     *
     * @throws IOException If an I/O error occurs
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            errorState = errorState.reset();
        }
    }

    /**
     * Runs the Magma compiler on the given source code.
     *
     * @param source The source code to run
     */
    private static void run(String source) {
        // Create the lexer
        Lexer lexer = new MagmaLexer(source);
        
        // Create the parser
        Parser parser = new MagmaParser(lexer);
        
        // Parse the source code
        List<Stmt> statements = parser.parse();
        
        // Stop if there was a syntax error
        if (errorState.hadError()) return;
        
        // For now, just print the AST structure
        System.out.println("Parsed " + statements.size() + " statements.");
        
        // In a complete compiler, we would:
        // 1. Perform semantic analysis
        // 2. Generate intermediate code
        // 3. Optimize the code
        // 4. Generate C code (target language)
        // 5. Output the generated C code to a file
    }

    /**
     * Reports an error to the user.
     *
     * @param line    The line where the error occurred
     * @param message The error message
     */
    public static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Reports an error to the user.
     *
     * @param line    The line where the error occurred
     * @param where   Where the error occurred
     * @param message The error message
     */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        errorState = errorState.withError();
    }
}