package com.meti;

import com.meti.compile.CompileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var sourceDirectory = Paths.get(".", "src", "java");

        try {
            var sources = Files.walk(sourceDirectory)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".java"))
                    .toList();

            for (var source : sources) {
                var fileName = source.getFileName().toString();
                var separator = fileName.lastIndexOf('.');
                var name = fileName.substring(0, separator);

                var input = Files.readString(source);
                var target = source.resolveSibling(name + ".mgs");
                Files.writeString(target, Application.compile(input));
            }
        } catch (IOException | CompileException e) {
            throw new RuntimeException(e);
        }
    }
}
