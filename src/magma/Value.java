package magma;

/**
 * A runtime value which can also act as a {@link Caller}.
 */
sealed interface Value extends Caller, ValueArgument permits FieldAccess, Invocation, Placeholder, Symbol {
}
