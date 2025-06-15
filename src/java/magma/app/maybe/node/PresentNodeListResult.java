package magma.app.maybe.node;

import magma.app.maybe.AttachableToNodeListResult;
import magma.app.maybe.NodeListResult;
import magma.app.maybe.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PresentNodeListResult<Node, Error> implements NodeListResult<Node, Error> {
    private final List<Node> nodes;

    public PresentNodeListResult() {
        this(new ArrayList<>());
    }

    public PresentNodeListResult(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public NodeListResult<Node, Error> add(AttachableToNodeListResult<Node, Error> node) {
        return node.attachTo(this.nodes);
    }

    @Override
    public NodeListResult<Node, Error> transform(Function<List<Node>, List<Node>> mapper) {
        return new PresentNodeListResult<>(mapper.apply(this.nodes));
    }

    @Override
    public StringResult<Error> generate(Function<List<Node>, StringResult<Error>> generator) {
        return generator.apply(this.nodes);
    }
}
