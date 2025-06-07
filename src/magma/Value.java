package magma;

sealed interface Value extends Caller, ValueArgument permits FieldAccess, Invocation, Placeholder, Symbol {
}
