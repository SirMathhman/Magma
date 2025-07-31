package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
	private final Map<String, String> properties = new HashMap<>();

	@Override
	public Node withString(final String key, final String value) {
		this.properties.put(key, value);
		return this;
	}

	@Override
	public Optional<String> findString(final String key) {
		return Optional.ofNullable(this.properties.get(key));
	}
}