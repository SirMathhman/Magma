package magma.app.compile.lang.build;

import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder<Node, NodeResult, StringResult> {
    Rule<Node, NodeResult, StringResult> String(String value);

    Rule<Node, NodeResult, StringResult> Strip(Rule<Node, NodeResult, StringResult> rule);

    Rule<Node, NodeResult, StringResult> Last(Rule<Node, NodeResult, StringResult> parent, String infix, Rule<Node, NodeResult, StringResult> child);

    Rule<Node, NodeResult, StringResult> Suffix(Rule<Node, NodeResult, StringResult> last, String suffix);

    Rule<Node, NodeResult, StringResult> Prefix(Rule<Node, NodeResult, StringResult> suffix);

    Rule<Node, NodeResult, StringResult> NodeList(List<Rule<Node, NodeResult, StringResult>> children);

    Rule<Node, NodeResult, StringResult> Empty();
}
