package magma.error;

import magma.error.context.Context;
import magma.string.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CompileError implements FormatError {
    private final String message;
    private final Context context;
    private final List<FormatError> errors;

    public CompileError(final String message, final Context context, final List<FormatError> errors) {
        this.message = message;
        this.context = context;
        this.errors = new ArrayList<>(errors);
    }

    public CompileError(final String message, final Context context) {
        this(message, context, Collections.emptyList());
    }

    @Override
    public String format(final int depth) {
        final var joined = this.errors.stream()
                                      .map(compileError -> compileError.format(depth + 1))
                                      .map(value -> Strings.LINE_SEPARATOR + "\t".repeat(depth) + value)
                                      .collect(Collectors.joining());

        return this.message + ": " + this.context.display() + joined;
    }
}
