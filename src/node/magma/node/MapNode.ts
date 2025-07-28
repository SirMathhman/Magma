/*import java.util.HashMap;*/
/*import java.util.Map;*/
/*import java.util.Optional;*/
/*import java.util.Set;*/
/*public final*/class MapNode implements Node {/*
	private final Map<String, String> strings = new HashMap<>();
	private Optional<String> maybeType = Optional.empty();

	@Override
	public Node withString(final String key, final String value) {
		this.strings.put(key, value);
		return this;
	}

	@Override
	public Optional<String> findString(final String key) {
		return Optional.ofNullable(this.strings.get(key));
	}

	@Override
	public Node merge(final Node other) {
		return other.streamStrings()
								.stream()
								.<Node>reduce(this, (mapNode, entry) -> mapNode.withString(entry.getKey(), entry.getValue()),
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
	public Set<Map.Entry<String, String>> streamStrings() {
		return this.strings.entrySet();
	}
}*/
