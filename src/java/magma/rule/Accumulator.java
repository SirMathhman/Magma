package magma.rule;

import magma.error.FormattedError;
import magma.list.ListLike;

import java.util.function.Function;

public interface Accumulator<Value> {
    Accumulator<Value> withValue(Value value);

    Accumulator<Value> withError(FormattedError error);

    <Return> Return match(Function<Value, Return> whenPresent, Function<ListLike<FormattedError>, Return> whenErr);

    boolean hasValue();
}
