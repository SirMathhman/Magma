package magma.api.error;

public record WrappedError(Error error) implements Error {
    @Override
    public String display() {
        return this.error.display();
    }
}
