package magma;

/**
 * Represents type compatibility check parameters.
 * This record reduces the number of parameters needed for the checkVariableTypeCompatibility method.
 */
record TypeCheckParams(String variableName, String targetType, String targetName) {}
