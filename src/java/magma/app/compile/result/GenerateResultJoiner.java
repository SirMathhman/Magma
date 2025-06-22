package magma.app.compile.result;

import magma.api.collect.stream.Collector;

public record GenerateResultJoiner() implements Collector<StringResult, StringResult> {
    @Override
    public StringResult createInitial() {
        return new StringOk("");
    }

    @Override
    public StringResult fold(final StringResult current, final StringResult other) {
        return current.appendResult(() -> other);
    }
}
