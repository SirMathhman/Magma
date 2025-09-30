package magma.compile.context;

import magma.compile.Node;

public record NodeContext(Node node) implements Context {
	@Override
	public String display(int depth) {
		return node.format(depth);
	}
}
