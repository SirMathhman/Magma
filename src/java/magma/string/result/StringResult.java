package magma.string.result;

import java.util.Optional;

public interface StringResult {
    @Deprecated
    Optional<String> toOptional();

    StringResult appendResult(StringResult other);

    StringResult prepend(String other);

    String appendSlice(String slice);
}
