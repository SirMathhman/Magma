package magma.app.maybe.node;

import magma.app.maybe.NodeResult;
import magma.app.maybe.NodeListResult;
import magma.app.maybe.StringResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PresentNodeListResult<Node> implements NodeListResult<Node> {
    private final List<Node> nodes;

    public PresentNodeListResult() {
        this(new ArrayList<>());
    }

    public PresentNodeListResult(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public NodeListResult<Node> add(NodeResult<Node> node) {
        return node.addTo(this.nodes);
    }

    @Override
    public NodeListResult<Node> transform(Function<List<Node>, List<Node>> mapper) {
        return new PresentNodeListResult<Node>(mapper.apply(this.nodes));
    }

    @Override
    public StringResult generate(Function<List<Node>, StringResult> generator) {
        return generator.apply(this.nodes);
    }
}
