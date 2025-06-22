package magma.error;

import magma.list.ListLike;
import magma.list.ListLikes;

import java.util.stream.Collectors;

public record CompileError(String message, Context context, ListLike<CompileError> errors) implements Error {
    public CompileError(final String message, final Context context) {
        this(message, context, ListLikes.empty());
    }

    @Override
    public String display() {
        final var joined = this.errors.stream()
                .map(CompileError::display)
                .map(result -> System.lineSeparator() + "\t" + result)
                .collect(Collectors.joining());

        return this.message + ": " + this.context.display() + joined;
    }
}
