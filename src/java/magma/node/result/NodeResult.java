package magma.node.result;

import magma.error.CompileError;

import java.util.Optional;
import java.util.function.Function;

public interface NodeResult<Node> {
    @Deprecated
    Optional<Node> toOptional();

    <Return> Return match(Function<Node, Return> whenPresent, Function<CompileError, Return> whenErr);

    NodeResult<Node> map(Function<Node, Node> mapper);
}
