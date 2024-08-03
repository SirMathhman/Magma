package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public interface Rule {
    RuleResult<Node, ParseError> parse(String input);

    RuleResult<String, GenerateError> generate(Node node);
}
