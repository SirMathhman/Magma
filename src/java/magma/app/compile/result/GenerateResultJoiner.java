package magma.app.compile.result;

import magma.api.collect.stream.Collector;

public record GenerateResultJoiner() implements Collector<GenerateResult, GenerateResult> {
    @Override
    public GenerateResult createInitial() {
        return new GenerateOk("");
    }

    @Override
    public GenerateResult fold(final GenerateResult current, final GenerateResult other) {
        return current.appendResult(() -> other);
    }
}
