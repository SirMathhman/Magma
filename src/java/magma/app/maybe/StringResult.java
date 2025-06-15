package magma.app.maybe;

import magma.app.maybe.string.Appendable;
import magma.app.maybe.string.Prependable;

public interface StringResult extends Appendable<StringResult>, Prependable<StringResult>, Attachable<String> {
    String orElse(String other);
}
