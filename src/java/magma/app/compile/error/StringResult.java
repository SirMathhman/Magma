package magma.app.compile.error;

import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StringResult<Error> {
    StringResult<Error> appendResult(Supplier<StringResult<Error>> other);

    StringResult<Error> complete(Function<String, String> mapper);

    StringResult<Error> prependSlice(String slice);

    StringResult<Error> appendSlice(String slice);

    Result<String, Error> toResult();

    OrState<String, Error> attachToState(OrState<String, Error> state);
}
