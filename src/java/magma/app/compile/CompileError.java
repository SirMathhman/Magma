package magma.app.compile;

import magma.app.compile.context.Context;

public record CompileError(String message, Context context) {
}
