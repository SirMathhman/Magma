package magma.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
	private final Map<String, String> properties = new HashMap<>();
	private String typeTag = null;
	
	@Override
	public Optional<String> type() {
		return Optional.ofNullable(this.typeTag);
	}
	
	@Override
	public Node retype(String type) {
		this.typeTag = type;
		return this;
	}
	
	@Override
	public boolean is(String type) {
		return type != null && type.equals(this.typeTag);
	}

	@Override
	public Node withString(final String key, final String value) {
		this.properties.put(key, value);
		return this;
	}

	@Override
	public Optional<String> findString(final String key) {
		return Optional.ofNullable(this.properties.get(key));
	}
	
	@Override
	public Node merge(final Node other) {
		MapNode result = new MapNode();
		
		// Copy properties from this node
		for (Map.Entry<String, String> entry : this.properties.entrySet()) {
			result.withString(entry.getKey(), entry.getValue());
		}
		
		// Copy properties from the other node
		// For each property we know about in this node, try to find it in the other node
		for (String key : this.properties.keySet()) {
			other.findString(key).ifPresent(value -> result.withString(key, value));
		}
		
		// For MapNode instances, we can directly access and copy all properties
		if (other instanceof MapNode mapNode) {
			for (Map.Entry<String, String> entry : mapNode.properties.entrySet()) {
				// Only copy if not already present (this node's properties take precedence)
				if (!result.properties.containsKey(entry.getKey())) {
					result.withString(entry.getKey(), entry.getValue());
				}
			}
		}
		
		// Handle type tag - prefer the other node's type if it has one
		other.type().ifPresentOrElse(
			result::retype,
			() -> this.type().ifPresent(result::retype)
		);
		
		return result;
	}
}