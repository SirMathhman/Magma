package magma.app;

import magma.api.io.Error;

public record ApplicationError(Error error) implements Error {
    @Override
    public String display() {
        return this.error.display();
    }
}
