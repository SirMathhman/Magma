package magma;

import magma.api.Result;

public interface Rule {
    Result<Node, ParseException> parse(String input);

    Result<String, GenerateException> generate(Node node);
}
