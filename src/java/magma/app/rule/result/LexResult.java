package magma.app.rule.result;

import magma.app.MapNode;

import java.util.Optional;
import java.util.function.Function;

public record LexResult(Optional<MapNode> value) {
    public MapNode orElse(MapNode other) {
        return this.value.orElse(other);
    }

    public LexResult flatMap(Function<MapNode, LexResult> mapper) {
        return new LexResult(this.value.flatMap(mapNode -> mapper.apply(mapNode).value));
    }
}
