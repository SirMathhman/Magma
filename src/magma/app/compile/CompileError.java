package magma.app.compile;

public interface CompileError {
    String format();

    String formatWithoutContext();
}
