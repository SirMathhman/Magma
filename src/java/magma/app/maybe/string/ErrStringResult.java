package magma.app.maybe.string;

import magma.app.maybe.StringResult;
import magma.app.rule.or.OrState;

public class ErrStringResult<Error> implements StringResult<Error> {
    private final Error error;

    public ErrStringResult(Error error) {
        this.error = error;
    }

    @Override
    public String orElse(String other) {
        return other;
    }

    @Override
    public OrState<String, Error> attachTo(OrState<String, Error> state) {
        return state.withError(this.error);
    }

    @Override
    public StringResult<Error> appendString(String other) {
        return this;
    }

    @Override
    public StringResult<Error> appendMaybe(StringResult<Error> other) {
        return this;
    }

    @Override
    public StringResult<Error> prependString(String other) {
        return this;
    }
}
