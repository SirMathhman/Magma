package magma.string.result;

import magma.result.Matchable;

import java.util.function.Function;

public interface StringResult<Error> extends Matchable<String, Error> {
    StringResult<Error> appendResult(StringResult<Error> other);

    StringResult<Error> prependSlice(String other);

    StringResult<Error> appendSlice(String slice);

    StringResult<Error> flatMap(Function<String, StringResult<Error>> mapper);

    StringResult<Error> map(Function<String, String> mapper);
}
