package magma.app.rule.result;

import magma.app.node.Node;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface LexResult {
    LexResult flatMap(Function<Node, LexResult> mapper);

    LexResult merge(Supplier<LexResult> other);

    LexResult mapValue(Function<Node, Node> mapper);

    Optional<Node> findValue();
}
