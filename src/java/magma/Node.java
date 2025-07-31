package magma;

import java.util.Optional;

public interface Node {
	Node withString(String key, String value);

	Optional<String> findString(String key);
}
