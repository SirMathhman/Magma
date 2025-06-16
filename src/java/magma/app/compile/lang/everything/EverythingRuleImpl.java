package magma.app.compile.lang.everything;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.Optional;

public record EverythingRuleImpl(Rule<NodeWithEverything> rule) implements EverythingRule {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return this.rule.lex(input);
    }

    @Override
    public Optional<String> generate(NodeWithEverything nodeWithEverything) {
        return this.rule.generate(nodeWithEverything);
    }
}
