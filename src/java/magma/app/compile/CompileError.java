package magma.app.compile;

import magma.api.Error;
import magma.app.compile.context.Context;

public record CompileError(String message, Context context) implements Error {
    @Override
    public String display() {
        return this.message + ": " + this.context.display();
    }
}
