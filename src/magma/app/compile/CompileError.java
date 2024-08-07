package magma.app.compile;

public interface CompileError {
    String format(int depth);

    String formatWithoutContext();
}
