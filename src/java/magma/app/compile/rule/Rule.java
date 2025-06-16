package magma.app.compile.rule;

import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;

public interface Rule<Node, Error> {
    NodeResult<Node, Error> lex(String input);

    StringResult<Error> generate(Node node);
}
