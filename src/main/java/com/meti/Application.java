package com.meti;

import com.meti.io.NIOPath;
import com.meti.module.Module;
import com.meti.source.Source;

import java.io.IOException;

import static com.meti.io.NIOPath.Root;

public record Application(Module module) {
    public static final NIOPath Out = Root.resolveChild("out");

    void run() throws IOException {
        var sources = module.listSources();
        for (var source : sources) {
            compile(source);
        }
    }

    private static void compile(Source source) throws IOException {
        var name = source.computeName();

        var input = source.read();
        var output = new Compiler(input).compile();

        Out.ensureAsDirectory();
        source.computePackage()
                .reduce(Out, NIOPath::resolveChild, (previous, next) -> next)
                .resolveChild(name + ".c")
                .ensureAsFile()
                .writeAsString(output);
    }

}
