package magma.compile.rule;

public record ValueFolder() implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == ',' && state.isLevel()) return state.advance(); if (c == '<') return state.enter();
		if (c == '>') return state.exit(); return state.append(c);
	}
}
