package magma.app.compile.result;

import magma.api.io.Error;

public record CompileError(String message, String context) implements Error {
    @Override
    public String display() {
        return this.message + ": " + this.context;
    }
}
