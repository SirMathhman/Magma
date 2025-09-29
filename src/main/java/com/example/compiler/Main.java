package com.example.compiler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Magma compiler (minimal scaffold)");

        // For now create a hardcoded program AST and generate C
        Program program = new Program("Hello from generated C!\n");
        CodeGen.generateC(program, "output.c");

        System.out.println("Generated output.c");
    }
}
