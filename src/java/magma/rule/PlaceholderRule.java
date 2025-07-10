package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record PlaceholderRule(StringRule rule) {
    public static String wrap(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }

    Optional<String> generate(final Node node) {
        return this.rule.generate(node).map(PlaceholderRule::wrap);
    }
}