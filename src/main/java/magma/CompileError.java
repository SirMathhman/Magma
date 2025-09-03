package magma;

/**
 * Simple compile error container.
 */
public record CompileError(String message, String originalInput) {
}
