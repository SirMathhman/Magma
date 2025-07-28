package magma.node;

import magma.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class MapProperties implements Properties {
	final Map<String, String> entries = new HashMap<String, String>();

	private final Function<Properties, Node> completer;

	public MapProperties(final Function<Properties, Node> completer) {
		this.completer = completer;
	}

	@Override
	public Stream<Tuple<String, String>> stream() {
		return this.entries.entrySet().stream().map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
	}

	@Override
	public Optional<String> find(final String key) {
		return Optional.ofNullable(this.entries.get(key));
	}

	@Override
	public Node with(final String key, final String value) {
		this.entries.put(key, value);
		return this.completer.apply(this);
	}
}