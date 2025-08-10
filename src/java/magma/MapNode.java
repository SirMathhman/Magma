package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode {
	private final Map<String, String> strings = new HashMap<>();

	public MapNode withString(String key, String value) {
		strings.put(key, value);
		return this;
	}

	public Optional<String> find(String key) {
		return Optional.ofNullable(strings.get(key));
	}
}