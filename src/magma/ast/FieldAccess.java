package magma.ast;

import magma.util.*;
import magma.compile.*;
/**
 * Accesses a field on a parent {@link Value}.
 */
public record FieldAccess(Value parent, String property) implements Value {
    @Override
    public String generate() {
        return parent.generate() + "." + property;
    }
}
