package magma.app.maybe.string;

import magma.app.maybe.StringResult;
import magma.app.rule.OrState;

public record OkStringResult(String value) implements StringResult {
    @Override
    public String orElse(String other) {
        return this.value;
    }

    @Override
    public OrState<String> attachTo(OrState<String> state) {
        return state.withValue(this.value);
    }

    @Override
    public StringResult appendString(String other) {
        return new OkStringResult(this.value + other);
    }

    @Override
    public StringResult appendMaybe(StringResult other) {
        return other.prependString(this.value);
    }

    @Override
    public StringResult prependString(String other) {
        return new OkStringResult(other + this.value);
    }
}
