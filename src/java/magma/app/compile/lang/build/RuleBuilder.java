package magma.app.compile.lang.build;

import magma.app.compile.error.StringResult;
import magma.app.compile.rule.Rule;

import java.util.List;

public interface RuleBuilder<Node, Error, NodeResult> {
    Rule<Node, NodeResult, StringResult<Error>> String(String value);

    Rule<Node, NodeResult, StringResult<Error>> Strip(Rule<Node, NodeResult, StringResult<Error>> rule);

    Rule<Node, NodeResult, StringResult<Error>> Last(Rule<Node, NodeResult, StringResult<Error>> parent, String infix, Rule<Node, NodeResult, StringResult<Error>> child);

    Rule<Node, NodeResult, StringResult<Error>> Suffix(Rule<Node, NodeResult, StringResult<Error>> last, String suffix);

    Rule<Node, NodeResult, StringResult<Error>> Prefix(Rule<Node, NodeResult, StringResult<Error>> suffix);

    Rule<Node, NodeResult, StringResult<Error>> NodeList(List<Rule<Node, NodeResult, StringResult<Error>>> children);

    Rule<Node, NodeResult, StringResult<Error>> Empty();
}
