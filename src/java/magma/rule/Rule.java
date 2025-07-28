package magma.rule;

import magma.MapNode;

import java.util.Optional;

public interface Rule {
	Optional<String> generate(MapNode mapNode);
}
