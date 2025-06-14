package magma.app.compile;

import magma.app.compile.error.Context;

public record CompileError(String message, Context context) {
}
