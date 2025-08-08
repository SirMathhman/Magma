package magma;

/**
 * Represents all the information needed to process a variable declaration.
 * This record helps encapsulate the input string, variable name, value section, and type suffix.
 */
record DeclarationContext(String input, String variableName, String valueSection, String typeSuffix) {}
