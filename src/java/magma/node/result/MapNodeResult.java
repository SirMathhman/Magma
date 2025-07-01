package magma.node.result;

import magma.compile.result.ResultFactory;

import java.util.function.Function;

public interface MapNodeResult<Node, Error, StringResult, S> {
    S mapValue(Function<Node, Node> mapper);

    S mapErr(String message, String context, ResultFactory<Node, Error, StringResult, S> factory);
}
