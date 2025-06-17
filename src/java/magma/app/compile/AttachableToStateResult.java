package magma.app.compile;

import magma.api.list.Streamable;

public interface AttachableToStateResult<Value, Error> {
    Accumulator<Value, Error, Streamable<Error>> attachToState(Accumulator<Value, Error, Streamable<Error>> state);
}
