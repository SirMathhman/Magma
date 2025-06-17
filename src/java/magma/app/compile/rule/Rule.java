package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.node.Node;

public interface Rule {
    Result<Node, FormattedError> lex(String input);

    Result<String, FormattedError> generate(Node node);
}
