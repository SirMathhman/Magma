package magma.ast;

import magma.util.*;
import magma.compile.*;
/**
 * A runtime value which can also act as a {@link Caller}.
 */
public sealed interface Value extends Caller, ValueArgument permits FieldAccess, Invocation, Placeholder, Symbol {
}
