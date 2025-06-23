package magma.app.compile.error;

import magma.api.error.Error;

public interface FormattedError extends Error {
    @Override
    default String display() {
        return format(0);
    }

    String format(int depth);
}
