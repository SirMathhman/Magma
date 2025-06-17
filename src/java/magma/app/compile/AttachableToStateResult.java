package magma.app.compile;

import magma.api.list.Iterable;

public interface AttachableToStateResult<Value, Error> {
    Accumulator<Value, Error, Iterable<Error>> attachToState(Accumulator<Value, Error, Iterable<Error>> state);
}
