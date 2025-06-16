package magma.app.compile.lang.build;

import magma.app.compile.error.NodeResult;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder<Error> {
    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> String(String value);

    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> Strip(Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> rule);

    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> Last(Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> parent, String infix, Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> child);

    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> Suffix(Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> last, String suffix);

    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> Prefix(Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> suffix);

    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> NodeList(List<Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>>> children);

    Rule<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> Empty();
}
