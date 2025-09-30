package magma.compile.error;

public record ApplicationError(Error error) implements Error {
	public String display() {
		return error.display();
	}
}
