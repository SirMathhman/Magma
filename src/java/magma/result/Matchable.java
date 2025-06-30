package magma.result;

import magma.error.CompileError;

import java.util.function.Function;

public interface Matchable<Value> {
    <Return> Return match(Function<Value, Return> whenPresent, Function<CompileError, Return> whenErr);
}
