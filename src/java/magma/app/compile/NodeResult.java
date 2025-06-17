package magma.app.compile;

import java.util.function.Function;

public sealed interface NodeResult<Node, Error, Iterable> extends MergeNodeResult<Node, NodeResult<Node, Error, Iterable>>,
        TypeNodeResult<NodeResult<Node, Error, Iterable>>,
        AttachableToStateResult<Accumulator<Node, Error, Iterable>> permits NodeErr, NodeOk {
    StringResult<Error, Iterable> generate(Function<Node, StringResult<Error, Iterable>> mapper);

    NodeResult<Node, Error, Iterable> transform(Function<Node, Node> transformer);
}