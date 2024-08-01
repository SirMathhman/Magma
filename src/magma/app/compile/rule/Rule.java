package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Result;

public interface Rule {
    Result<Node, ParseException> parse(String input);

    Result<String, GenerateException> generate(Node node);
}
