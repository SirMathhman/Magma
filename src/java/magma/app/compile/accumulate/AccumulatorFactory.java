package magma.app.compile.accumulate;

public interface AccumulatorFactory<Error, Errors> {
    <Value> Accumulator<Value, Error, Errors> createAccumulator();
}
