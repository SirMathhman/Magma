package magma.app.compile;

import magma.api.collect.iter.Iterable;

import java.util.function.Function;

public sealed interface NodeResult<Node, Error> extends MergeNodeResult<Node, NodeResult<Node, Error>>, TypeNodeResult<NodeResult<Node, Error>>, AttachableToStateResult<Accumulator<Node, Error, Iterable<Error>>> permits NodeErr, NodeOk {
    StringResult<Error, Iterable<Error>> generate(Function<Node, StringResult<Error, Iterable<Error>>> mapper);

    NodeResult<Node, Error> transform(Function<Node, Node> transformer);
}