package magma.app.compile.error;

import magma.api.result.Result;

public interface ResultFactory<Node, Error> {
    Result<Node, Error> fromStringErr(String message, String input);

    Result<String, Error> fromNodeErr(String message, Node node);

    Result<Node, Error> fromNode(Node value);

    Result<String, Error> fromString(String value);
}
