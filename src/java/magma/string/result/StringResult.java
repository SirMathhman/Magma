package magma.string.result;

import magma.error.CompileError;

import java.util.Optional;
import java.util.function.Function;

public interface StringResult {
    @Deprecated
    Optional<String> toOptional();

    StringResult appendResult(StringResult other);

    StringResult prepend(String other);

    StringResult appendSlice(String slice);

    <Return> Return match(Function<String, Return> whenOk, Function<CompileError, Return> whenErr);
}
