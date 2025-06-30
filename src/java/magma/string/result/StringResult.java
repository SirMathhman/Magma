package magma.string.result;

import magma.error.CompileError;
import magma.result.Matchable;

import java.util.Optional;

public interface StringResult extends Matchable<String, CompileError> {
    @Deprecated
    Optional<String> toOptional();

    StringResult appendResult(StringResult other);

    StringResult prepend(String other);

    StringResult appendSlice(String slice);
}
