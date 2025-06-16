package magma.app.compile.lang.build;

import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder<Node, Error, NodeResult> {
    Rule<Node, Error, NodeResult> String(String value);

    Rule<Node, Error, NodeResult> Strip(Rule<Node, Error, NodeResult> rule);

    Rule<Node, Error, NodeResult> Last(Rule<Node, Error, NodeResult> parent, String infix, Rule<Node, Error, NodeResult> child);

    Rule<Node, Error, NodeResult> Suffix(Rule<Node, Error, NodeResult> last, String suffix);

    Rule<Node, Error, NodeResult> Prefix(Rule<Node, Error, NodeResult> suffix);

    Rule<Node, Error, NodeResult> NodeList(List<Rule<Node, Error, NodeResult>> children);

    Rule<Node, Error, NodeResult> Empty();
}
