package magma.app;

import java.util.Optional;

public record StringRule(String key) {
    public Optional<String> generate(MapNode mapNode) {
        return mapNode.findString(this.key);
    }
}