package magma.diagnostics;

public record CompileError(String message) implements Error_ {
}
