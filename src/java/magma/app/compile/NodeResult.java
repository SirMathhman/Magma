package magma.app.compile;

import magma.api.result.Result;

import java.util.function.Function;

public sealed interface NodeResult<Node, Error> extends MergeNodeResult<Node, NodeResult<Node, Error>>, TypeNodeResult<NodeResult<Node, Error>>, AttachableToStateResult<Node, Error> permits NodeErr, NodeOk {
    StringResult<Error, Result<String, Error>> generate(Function<Node, StringResult<Error, Result<String, Error>>> mapper);

    NodeResult<Node, Error> transform(Function<Node, Node> transformer);
}