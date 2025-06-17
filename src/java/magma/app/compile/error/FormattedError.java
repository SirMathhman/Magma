package magma.app.compile.error;

import magma.api.Error;

public interface FormattedError extends Error {
    @Override
    default String display() {
        return this.format(0);
    }

    String format(int depth);
}
