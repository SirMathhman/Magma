package magma.app.maybe.string;

import magma.app.maybe.MaybeString;

public record PresentString(String value) implements MaybeString {
    @Override
    public String orElse(String other) {
        return this.value;
    }
}
