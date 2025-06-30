package magma.string.result;

import magma.error.FormatError;
import magma.result.Matchable;

import java.util.Optional;
import java.util.function.Function;

public interface StringResult extends Matchable<String, FormatError> {
    @Deprecated
    Optional<String> toOptional();

    StringResult appendResult(StringResult other);

    StringResult prependSlice(String other);

    StringResult appendSlice(String slice);

    StringResult flatMap(Function<String, StringResult> mapper);

    StringResult map(Function<String, String> mapper);
}
