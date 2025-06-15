package magma.app.maybe.string;

import magma.app.maybe.StringResult;
import magma.app.rule.or.OrState;

public record OkStringResult<Error>(String value) implements StringResult<Error> {
    @Override
    public String orElse(String other) {
        return this.value;
    }

    @Override
    public OrState<String, Error> attachTo(OrState<String, Error> state) {
        return state.withValue(this.value);
    }

    @Override
    public StringResult<Error> appendString(String other) {
        return new OkStringResult<Error>(this.value + other);
    }

    @Override
    public StringResult<Error> appendMaybe(StringResult<Error> other) {
        return other.prependString(this.value);
    }

    @Override
    public StringResult<Error> prependString(String other) {
        return new OkStringResult<Error>(other + this.value);
    }
}
