package magma.node.result;

import magma.error.CompileError;

import java.util.function.Function;

public interface Matching<Value> {
    <Return> Return match(Function<Value, Return> whenOk, Function<CompileError, Return> whenError);
}
