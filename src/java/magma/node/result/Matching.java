package magma.node.result;

import magma.error.FormattedError;

import java.util.function.Function;

public interface Matching<Value> {
    <Return> Return match(Function<Value, Return> whenOk, Function<FormattedError, Return> whenError);
}
