package magma.app;

import java.util.Optional;

public record InfixRule(StringRule leftRule, String infix, StringRule rightRule) {
    public Optional<String> generate(MapNode mapNode) {
        return Optional.of(this.leftRule.generate(mapNode).orElse("") + this.infix + this.rightRule.generate(mapNode).orElse("") + "\n");
    }
}