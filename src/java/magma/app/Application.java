package magma.app;

import magma.api.io.IOError;
import magma.api.io.IOOption;
import magma.api.io.SimpleIOOption;
import magma.api.list.ListLike;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.io.source.Source;
import magma.app.io.source.Sources;
import magma.app.io.target.Targets;

import java.util.HashMap;
import java.util.Map;

public record Application(Sources sources, Compiler compiler, Targets targets) {
    private static IOOption compileAll(ListLike<Source> sources, Targets targets, Compiler compiler) {
        return switch (readAll(sources)) {
            case Err(var error) -> SimpleIOOption.of(error);
            case Ok(var sourceMap) -> compileAndWrite(sourceMap, targets, compiler);
        };
    }

    private static IOOption compileAndWrite(Map<Source, String> sourceMap, Targets targets, Compiler compiler) {
        final var fileName = compiler.compile(sourceMap);
        return targets.write("@startuml\nskinparam linetype ortho\n" + fileName + "@enduml");
    }

    private static Result<Map<Source, String>, IOError> readAll(ListLike<Source> sources) {
        final var sourceMap = new HashMap<Source, String>();
        for (var i = 0; i < sources.size(); i++) {
            final var source = sources.get(i);
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

    public IOOption run() {
        return switch (this.sources.collect()) {
            case Err<ListLike<Source>, IOError>(var error) -> SimpleIOOption.of(error);
            case Ok<ListLike<Source>, IOError>(var files) -> compileAll(files, this.targets, this.compiler);
        };
    }
}