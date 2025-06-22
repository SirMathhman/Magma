package magma.node.result;

import magma.node.Node;
import magma.option.Option;

import java.util.function.Function;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public <Return> Option<Return> flatMap(final Function<Node, Option<Return>> mapper) {
        return mapper.apply(this.node);
    }
}
