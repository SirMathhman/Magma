package magma.rule;

import magma.error.CompileError;
import magma.list.ListLike;

import java.util.function.Function;

public interface Accumulator<Value> {
    Accumulator<Value> withValue(Value value);

    Accumulator<Value> withError(CompileError error);

    <Return> Return match(Function<Value, Return> whenPresent, Function<ListLike<CompileError>, Return> whenErr);

    boolean hasValue();
}
