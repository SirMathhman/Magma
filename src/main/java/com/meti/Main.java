package com.meti;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        var files = new ArrayList<Path>();
        var sourceDirectory = Paths.get(".", "src");
        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
              files.add(file);
              return FileVisitResult.CONTINUE;
            }
        });

        var target = Paths.get(".", "target");
        for (Path file : files) {
            var relativeOriginal = sourceDirectory.relativize(file);
            var fileName = relativeOriginal.getFileName().toString();
            var separator= fileName.indexOf(".");
            var fileNameWithoutExtension = fileName.substring(0, separator);

            Path leafDirectory;
            var parent = relativeOriginal.getParent();
            if (parent == null) {
                leafDirectory = target;
            } else {
                leafDirectory = target.resolve(parent);
            }

            var leaf = leafDirectory
                    .resolve(fileNameWithoutExtension + ".mgs");

            var input = Files.readString(file);
            var lines = Arrays.stream(input.split(";"))
                    .map(String::strip)
                    .filter(value -> !value.isEmpty())
                    .toList();

            var output = new StringBuilder();
            for (String line : lines) {
                if(line.startsWith("import ")) {
                    output.append(line).append(";\n");
                }
            }

            var actualParent = leaf.getParent();
            if(!Files.exists(actualParent)) {
                Files.createDirectories(actualParent);
            }

            Files.writeString(leaf, output.toString());
        }
    }
}