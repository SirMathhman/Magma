package magma.app.compile;

import magma.app.compile.rule.or.Accumulator;

public interface AttachableToStateResult<Value, Error> {
    Accumulator<Value, Error> attachToState(Accumulator<Value, Error> state);
}
