package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple command line interface for the Transpiler.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java com.example.Main <Java file>");
            return;
        }
        Path path = Path.of(args[0]);
        String javaSrc = Files.readString(path);
        String ts = new Transpiler().toTypeScript(javaSrc);
        System.out.println(ts);
    }
}
