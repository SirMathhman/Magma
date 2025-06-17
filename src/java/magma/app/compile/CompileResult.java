package magma.app.compile;

import magma.api.Tuple2;
import magma.api.option.Option;
import magma.app.compile.state.CompileState;

import java.util.function.Supplier;

public interface CompileResult {
    Tuple2<CompileState, String> get();

    boolean isPresent();

    CompileResult or(Supplier<CompileResult> other);

    Option<Tuple2<CompileState, String>> value();
}
