package magma.node.result;

import magma.compile.result.ResultFactory;

import java.util.function.Function;

public interface MapNodeResult<Self, Node, Error, StringResult> {
    Self mapValue(Function<Node, Node> mapper);

    Self mapErr(String message, String context, ResultFactory<Node, Error, StringResult, Self> factory);

    Self flatMap(Function<Node, Self> mapper);
}
