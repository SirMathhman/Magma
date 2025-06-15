package magma.app.maybe.string;

import magma.app.maybe.MaybeString;
import magma.app.rule.OrState;

public class EmptyString implements MaybeString {
    @Override
    public String orElse(String other) {
        return other;
    }

    @Override
    public OrState<String> attachTo(OrState<String> state) {
        return state;
    }

    @Override
    public MaybeString appendString(String other) {
        return this;
    }

    @Override
    public MaybeString appendMaybe(MaybeString other) {
        return this;
    }

    @Override
    public MaybeString prependString(String other) {
        return this;
    }
}
