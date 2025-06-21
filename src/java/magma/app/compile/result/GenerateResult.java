package magma.app.compile.result;

import magma.api.optional.OptionalLike;

import java.util.function.Supplier;

public interface GenerateResult {
    GenerateResult appendResult(Supplier<GenerateResult> other);

    GenerateResult prependSlice(String slice);

    OptionalLike<String> unwrap();

    GenerateResult appendSlice(String slice);
}
