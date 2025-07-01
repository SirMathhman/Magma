package magma.node.result;

import magma.result.Matchable;

public interface NodeResult<Node, Error, StringResult> extends Matchable<Node, Error>,
        MapNodeResult<NodeResult<Node, Error, StringResult>, Node, Error, StringResult> {

}
