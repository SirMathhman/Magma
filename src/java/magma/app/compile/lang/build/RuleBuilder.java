package magma.app.compile.lang.build;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder<Error, NodeResult> {
    Rule<NodeWithEverything, Error, NodeResult> String(String value);

    Rule<NodeWithEverything, Error, NodeResult> Strip(Rule<NodeWithEverything, Error, NodeResult> rule);

    Rule<NodeWithEverything, Error, NodeResult> Last(Rule<NodeWithEverything, Error, NodeResult> parent, String infix, Rule<NodeWithEverything, Error, NodeResult> child);

    Rule<NodeWithEverything, Error, NodeResult> Suffix(Rule<NodeWithEverything, Error, NodeResult> last, String suffix);

    Rule<NodeWithEverything, Error, NodeResult> Prefix(Rule<NodeWithEverything, Error, NodeResult> suffix);

    Rule<NodeWithEverything, Error, NodeResult> NodeList(List<Rule<NodeWithEverything, Error, NodeResult>> children);

    Rule<NodeWithEverything, Error, NodeResult> Empty();
}
