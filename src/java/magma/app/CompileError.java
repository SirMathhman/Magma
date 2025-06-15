package magma.app;

import java.util.ArrayList;
import java.util.List;

public record CompileError(String message, Context context, List<CompileError> errors) {
    public CompileError(String message, Context context) {
        this(message, context, new ArrayList<>());
    }
}
