package magma.build.compile.parse.rule.filter;

import magma.api.result.Result;
import magma.build.compile.error.CompileError;
import magma.build.compile.error.CompileParentError;
import magma.build.compile.error.Error_;
import magma.build.compile.error.TimeoutError;
import magma.build.compile.parse.Node;
import magma.build.compile.parse.result.ErrorParsingResult;
import magma.build.compile.parse.result.ParsingResult;
import magma.build.compile.parse.rule.Rule;
import magma.build.compile.parse.rule.Rules;

public record FilterRule(Rule child, Filter filter) implements Rule {
    @Override
    public Result<String, Error_> fromNode(Node node) {
        return child.fromNode(node);
    }

    @Override
    public ParsingResult toNode(String input) {
        if (!filter.filter(input)) {
            var format = "Invalid filter: %s";
            var message = format.formatted(filter.computeMessage());
            return new ErrorParsingResult(new CompileError(message, input));
        }

        var result = Rules.toNode(child, input);

        var errorOptional = result.findError();
        if (errorOptional.isPresent()) {
            var error = errorOptional.orElsePanic();
            var duration = error.findDuration();
            if (duration.isPresent()) {
                return result.mapErr(err -> new CompileParentError("Failed to apply filter.", input, err));
            }
        }

        var durationOptional = result.findDuration();
        if (!durationOptional.isPresent()) return result;

        var duration = durationOptional.orElsePanic();
        if (duration.compareTo(Rules.DEFAULT_TIMEOUT) <= 0) return result;
        return new ErrorParsingResult(new TimeoutError(input, duration));
    }
}
