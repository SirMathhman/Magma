package magma.node.result;

import magma.result.Matchable;

import java.util.Optional;
import java.util.function.Function;

public interface NodeResult<Node> extends Matchable<Node> {
    @Deprecated
    Optional<Node> toOptional();

    NodeResult<Node> map(Function<Node, Node> mapper);
}
