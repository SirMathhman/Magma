package magma.error;

public interface FormatError extends Error {
    @Override
    default String display() {
        return this.format(0);
    }

    String format(int depth);
}
