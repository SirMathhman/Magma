package magma.build.compile.parse.rule.text;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.Error_;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;

public record LeftRule(String slice, Rule child) implements Rule {

    private ParsingResult toNode0(String input) {
        if (input.startsWith(slice)) {
            var content = input.substring(slice.length());
            return Rules.toNode(child, content);
        } else {
            return new ErrorParsingResult(new CompileError(String.format("Input does not start with '%s'.", slice), input));
        }
    }

    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node).mapValue(inner -> slice + inner);
    }

    @Override
    public ParsingResult toNode(String input) {
        return toNode0(input);
    }
}