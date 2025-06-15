package magma.app.maybe.string;

import magma.app.maybe.MaybeString;

public class EmptyString implements MaybeString {
    @Override
    public String orElse(String other) {
        return other;
    }
}
