package magma.app.compile.error;

import magma.api.Error;
import magma.app.compile.context.Context;

import java.util.ArrayList;
import java.util.List;

public record CompileError(String message, Context context, List<Error> errors) implements FormattedError {
    public CompileError(String message, Context context) {
        this(message, context, new ArrayList<>());
    }

    @Override
    public String format(int depth) {
        return "\t".repeat(depth) + this.message + ": " + this.context.display();
    }
}
