package magma.api.error;

public interface FormattedError extends Error {
    @Override
    default String display() {
        return this.format(0);
    }

    String format(int depth);
}
