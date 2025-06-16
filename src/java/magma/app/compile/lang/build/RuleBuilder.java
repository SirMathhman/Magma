package magma.app.compile.lang.build;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder<Error> {
    Rule<NodeWithEverything, Error> String(String value);

    Rule<NodeWithEverything, Error> Strip(Rule<NodeWithEverything, Error> rule);

    Rule<NodeWithEverything, Error> Last(Rule<NodeWithEverything, Error> parent, String infix, Rule<NodeWithEverything, Error> child);

    Rule<NodeWithEverything, Error> Suffix(Rule<NodeWithEverything, Error> last, String suffix);

    Rule<NodeWithEverything, Error> Prefix(Rule<NodeWithEverything, Error> suffix);

    Rule<NodeWithEverything, Error> NodeList(List<Rule<NodeWithEverything, Error>> children);

    Rule<NodeWithEverything, Error> Empty();
}
