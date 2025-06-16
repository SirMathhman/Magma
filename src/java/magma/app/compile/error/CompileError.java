package magma.app.compile.error;

import magma.api.Error;
import magma.app.compile.context.Context;

import java.util.ArrayList;
import java.util.List;

public record CompileError(String message, Context context, List<CompileError> errors) implements Error {
    public CompileError(String message, Context context) {
        this(message, context, new ArrayList<>());
    }

    @Override
    public String display() {
        return this.message + ": " + this.context.display();
    }
}
