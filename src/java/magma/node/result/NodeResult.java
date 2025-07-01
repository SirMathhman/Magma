package magma.node.result;

import magma.result.Matchable;

import java.util.function.Function;

public interface NodeResult<Node, Error> extends Matchable<Node, Error> {
    NodeResult<Node, Error> mapValue(Function<Node, Node> mapper);

    NodeResult<Node, Error> flatMap(Function<Node, NodeResult<Node, Error>> mapper);

    NodeResult<Node, Error> mapErr(String message, String context);
}
