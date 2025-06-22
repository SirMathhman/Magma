package magma.node.result;

import magma.node.Node;
import magma.string.StringErr;
import magma.string.StringResult;

import java.util.function.Function;

public record NodeErr() implements NodeResult {
    @Override
    public StringResult flatMap(final Function<Node, StringResult> mapper) {
        return new StringErr();
    }
}
