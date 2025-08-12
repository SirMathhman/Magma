package com.example;

public class Compiler {
    public Compiler() {
        // Default constructor
    }

    public void compile(String sourceCode) throws CompileException {
        // Always throw for demonstration
        throw new CompileException("Compilation failed for: " + sourceCode);
    }
}
