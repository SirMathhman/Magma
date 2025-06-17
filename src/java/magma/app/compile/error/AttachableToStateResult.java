package magma.app.compile.error;

import magma.app.compile.rule.or.Accumulator;

public interface AttachableToStateResult<Value, Error> {
    Accumulator<Value, Error> attachToState(Accumulator<Value, Error> state);
}
