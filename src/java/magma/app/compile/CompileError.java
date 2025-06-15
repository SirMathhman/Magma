package magma.app.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record CompileError(String message, Context context, List<CompileError> errors) implements TreeError {
    public CompileError(String message, Context context) {
        this(message, context, new ArrayList<>());
    }

    @Override
    public String format(int depth) {
        final var joined = this.errors.stream()
                .map(compileError -> compileError.format(depth + 1))
                .collect(Collectors.joining());

        return "\t".repeat(depth) + this.message + ": " + this.context.display() + joined;
    }
}
