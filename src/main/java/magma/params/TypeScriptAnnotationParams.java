package magma.params;

import magma.declaration.DeclarationContext;

/**
 * Represents parameters for TypeScript annotation processing.
 * This record reduces the number of parameters needed for the processTypeScriptAnnotation method.
 */
public record TypeScriptAnnotationParams(String statement, DeclarationContext context, String variableName, String rawValue) {}
