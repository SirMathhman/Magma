package magma.app.compile.result;

import magma.api.result.Result;

import java.util.function.Supplier;

public interface StringResult {
    StringResult appendResult(Supplier<StringResult> other);

    StringResult prependSlice(String slice);

    Result<String, CompileError> toResult();

    StringResult appendSlice(String slice);
}
