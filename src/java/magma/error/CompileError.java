package magma.error;

import magma.list.ListLike;
import magma.list.ListLikes;

import java.util.stream.Collectors;

public record CompileError(String message, Context context, ListLike<FormattedError> errors) implements FormattedError {
    public CompileError(final String message, final Context context) {
        this(message, context, ListLikes.empty());
    }

    @Override
    public String format(final int depth) {
        final var joined = this.errors.stream()
                .map(compileError -> compileError.format(depth + 1))
                .map(result -> System.lineSeparator() + "\t".repeat(depth) + result)
                .collect(Collectors.joining());

        return this.message + ": " + this.context.display() + joined;
    }
}
