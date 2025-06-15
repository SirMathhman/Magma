package magma.app.rule.or;

import magma.app.CompileError;

import java.util.List;
import java.util.function.Function;

public interface OrState<Value> {
    <Return> Return match(Function<Value, Return> whenPresent, Function<List<CompileError>, Return> whenMissing);

    OrState<Value> withValue(Value value);

    OrState<Value> withError(CompileError error);

    boolean hasValue();
}
