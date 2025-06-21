package magma.app.compile.result;

import magma.api.result.Result;

import java.util.function.Supplier;

public interface GenerateResult {
    GenerateResult appendResult(Supplier<GenerateResult> other);

    GenerateResult prependSlice(String slice);

    Result<String, CompileError> toResult();

    GenerateResult appendSlice(String slice);
}
