package magma.node.result;

import magma.error.FormatError;
import magma.result.Matchable;

import java.util.Optional;
import java.util.function.Function;

public interface NodeResult<Node> extends Matchable<Node, FormatError> {
    @Deprecated
    Optional<Node> toOptional();

    NodeResult<Node> map(Function<Node, Node> mapper);

    NodeResult<Node> flatMap(Function<Node, NodeResult<Node>> mapper);
}
