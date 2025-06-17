package magma.app.compile;

public interface AttachableToStateResult<Accumulator> {
    Accumulator attachToAccumulator(Accumulator state);
}
