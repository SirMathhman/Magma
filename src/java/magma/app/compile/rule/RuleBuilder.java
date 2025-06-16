package magma.app.compile.rule;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.divide.NodeListRule;
import magma.app.compile.rule.truncate.TruncateRule;

import java.util.List;

public class RuleBuilder {
    public static Rule<NodeWithEverything> String(String value) {
        return new StringRule<>(value, new MapNodeFactory());
    }

    public static Rule<NodeWithEverything> Strip(Rule<NodeWithEverything> rule) {
        return TruncateRule.createStripRule(rule);
    }

    public static Rule<NodeWithEverything> Last(Rule<NodeWithEverything> parent, String infix, Rule<NodeWithEverything> child) {
        return new LastRule<>(parent, infix, child);
    }

    public static Rule<NodeWithEverything> Suffix(Rule<NodeWithEverything> last, String suffix) {
        return TruncateRule.Suffix(last, suffix);
    }

    public static Rule<NodeWithEverything> Prefix(Rule<NodeWithEverything> suffix) {
        return TruncateRule.Prefix("import ", suffix);
    }

    public static Rule<NodeWithEverything> NodeList(List<Rule<NodeWithEverything>> children) {
        return new NodeListRule<>("children", new OrRule<>(children), new MapNodeFactory());
    }

    public static Rule<NodeWithEverything> Empty() {
        return new EmptyRule<>(new MapNodeFactory());
    }
}
