package magma.compile.rule;

public record ValueFolder() implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == ',') return state.advance(); return state;
	}
}
