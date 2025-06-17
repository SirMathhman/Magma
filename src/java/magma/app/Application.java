package magma.app;

import magma.api.io.IOError;
import magma.api.io.IOOption;
import magma.api.io.SimpleIOOption;
import magma.api.list.Sequence;
import magma.api.result.Result;
import magma.api.result.Results;
import magma.app.compile.Compiler;
import magma.app.io.source.Source;
import magma.app.io.source.Sources;
import magma.app.io.target.Targets;

import java.util.HashMap;
import java.util.Map;

public record Application(Sources sources, Compiler compiler, Targets targets) {
    private static IOOption compileAll(Sequence<Source> sources, Targets targets, Compiler compiler) {
        return readAll(sources).match(sourceMap -> compileAndWrite(sourceMap, targets, compiler), SimpleIOOption::of);
    }

    private static IOOption compileAndWrite(Map<Source, String> sourceMap, Targets targets, Compiler compiler) {
        final var fileName = compiler.compile(sourceMap);
        return targets.write("@startuml\nskinparam linetype ortho\n" + fileName + "@enduml");
    }

    private static Result<Map<Source, String>, IOError> readAll(Sequence<Source> sources) {
        Result<Map<Source, String>, IOError> maybeSourceMap = Results.fromValue(new HashMap<>());
        for (var i = 0; i < sources.size(); i++) {
            final var source = sources.get(i);
            maybeSourceMap = foldSource(maybeSourceMap, source);
        }

        return maybeSourceMap;
    }

    private static Result<Map<Source, String>, IOError> foldSource(Result<Map<Source, String>, IOError> maybeSourceMap, Source source) {
        return maybeSourceMap.flatMap(sourceMap -> source.readString()
                .map(input -> {
                    sourceMap.put(source, input);
                    return sourceMap;
                }));
    }

    public IOOption run() {
        return this.sources.collect()
                .match(files -> compileAll(files, this.targets, this.compiler), SimpleIOOption::of);
    }
}