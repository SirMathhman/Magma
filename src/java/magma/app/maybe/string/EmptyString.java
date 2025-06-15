package magma.app.maybe.string;

import magma.app.maybe.MaybeString;

public class EmptyString implements MaybeString {
    @Override
    public String orElse(String other) {
        return other;
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
