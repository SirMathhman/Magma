package magma.app.compile.lang;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.divide.NodeListRule;
import magma.app.compile.rule.truncate.TruncateRule;

import java.util.List;

public class RuleBuilder {
    static Rule<NodeWithEverything> String(String value) {
        return new StringRule<>(value, new MapNodeFactory());
    }

    static Rule<NodeWithEverything> Strip(Rule<NodeWithEverything> rule) {
        return TruncateRule.createStripRule(rule);
    }

    static Rule<NodeWithEverything> Last(Rule<NodeWithEverything> parent, String infix, Rule<NodeWithEverything> child) {
        return new LastRule<>(parent, infix, child);
    }

    static Rule<NodeWithEverything> Suffix(Rule<NodeWithEverything> last, String suffix) {
        return TruncateRule.Suffix(last, suffix);
    }

    static Rule<NodeWithEverything> Prefix(Rule<NodeWithEverything> suffix) {
        return TruncateRule.Prefix("import ", suffix);
    }

    static Rule<NodeWithEverything> NodeList(List<Rule<NodeWithEverything>> children) {
        return new NodeListRule<>("children", new OrRule<>(children), new MapNodeFactory());
    }

    static Rule<NodeWithEverything> Empty() {
        return new EmptyRule<>(new MapNodeFactory());
    }
}
