package magma.node.result;

import magma.result.Matchable;

import java.util.function.Function;

public interface NodeResult<Node, Error, StringResult> extends Matchable<Node, Error>,
        MapNodeResult<Node, Error, StringResult, NodeResult<Node, Error, StringResult>> {

    NodeResult<Node, Error, StringResult> flatMap(Function<Node, NodeResult<Node, Error, StringResult>> mapper);

}
