package magma.app.compile.error.string;

import magma.api.Error;
import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StringResult {
    StringResult appendResult(Supplier<StringResult> other);

    StringResult complete(Function<String, String> mapper);

    StringResult prependSlice(String slice);

    StringResult appendSlice(String slice);

    Result<String, Error> toResult();

    OrState<String> attachToState(OrState<String> state);
}
