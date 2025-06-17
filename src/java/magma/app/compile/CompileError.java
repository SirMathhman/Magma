package magma.app.compile;

import magma.api.list.Lists;
import magma.api.list.Streamable;

public record CompileError(String message, Context context,
                           Streamable<FormattedError> errors) implements FormattedError {
    public CompileError(String message, Context context) {
        this(message, context, Lists.empty());
    }

    @Override
    public String format(int depth) {
        return "\t".repeat(depth) + this.message + ": " + this.context.display();
    }
}
