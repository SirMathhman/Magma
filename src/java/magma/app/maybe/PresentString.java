package magma.app.maybe;

import magma.app.MaybeString;

public record PresentString(String value) implements MaybeString {
    @Override
    public String orElse(String other) {
        return this.value;
    }
}
