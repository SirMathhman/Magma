package magma.error;

public record CompileError(String message, Context context,
                           ErrorList<FormattedError> errors) implements FormattedError {
    public CompileError(final String message, final Context context) {
        this(message, context, new ImmutableErrorList<>());
    }

    @Override
    public String format(final int depth) {
        final var joined = this.errors.join(error -> System.lineSeparator() + "\t".repeat(depth) + error.format(depth + 1));
        return this.message + ": " + this.context.display() + joined;
    }

}
