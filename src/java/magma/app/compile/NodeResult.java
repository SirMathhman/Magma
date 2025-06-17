package magma.app.compile;

import magma.api.result.Result;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node, Error> extends MergeNodeResult<Node, NodeResult<Node, Error>>, TypeNodeResult<NodeResult<Node, Error>>, AttachableToStateResult<Node, Error> {
    Result<List<Node>, Error> appendTo(List<Node> list);

    StringResult<Error> generate(Function<Node, StringResult<Error>> mapper);

    NodeResult<Node, Error> transform(Function<Node, Node> transformer);
}