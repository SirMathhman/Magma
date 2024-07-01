package magma.build.compile.parse.rule.filter;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;

public record FilterRule(Rule child, Filter filter) implements Rule {
    private ParsingResult toNode0(String input) {
        if (filter.filter(input)) return Rules.toNode(child, input);
        return new ErrorParsingResult(new CompileError("Invalid filter: " + filter.computeMessage(), input));
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}
