package magma.string.result;

import magma.error.CompileError;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.TypedNode;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record StringErr(FormatError error) implements StringResult {
    public static <Node extends TypedNode<Node>> StringResult create(final String message, final Node node) {
        return new StringErr(new CompileError(message, node.toString()));
    }

    public static StringErr createWithChildren(final String message, final EverythingNode node, final List<FormatError> errors) {
        return new StringErr(new CompileError(message, node.toString(), errors));
    }

    @Override
    public Optional<String> toOptional() {
        return Optional.empty();
    }

    @Override
    public StringResult appendResult(final StringResult other) {
        return this;
    }

    @Override
    public StringResult prependSlice(final String other) {
        return this;
    }

    @Override
    public StringResult appendSlice(final String slice) {
        return this;
    }

    @Override
    public StringResult flatMap(final Function<String, StringResult> mapper) {
        return this;
    }

    @Override
    public StringResult map(final Function<String, String> mapper) {
        return this;
    }

    @Override
    public <Return> Return match(final Function<String, Return> whenOk, final Function<FormatError, Return> whenErr) {
        return whenErr.apply(this.error);
    }
}
