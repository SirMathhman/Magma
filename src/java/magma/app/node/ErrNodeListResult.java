package magma.app.node;

import magma.app.Error;
import magma.app.AttachableToNodeListResult;
import magma.app.NodeListResult;
import magma.app.StringResult;
import magma.app.string.StringResults;

import java.util.List;
import java.util.function.Function;

public class ErrNodeListResult<Node, E extends Error> implements NodeListResult<Node, E> {
    private final E error;

    public ErrNodeListResult(E error) {
        this.error = error;
    }

    @Override
    public NodeListResult<Node, E> add(AttachableToNodeListResult<Node, E> node) {
        return this;
    }

    @Override
    public NodeListResult<Node, E> transform(Function<List<Node>, List<Node>> mapper) {
        return this;
    }

    @Override
    public StringResult<E> generate(Function<List<Node>, StringResult<E>> generator) {
        return StringResults.createFromError(this.error);
    }
}
