package magma.string.result;

import java.util.Optional;

public class StringErr implements StringResult {
    @Override
    public Optional<String> toOptional() {
        return Optional.empty();
    }

    @Override
    public StringResult appendResult(final StringResult other) {
        return this;
    }

    @Override
    public StringResult prepend(final String other) {
        return this;
    }
}
