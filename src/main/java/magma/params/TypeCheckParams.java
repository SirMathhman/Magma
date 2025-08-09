package magma.params;

/**
 * Represents type compatibility check parameters.
 * This record reduces the number of parameters needed for the checkVariableTypeCompatibility method.
 */
public record TypeCheckParams(String variableName, String targetType, String targetName) {}
