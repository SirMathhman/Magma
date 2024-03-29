package com.meti;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
    static void run(SourceSet sourceSet, Path targetDirectory, String targetExtension) throws IOException, CompileException {
        var set = sourceSet.collect();

        for (var source : set) {
            var namespace = source.findNamespace();
            var unit = new Unit(namespace);
            var output = unit.compile(source.read());

            var without = source.findName();

            var parent = namespace.stream().reduce(targetDirectory, Path::resolve, (path, path2) -> path2);
            if (!Files.exists(parent)) Files.createDirectories(parent);

            var target = parent.resolve(without + targetExtension);
            Files.writeString(target, output);
        }
    }
}