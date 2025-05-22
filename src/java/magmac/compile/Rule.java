package magmac.compile;

import java.util.Optional;
import java.util.function.Function;

public interface Rule extends Function<String, Optional<MapNode>> {
    Optional<String> generate(MapNode node);
}
