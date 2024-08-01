package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;

public interface Rule {
    RuleResult<Node, ParseException> parse(String input);

    RuleResult<String, GenerateException> generate(Node node);
}
