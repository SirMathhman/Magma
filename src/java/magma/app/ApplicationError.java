package magma.app;

import magma.api.Error;

public record ApplicationError(magma.api.Error error) implements Error {
    @Override
    public String display() {
        return this.error.display();
    }
}
