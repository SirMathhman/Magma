package magma.rule.accumulate;

import java.util.List;
import java.util.function.Function;

public interface Accumulator<Node, Error> {
    boolean isPresent();

    Accumulator<Node, Error> withValue(Node value);

    Accumulator<Node, Error> withError(Error error);

    <Return> Return match(Function<Node, Return> whenOk, Function<List<Error>, Return> whenErr);
}
