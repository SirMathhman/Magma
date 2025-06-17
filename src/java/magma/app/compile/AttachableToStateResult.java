package magma.app.compile;

public interface AttachableToStateResult<Value, Error> {
    Accumulator<Value, Error> attachToState(Accumulator<Value, Error> state);
}
