package magma.node;

/**
 * Record to hold a parsed struct identifier and its position in the code.
 */
public record StructIdentifier(String name, int position) {}