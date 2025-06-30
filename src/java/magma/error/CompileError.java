package magma.error;

import java.util.Collections;
import java.util.List;

public record CompileError(String message, List<CompileError> errors) implements Error {
    public CompileError(final String message) {
        this(message, Collections.emptyList());
    }

    @Override
    public String display() {
        return this.message;
    }
}
