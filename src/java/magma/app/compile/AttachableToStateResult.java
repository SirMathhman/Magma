package magma.app.compile;

public interface AttachableToStateResult<Value, Error, Iterable> {
    Accumulator<Value, Error, Iterable> attachToState(Accumulator<Value, Error, Iterable> state);
}
