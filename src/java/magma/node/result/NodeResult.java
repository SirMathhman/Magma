package magma.node.result;

import magma.compile.result.ResultFactory;
import magma.result.Matchable;

import java.util.function.Function;

public interface NodeResult<Node, Error, StringResult> extends Matchable<Node, Error> {
    NodeResult<Node, Error, StringResult> mapValue(Function<Node, Node> mapper);

    NodeResult<Node, Error, StringResult> flatMap(Function<Node, NodeResult<Node, Error, StringResult>> mapper);

    NodeResult<Node, Error, StringResult> mapErr(String message,
                                                 String context,
                                                 ResultFactory<Node, Error, StringResult> factory);
}
