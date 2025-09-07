package magma.compiler;

/**
 * Simple compile error that contains a message and the source representation
 * (the input that caused the error).
 */
public record CompileError(String message, String source) {
}
