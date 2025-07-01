package magma.node.result;

import magma.compile.result.ResultFactory;
import magma.result.Matchable;

public interface NodeResult<Node, Error, StringResult> extends Matchable<Node, Error>,
        MapNodeResult<NodeResult<Node, Error, StringResult>, Node, ResultFactory<Node, Error, StringResult, NodeResult<Node, Error, StringResult>>> {
}
