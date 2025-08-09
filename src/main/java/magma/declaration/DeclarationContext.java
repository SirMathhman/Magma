package magma.declaration;

/**
 * Represents all the information needed to process a variable declaration.
 * This record helps encapsulate the input string, variable name, value section, type suffix, and mutability.
 * The mutability flag indicates whether the variable can be reassigned after declaration.
 */
public record DeclarationContext(String input, String variableName, String valueSection, String typeSuffix, boolean mutable) {}
