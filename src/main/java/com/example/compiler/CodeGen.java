package com.example.compiler;

import java.io.FileWriter;
import java.io.IOException;

public class CodeGen {
    public static void generateC(Program program, String outPath) throws IOException {
        try (FileWriter fw = new FileWriter(outPath)) {
            fw.write("#include <stdio.h>\n\n");
            fw.write("int main() {\n");
            fw.write("    printf(\"" + escapeForC(program.getMessage()) + "\");\n");
            fw.write("    return 0;\n");
            fw.write("}\n");
        }
    }

    private static String escapeForC(String s) {
        // naive escaping: replace backslashes and double quotes
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
