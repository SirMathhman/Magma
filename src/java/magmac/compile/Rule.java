package magmac.compile;

import java.util.Optional;

public interface Rule {
    Optional<MapNode> parse(String input);

    Optional<String> generate(MapNode node);
}
