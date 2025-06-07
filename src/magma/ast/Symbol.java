package magma.ast;

import magma.util.*;
import magma.compile.*;
/**
 * Represents a named symbol within generated code.
 */
public record Symbol(String value) implements Value, Type {
    @Override
    public String generate() {
        return value;
    }

}
