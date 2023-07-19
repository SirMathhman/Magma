package com.meti.app;

import com.meti.core.Result;
import com.meti.core.Results;
import com.meti.core.ThrowableResult;
import com.meti.iterate.ResultIterator;
import com.meti.java.JavaSet;

public final class Application {
    private final Sources sources;
    private final Targets targets;

    public Application(Sources sources, Targets targets) {
        this.sources = sources;
        this.targets = targets;
    }

    Result<JavaSet<NIOTarget>, CompileException> compileAll() {
        return sources.collect()
                .peekValue(value -> System.out.printf("Found '%s' sources.%n", value.size().value()))
                .mapErr(CompileException::new)
                .mapValueToResult(nioSourceJavaSet -> nioSourceJavaSet.iter()
                        .map(this::compile)
                        .into(ResultIterator::new)
                        .collectToResult(JavaSet.asSet()));
    }

    private Result<NIOTarget, CompileException> compile(NIOSource source) {
        return Results.$Result(() -> {
            var package_ = source.computePackage();
            var other = source.computeName().append(".mgs");
            var input = source.read()
                    .mapErr(CompileException::new)
                    .into(ThrowableResult::new).$();

            var output = new Compiler(input)
                    .compile()
                    .into(ThrowableResult::new)
                    .$();

            if (!input.isEmpty() && output.isEmpty()) {
                throw new CompileException("Input was not empty but output was? Input: " + input.unwrap());
            }

            var target = targets.resolve(package_, other)
                    .mapErr(CompileException::new)
                    .into(ThrowableResult::new)
                    .$();

            Results.throwOption(target.write(output).map(CompileException::new));
            return target;
        });
    }
}