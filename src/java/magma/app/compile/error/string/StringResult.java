package magma.app.compile.error.string;

import magma.api.Result;
import magma.app.compile.error.CompileError;
import magma.app.compile.rule.State;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StringResult {
    StringResult appendResult(Supplier<StringResult> other);

    StringResult complete(Function<String, String> mapper);

    StringResult prependSlice(String slice);

    StringResult appendSlice(String slice);

    Result<String, CompileError> toResult();

    State<String> attachToState(State<String> state);
}
