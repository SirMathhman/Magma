package magma.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

final class MapProperties<T> implements Properties<T> {
	private final Map<String, T> map = new HashMap<>();

	@Override
	public Properties<T> with(final String key, final T value) {
		this.map.put(key, value);
		return this;
	}

	@Override
	public Optional<T> find(final String key) {
		return Optional.ofNullable(this.map.get(key));
	}

	@Override
	public Stream<Map.Entry<String, T>> stream() {
		return this.map.entrySet().stream();
	}

	@Override
	public boolean has(final String key) {
		return this.map.containsKey(key);
	}
}
