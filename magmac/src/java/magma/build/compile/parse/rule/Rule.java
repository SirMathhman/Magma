package magma.build.compile.parse.rule;

import magma.api.result.Result;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ParsingResult;

/**
 * The Rule interface provides methods for converting between strings
 * and nodes. It includes methods to transform an input string into a node
 * and to transform a node back into a string.
 */
public interface Rule {
    ParsingResult toNode(String input);

    Result<String, Error_> fromNode(Node node);
}