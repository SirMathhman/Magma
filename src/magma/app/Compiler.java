package magma.app;

import magma.compile.CompileException;

public class Compiler {
    String compile(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith("package ")) return "";
        if (input.startsWith("import ")) return input;

        throw new CompileException("Invalid root", input);
    }
}