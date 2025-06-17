package magma.app.compile.error;

import magma.app.compile.rule.or.OrState;

public interface AttachableToStateResult<Value> {
    OrState<Value, FormattedError> attachToState(OrState<Value, FormattedError> state);
}
