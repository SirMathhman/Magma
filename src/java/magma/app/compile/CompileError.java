package magma.app.compile;

import magma.api.list.List;
import magma.api.list.Lists;

public record CompileError(String message, Context context, List<FormattedError> errors) implements FormattedError {
    public CompileError(String message, Context context) {
        this(message, context, Lists.empty());
    }

    @Override
    public String format(int depth) {
        return "\t".repeat(depth) + this.message + ": " + this.context.display();
    }
}
