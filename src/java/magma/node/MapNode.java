package magma.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
	private final Map<String, List<Node>> nodeLists = new HashMap<>();
	private Properties strings;
	private Optional<String> maybeType = Optional.empty();

	public MapNode() {
		this.strings = new MapProperties(strings -> {
			this.strings = strings;
			return this;
		});
	}

	@Override
	public Node merge(final Node other) {
		return other.strings()
								.stream()
								.<Node>reduce(this, (mapNode, entry) -> mapNode.strings().with(entry.left(), entry.right()),
															(_, next) -> next);
	}

	@Override
	public Node retype(final String type) {
		this.maybeType = Optional.of(type);
		return this;
	}

	@Override
	public boolean is(final String type) {
		return this.maybeType.isPresent() && this.maybeType.get().contentEquals(type);
	}

	@Override
	public Node withNodeList(final String key, final List<Node> values) {
		this.nodeLists.put(key, values);
		return this;
	}

	@Override
	public Optional<List<Node>> findNodeList(final String key) {
		return Optional.ofNullable(this.nodeLists.get(key));
	}

	@Override
	public String toString() {
		return "MapNode{" + "maybeType=" + this.maybeType + ", strings=" + this.strings() + ", nodeLists=" +
					 this.nodeLists + '}';
	}

	@Override
	public Properties strings() {
		return this.strings;
	}
}