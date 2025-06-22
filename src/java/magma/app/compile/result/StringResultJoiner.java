package magma.app.compile.result;

import magma.api.collect.stream.Collector;

public class StringResultJoiner implements Collector<StringResult, StringResult> {
    @Override
    public StringResult createInitial() {
        return new StringOk("");
    }

    @Override
    public StringResult fold(final StringResult stringResult, final StringResult stringResult2) {
        return stringResult.appendResult(() -> stringResult2);
    }
}
