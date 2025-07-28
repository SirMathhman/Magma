package magma.error;

public record CompileError() implements Error {
	@Override
	public String display() {
		return "?";
	}
}
