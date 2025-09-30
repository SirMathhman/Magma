package magma.compile.context;

public record StringContext(String context) implements Context {
	@Override
	public String display(int depth) {
		return context;
	}
}
