package magma.compile.rule;

public record ValueFolder() implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == ',' && state.isLevel()) return state.advance();
		if (c == '<' || c == '(') return state.enter().append(c);
		if (c == '>' || c == ')') return state.exit().append(c);
		return state.append(c);
	}

	@Override
	public String delimiter() {
		return ", ";
	}
}
