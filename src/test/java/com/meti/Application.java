package com.meti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public record Application(Path source) {
    Optional<Path> run() throws IOException {
        if (Files.exists(source())) {
            var fileName = source().getFileName().toString();
            var separator = fileName.indexOf('.');
            var fileNameWithoutExtension = fileName.substring(0, separator);
            var target = source().resolveSibling(fileNameWithoutExtension + ".mgs");
            Files.createFile(target);
            return Optional.of(target);
        }
        return Optional.empty();
    }
}