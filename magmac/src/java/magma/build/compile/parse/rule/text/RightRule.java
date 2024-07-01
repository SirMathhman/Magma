package magma.build.compile.parse.rule.text;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;

public record RightRule(Rule child, String slice) implements Rule {

    private ParsingResult toNode0(String input) {
        if (input.endsWith(slice)) {
            var contentEnd = input.length() - slice.length();
            var content = input.substring(0, contentEnd);
            return Rules.toNode(child, content);
        } else {
            return new ErrorParsingResult(new CompileError("Input does not end with '%s'.".formatted(slice), input));
        }
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node).mapValue(inner -> inner + slice);
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}