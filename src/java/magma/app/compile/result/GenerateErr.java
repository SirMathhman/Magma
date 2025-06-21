package magma.app.compile.result;

import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;

import java.util.function.Supplier;

public record GenerateErr() implements GenerateResult {
    @Override
    public GenerateResult appendResult(final Supplier<GenerateResult> other) {
        return this;
    }

    @Override
    public GenerateResult prependSlice(final String slice) {
        return this;
    }

    @Override
    public OptionalLike<String> unwrap() {
        return Optionals.empty();
    }

    @Override
    public GenerateResult appendSlice(final String slice) {
        return this;
    }
}
