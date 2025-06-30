package magma.node.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface NodeResult<Node> {
    @Deprecated
    Optional<Node> toOptional();

    <Return> Return match(Function<Node, Return> whenPresent, Supplier<Return> whenErr);
}
