package magma.string.result;

import magma.error.FormatError;
import magma.result.Matchable;

import java.util.Optional;

public interface StringResult extends Matchable<String, FormatError> {
    @Deprecated
    Optional<String> toOptional();

    StringResult appendResult(StringResult other);

    StringResult prepend(String other);

    StringResult appendSlice(String slice);
}
