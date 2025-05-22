package magmac.compile;

import java.util.Optional;

public record StripRule(String leftKey, Rule rule, String rightKey) implements Rule {
    public StripRule(Rule rule) {
        this("", rule, "");
    }

    @Override
    public Optional<String> generate(MapNode node) {
        var leftString = node.findString(this.leftKey).orElse("");
        var rightString = node.findString(this.rightKey).orElse("");
        return this.rule.generate(node).map(generated -> leftString + generated + rightString);
    }

    @Override
    public Optional<MapNode> parse(String input) {
        return this.rule.parse(input.strip());
    }
}
