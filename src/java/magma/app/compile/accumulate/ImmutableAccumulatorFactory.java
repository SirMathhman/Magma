package magma.app.compile.accumulate;

import magma.api.error.list.ErrorSequence;

public class ImmutableAccumulatorFactory<Error> implements AccumulatorFactory<Error, ErrorSequence<Error>> {
    @Override
    public <Value> Accumulator<Value, Error, ErrorSequence<Error>> createAccumulator() {
        return new ImmutableAccumulator<>();
    }
}