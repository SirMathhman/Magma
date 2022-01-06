package com.meti.module;

import com.meti.io.NIOPath;
import com.meti.PathSource;
import com.meti.Source;

import java.util.Collections;
import java.util.List;

public record SingleModule(NIOPath path) implements Module {
    @Override
    public List<Source> listSources() {
        return path.exists()
                ? List.of(new PathSource(path, Collections.emptyList()))
                : Collections.emptyList();
    }
}