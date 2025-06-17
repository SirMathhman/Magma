package magma.app.compile;

import magma.api.collect.iter.Iterable;

import java.util.function.Function;

public sealed interface NodeResult<Node, Error> extends MergeNodeResult<Node, NodeResult<Node, Error>>, TypeNodeResult<NodeResult<Node, Error>>, AttachableToStateResult<Node, Error, Iterable<Error>> permits NodeErr, NodeOk {
    StringResult<Error> generate(Function<Node, StringResult<Error>> mapper);

    NodeResult<Node, Error> transform(Function<Node, Node> transformer);
}