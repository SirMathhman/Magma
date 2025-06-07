package magma.ast;

import magma.util.*;
import magma.compile.*;
/**
 * Represents a type that can generate code and be used as a type argument.
 */
public interface Type extends Generating, TypeArgument {
    default Map<String, Type> extract(Type actual) {
        return Maps.empty();
    }

    default Type resolve(Map<String, Type> resolved) {
        return this;
    }
}
