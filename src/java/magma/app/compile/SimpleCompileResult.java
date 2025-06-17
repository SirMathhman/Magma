package magma.app.compile;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.option.Option;
import magma.api.option.Options;
import magma.app.compile.state.CompileState;

import java.util.function.Supplier;

record SimpleCompileResult(Option<Tuple2<CompileState, String>> value) implements CompileResult {
    public static CompileResult fromValues(CompileState state, String value) {
        return new SimpleCompileResult(Options.of(new Tuple2Impl<>(state, value)));
    }

    public static CompileResult fromEmpty() {
        return new SimpleCompileResult(Options.empty());
    }

    @Override
    public Tuple2<CompileState, String> get() {
        return this.value.get();
    }

    @Override
    public boolean isPresent() {
        return this.value.isPresent();
    }

    @Override
    public CompileResult or(Supplier<CompileResult> other) {
        return new SimpleCompileResult(this.value.or(() -> other.get()
                .value()));
    }
}
