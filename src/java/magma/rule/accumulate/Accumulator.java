package magma.rule.accumulate;

import magma.error.FormatError;

import java.util.List;
import java.util.function.Function;

public interface Accumulator<Node> {
    boolean isPresent();

    Accumulator<Node> withValue(Node value);

    Accumulator<Node> withError(FormatError error);

    <Return> Return match(Function<Node, Return> whenOk, Function<List<FormatError>, Return> whenErr);
}
