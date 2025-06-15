package magma.app.maybe;

import magma.app.maybe.string.Appendable;
import magma.app.maybe.string.Prependable;

public interface MaybeString extends Appendable<MaybeString>, Prependable<MaybeString>, Attachable<String> {
    String orElse(String other);
}
