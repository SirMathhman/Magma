package magma.app.compile.error;

import magma.app.compile.rule.or.OrState;

public sealed interface StringResult extends AppendableStringResult<StringResult>, PrependStringResult<StringResult> permits StringErr, StringOk {
    OrState<String, FormattedError> attachToState(OrState<String, FormattedError> state);
}
