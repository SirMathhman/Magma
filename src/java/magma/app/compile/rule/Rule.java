package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;

public interface Rule<Node> {
    Result<Node, FormattedError> lex(String input);

    Result<String, FormattedError> generate(Node node);
}
