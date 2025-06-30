package magma.rule.accumulate;

import magma.error.CompileError;

import java.util.List;
import java.util.function.Function;

public interface Accumulator<Node> {
    boolean isPresent();

    Accumulator<Node> withValue(Node value);

    Accumulator<Node> withError(CompileError error);

    <Return> Return match(Function<Node, Return> whenOk, Function<List<CompileError>, Return> whenErr);
}
