package magma.node.result;

import magma.node.Node;
import magma.option.None;
import magma.option.Option;

import java.util.function.Function;

public record NodeErr() implements NodeResult {
    @Override
    public <Return> Option<Return> flatMap(final Function<Node, Option<Return>> mapper) {
        return new None<>();
    }
}
