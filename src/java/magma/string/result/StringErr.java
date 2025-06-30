package magma.string.result;

import java.util.Optional;

public class StringErr implements StringResult {
    @Override
    public Optional<String> toOptional() {
        return Optional.empty();
    }
}
