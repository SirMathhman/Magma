package magma.app.maybe;

import magma.app.MaybeString;

public class EmptyString implements MaybeString {
    @Override
    public String orElse(String other) {
        return other;
    }
}
