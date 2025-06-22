package magma.error;

import magma.list.ListLike;
import magma.list.ListLikes;

public record CompileError(String message, Context context, ListLike<CompileError> errors) implements Error {
    public CompileError(final String message, final Context context) {
        this(message, context, ListLikes.empty());
    }

    @Override
    public String display() {
        return this.message + ": " + this.context.display();
    }
}
