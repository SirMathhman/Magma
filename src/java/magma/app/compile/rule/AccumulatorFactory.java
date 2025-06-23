package magma.app.compile.rule;

public interface AccumulatorFactory<Error, Errors> {
    <Value> Accumulator<Value, Error, Errors> createAccumulator();
}
