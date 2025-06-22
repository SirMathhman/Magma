package magma.node.result;

import magma.node.Node;
import magma.string.StringResult;

import java.util.function.Function;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public StringResult generate(final Function<Node, StringResult> mapper) {
        return mapper.apply(this.node);
    }
}
