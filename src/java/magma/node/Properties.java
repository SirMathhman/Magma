package magma.node;

import magma.Tuple;

import java.util.Optional;
import java.util.stream.Stream;

public interface Properties {
	Stream<Tuple<String, String>> stream();

	Optional<String> find(String key);

	Node with(String key, String value);
}
