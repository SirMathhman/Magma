package magma.app.compile;

import magma.api.Error;

public interface TreeError extends Error {
    @Override
    default String display() {
        return this.format(0);
    }

    String format(int depth);
}
