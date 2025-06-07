package magma.ast;

/**
 * A runtime value which can also act as a {@link Caller}.
 */
public sealed interface Value extends Caller, ValueArgument permits FieldAccess, Invocation, Placeholder, Symbol {
}
