package magma.string.result;

import magma.result.Matchable;

import java.util.Optional;

public interface StringResult extends Matchable<String> {
    @Deprecated
    Optional<String> toOptional();

    StringResult appendResult(StringResult other);

    StringResult prepend(String other);

    StringResult appendSlice(String slice);
}
