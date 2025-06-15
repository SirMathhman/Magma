package magma.app.maybe.node;

import magma.app.maybe.AttachableToNodeListResult;
import magma.app.maybe.NodeListResult;
import magma.app.maybe.StringResult;
import magma.app.maybe.StringResults;

import java.util.List;
import java.util.function.Function;

public class ErrNodeListResult<Node, Error> implements NodeListResult<Node, Error> {
    private final Error error;

    public ErrNodeListResult(Error error) {
        this.error = error;
    }

    @Override
    public NodeListResult<Node, Error> add(AttachableToNodeListResult<Node, Error> node) {
        return this;
    }

    @Override
    public NodeListResult<Node, Error> transform(Function<List<Node>, List<Node>> mapper) {
        return this;
    }

    @Override
    public StringResult<Error> generate(Function<List<Node>, StringResult<Error>> generator) {
        return StringResults.createFromError(this.error);
    }
}
