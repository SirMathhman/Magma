package magma;

import java.util.Map;

/**
 * Configuration record for DeclarationProcessor.
 * <p>
 * This record holds the required dependencies for the DeclarationProcessor class,
 * allowing the constructor to take fewer parameters while still providing
 * all the necessary components.
 */
public record DeclarationConfig(TypeMapper typeMapper, ValueProcessor valueProcessor,
																Map<String, String> variableTypes) {}