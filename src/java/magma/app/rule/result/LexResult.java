package magma.app.rule.result;

import magma.app.node.CompoundNode;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface LexResult {
    LexResult flatMap(Function<CompoundNode, LexResult> mapper);

    LexResult merge(Supplier<LexResult> other);

    LexResult mapValue(Function<CompoundNode, CompoundNode> mapper);

    Optional<CompoundNode> findValue();
}
