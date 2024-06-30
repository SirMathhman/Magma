package magma.build.compile.rule;

import magma.api.result.Result;
import magma.build.compile.Error_;
import magma.build.compile.rule.result.RuleResult;

/**
 * The Rule interface provides methods for converting between strings
 * and nodes. It includes methods to transform an input string into a node
 * and to transform a node back into a string.
 */
public interface Rule {
    RuleResult toNode(String input);

    Result<String, Error_> fromNode(Node node);
}