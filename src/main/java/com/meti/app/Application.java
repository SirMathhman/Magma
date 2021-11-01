package com.meti.app;

import com.meti.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record Application(Path source) {
    Path run() throws ApplicationException {
        var fileName = source.getFileName().toString();
        var separator = fileName.indexOf('.');
        var name = fileName.substring(0, separator);
        var targetName = name + ".c";
        var target = source.resolveSibling(targetName);
        if (Files.exists(source)) {
            try {
                var input = Files.readString(source);
                var output = new Compiler(input).compile();
                Files.writeString(target, output);
            } catch (IOException e) {
                throw new ApplicationException(e);
            }
        }

        return target;
    }
}