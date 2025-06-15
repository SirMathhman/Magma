package magma.app.maybe.string;

import magma.app.maybe.StringResult;
import magma.app.maybe.StringResults;
import magma.app.rule.or.OrState;

import java.util.Objects;

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
        return StringResults.createFromValue(this.value + other);
    }

    @Override
    public StringResult<Error> appendMaybe(StringResult<Error> other) {
        return other.prependString(this.value);
    }

    @Override
    public StringResult<Error> prependString(String other) {
        return StringResults.createFromValue(other + this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (OkStringResult) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public String toString() {
        return "OkStringResult[" + "value=" + this.value + ']';
    }

}
