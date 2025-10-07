package magma.compile.context;

import magma.compile.rule.Slice;

public record InputContext(Slice context) implements Context {
	@Override
	public String display(int depth) {
		return context.value();
	}
}
