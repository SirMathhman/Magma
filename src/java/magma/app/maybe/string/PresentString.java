package magma.app.maybe.string;

import magma.app.maybe.MaybeString;

public record PresentString(String value) implements MaybeString {
    @Override
    public String orElse(String other) {
        return this.value;
    }

    @Override
    public MaybeString appendString(String other) {
        return new PresentString(this.value + other);
    }

    @Override
    public MaybeString appendMaybe(MaybeString other) {
        return other.prependString(this.value);
    }

    @Override
    public MaybeString prependString(String other) {
        return new PresentString(other + this.value);
    }
}
