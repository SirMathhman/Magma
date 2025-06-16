package magma.app.compile.lang.build;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder {
    Rule<NodeWithEverything> String(String value);

    Rule<NodeWithEverything> Strip(Rule<NodeWithEverything> rule);

    Rule<NodeWithEverything> Last(Rule<NodeWithEverything> parent, String infix, Rule<NodeWithEverything> child);

    Rule<NodeWithEverything> Suffix(Rule<NodeWithEverything> last, String suffix);

    Rule<NodeWithEverything> Prefix(Rule<NodeWithEverything> suffix);

    Rule<NodeWithEverything> NodeList(List<Rule<NodeWithEverything>> children);

    Rule<NodeWithEverything> Empty();
}
