package magma.node;

public record Placeholder(String value) implements JavaParameter {
	public static String wrap(final String input) {
		return "/*" + input + "*/";
	}

	@Override
	public String generate() {
		return Placeholder.wrap(this.value);
	}
}
