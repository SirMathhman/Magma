/*import java.util.HashMap;*/
/*import java.util.Map;*/
/*import java.util.Optional;*/
/*import java.util.Set;*/
/*public final*/class MapNode {/*
	private final Map<String, String> strings = new HashMap<>();

	public MapNode withString(final String key, final String value) {
		this.strings.put(key, value);
		return this;
	}

	public Optional<String> findString(final String key) {
		return Optional.ofNullable(this.strings.get(key));
	}

	public MapNode merge(final MapNode other) {
		return other.streamStrings()
								.stream()
								.reduce(this, (mapNode, entry) -> mapNode.withString(entry.getKey(), entry.getValue()),
												(_, next) -> next);
	}

	private Set<Map.Entry<String, String>> streamStrings() {
		return this.strings.entrySet();
	}
}*/
