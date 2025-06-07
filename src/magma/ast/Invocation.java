package magma.ast;

import magma.util.*;
import magma.Main;
import magma.compile.*;
/**
 * Invocation of a {@link Caller} with a list of argument values.
 */
public record Invocation(Caller caller, List<Value> arguments) implements Value {
    @Override
    public String generate() {
        return caller.generate() + "(" + Main.generateNodes(arguments) + ")";
    }
}
