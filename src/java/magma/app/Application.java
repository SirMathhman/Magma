package magma.app;

import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.io.source.Source;
import magma.app.io.source.Sources;
import magma.app.io.target.Targets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record Application(Sources sources, Compiler compiler, Targets targets) {
    private static Option<IOException> compileAll(Iterable<Source> sources, Targets targets, Compiler compiler) {
        return switch (readAll(sources)) {
            case Err(var error) -> new Some<>(error);
            case Ok(var sourceMap) -> compileAndWrite(sourceMap, targets, compiler);
        };
    }

    private static Option<IOException> compileAndWrite(Map<Source, String> sourceMap, Targets targets, Compiler compiler) {
        final var fileName = compiler.compile(sourceMap);
        return targets.write("@startuml\nskinparam linetype ortho\n" + fileName + "@enduml");
    }

    private static Result<Map<Source, String>, IOException> readAll(Iterable<Source> sources) {
        final var sourceMap = new HashMap<Source, String>();
        for (var source : sources) {
            final var result = source.readString();
            switch (result) {
                case Err(var error) -> {
                    return new Err<>(error);
                }
                case Ok(var value) -> sourceMap.put(source, value);
            }
        }

        return new Ok<>(sourceMap);
    }

    public Option<IOException> run() {
        return switch (this.sources.collect()) {
            case Err<Set<Source>, IOException>(var error) -> new Some<>(error);
            case Ok<Set<Source>, IOException>(var files) -> compileAll(files, this.targets, this.compiler);
        };
    }
}