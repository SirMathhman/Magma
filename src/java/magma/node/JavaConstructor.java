package magma.node;

public record JavaConstructor() implements JavaMethodHeader {
	@Override
	public String generate() {
		return "?";
	}
}
