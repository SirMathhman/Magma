package magma.app.compile;

import magma.api.collect.iter.Iterable;

public interface AttachableToStateResult<Value, Error> {
    Accumulator<Value, Error, Iterable<Error>> attachToState(Accumulator<Value, Error, Iterable<Error>> state);
}
