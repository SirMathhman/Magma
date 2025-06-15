package magma.app.maybe;

import magma.app.maybe.string.Appendable;
import magma.app.maybe.string.Prependable;

public interface StringResult<Error> extends Appendable<StringResult<Error>>, Prependable<StringResult<Error>>, AttachableToOrState<String, Error> {
    String orElse(String other);
}
