package magma.app.compile;

import magma.api.List;

public record CompileError(String message, Context context, List<FormattedError> errors) implements FormattedError {
    public CompileError(String message, Context context) {
        this(message, context, List.empty());
    }

    @Override
    public String format(int depth) {
        return "\t".repeat(depth) + this.message + ": " + this.context.display();
    }
}
