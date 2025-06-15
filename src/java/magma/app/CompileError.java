package magma.app;

import magma.api.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record CompileError(String message, Context context, List<CompileError> errors) implements Error {
    public CompileError(String message, Context context) {
        this(message, context, new ArrayList<>());
    }

    @Override
    public String display() {
        final var joined = this.errors.stream().map(CompileError::display).collect(Collectors.joining());
        return this.message + ": " + this.context + joined;
    }
}
