package magma.app.compile.error;

import magma.app.compile.context.Context;

public record CompileError(String message, Context context) implements FormattedError {
    @Override
    public String format(int depth) {
        return this.message + ": " + this.context.display();
    }
}
