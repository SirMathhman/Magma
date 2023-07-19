package com.meti.app;

import com.meti.core.Result;
import com.meti.java.JavaSet;
import com.meti.java.JavaString;
import com.meti.nio.NIODirectory;

import java.io.IOException;

public record DirectorySources(NIODirectory directory) implements Sources {
    @Override
    public Result<JavaSet<NIOSource>, IOException> collect() {
        return directory.walk().mapValue(set -> {
            System.out.printf("Located '%d' sources overall.%n", set.size().value());
            set.iter().take(5).forEach(path -> System.out.println(" - " + path.asString().unwrap()));

            return set.iter()
                    .filter(path -> path.isExtendedBy(JavaString.from("java")))
                    .map((parent) -> new NIOSource(directory, parent))
                    .collect(JavaSet.asSet());
        });
    }
}
